package com.redrockwork.overrdie.firstdemo.xiandu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redrockwork.overrdie.firstdemo.BrowserPageActivity;
import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.MyImageTools;
import com.redrockwork.overrdie.firstdemo.developtools.Recall;
import com.redrockwork.overrdie.firstdemo.developtools.RecyclerViewMyLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class XianduNewsFragment extends Fragment {
    private View xianduNewsFragment;
    private Handler mainHandler = new Handler();
    private Context context;
    private RecyclerViewMyLinearLayoutManager linearLayoutManager;
    private XianduNewsInitHelper xianduNewsInitHelper;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String categories;
    private ArrayList<String> detailCategories = new ArrayList<>();
    private ArrayList<XianduNews> xianduNewsArrayList = new ArrayList<>();
    private static final int INITRECYCLERVIEW = 0;
    private static final int UPLOADIMAGE = 1;
    private static final int DELETEIMAGE = 2;
    private static final int STARTTOUPLOADIMAGE = 3;

    /**
     * 用于发送消息以回到主线程更新ui
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INITRECYCLERVIEW:
                    linearLayoutManager = new RecyclerViewMyLinearLayoutManager(getContext());
                    xianduNewsInitHelper = new XianduNewsInitHelper(xianduNewsArrayList,context,handler);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(xianduNewsInitHelper);
                    xianduNewsInitHelper.setOnItemClickListener(new XianduNewsInitHelper.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(),BrowserPageActivity.class);
                            intent.putExtra("url",xianduNewsInitHelper.getXianduNewsArrayList().get(position).getUrl());
                            startActivity(intent);
                        }
                    });
//                    Message message = new Message();
//                    message.what = STARTTOUPLOADIMAGE;
//                    handler.sendMessage(message);
                    break;
//                case UPLOADIMAGE:
//                    try{
//                        xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getImageView().setImageBitmap((Bitmap) msg.obj);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    Log.d(TAG, "handleMessage: "+categories+"分类的    "+xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getTitle()+"  新闻的图片加载");
//                    break;
//                case DELETEIMAGE:
//                    Log.d(TAG, "handleMessage: "+categories+"分类的    "+xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getTitle()+"  新闻图片为空");
//                    break;
//                case STARTTOUPLOADIMAGE:
//                    MainActivity.fixedThreadPool.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            starttouploadimage();
//                        }
//                    });
//                    break;
            }
        }
    };

    //继承Fragment后的自定义类不能添加自定义的构造方法,所以我们使用Bundle来传递参数
    public static XianduNewsFragment newIntance(String categories){
        XianduNewsFragment xianduNewsFragment = new XianduNewsFragment();
        Bundle args = new Bundle();
        args.putString("categories",categories);
        xianduNewsFragment.setArguments(args);
        return xianduNewsFragment;
    }

    /**
     * 创建新的fragment页面的具体步骤应该是,先加载文字,在返回整个视图之前再发送消息来加载图片
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        xianduNewsFragment = inflater.inflate(R.layout.xiandu_news_fragment,container,false);
        recyclerView = xianduNewsFragment.findViewById(R.id.xiandu_rv_news);
        //接收Bundle传递进来的参数.
        Bundle bundle = getArguments();
        //拿到主分类
        categories = bundle.getString("categories");
        swipeRefreshLayout = xianduNewsFragment.findViewById(R.id.sr_xiandu);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSkyBlue));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            context = getContext();
                            //通过主分类拿到子分类的所有id
                            initDetailCategories(categories);
                            initNewsData(detailCategories.get(1));
                            //特别注意,解决RecyclerView刷新时崩溃的问题
                            //不要直接替换Adapter中的List,会导致数据丢失,此时若滑动RecyclerView会导致数组越界从而崩溃
                            //使用一个中间值来做中介重新创建一个Adapter
                            xianduNewsArrayList = initNewsData(detailCategories.get(1));
                            Message message = new Message();
                            message.what = INITRECYCLERVIEW;
                            handler.sendMessage(message);
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    context = getContext();
                    initDetailCategories(categories);
                    xianduNewsArrayList = initNewsData(detailCategories.get(1));
                    //等待所有数据加载完毕
//                    while (xianduNewsInitHelper.getXianduNewsArrayList().size() != detailCategories.size()){
//                        Thread.sleep(20);
//                    }
                    Message message = new Message();
                    message.what = INITRECYCLERVIEW;
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
        return xianduNewsFragment;
    }
    private void initDetailCategories(String categories) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/xiandu/category/"+categories);
        Recall recall = httpsRequestHelper.start();
        JSONObject mainJson = new JSONObject(recall.getJson());
        JSONArray results = new JSONArray(mainJson.getString("results"));
        detailCategories.clear();
        for (int i = 0; i < results.length(); i++) {
            JSONObject detailJson = new JSONObject(results.get(i).toString());
            String detailCategory = detailJson.getString("id");
            detailCategories.add(detailCategory);
        }
    }


//    private void starttouploadimage(){
//        for (int i = 0; i < xianduNewsInitHelper.getXianduNewsArrayList().size(); i++) {
//            XianduNews xianduNews = xianduNewsInitHelper.getXianduNewsArrayList().get(i);
//            String imageUrl = xianduNews.getImage();
//            Log.d(TAG, "run: 解析到"+categories+"分类的   "+xianduNews.getTitle()+"   新闻的图片url为\n"+imageUrl);
//            if(imageUrl.equals("none")||imageUrl.equals("null")){
//                Message messageDeleteImage = new Message();
//                messageDeleteImage.what = DELETEIMAGE;
//                messageDeleteImage.arg1 = 1;
//                handler.sendMessage(messageDeleteImage);
//            }else {
//                Bitmap bitmap = MyImageTools.getBitmap(imageUrl);
//                Message messageImage = new Message();
//                messageImage.what = UPLOADIMAGE;
//                messageImage.arg1 = i;
//                messageImage.obj = bitmap;
//                handler.sendMessage(messageImage);
//            }
//        }
//    }
    /**
     * 这个方法用来初始化一个fragment的所有新闻数据,传递参数为detailCategories(闲读的具体子分类),加载全部分类遍历子分类id即可
     */
    public ArrayList<XianduNews> initNewsData(String detailCategories) throws JSONException {
        ArrayList<XianduNews> xianduNewsArrayList = new ArrayList<>();
        Log.i(TAG, "run: 开始网络请求"+detailCategories+"的数据,GET请求地址\n"+"http://gank.io/api/xiandu/data/id/"+detailCategories+"/count/10/page/1");
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/xiandu/data/id/"+detailCategories+"/count/10/page/1");
        Recall recall = null;
        try {
            recall = httpsRequestHelper.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String baseJson = recall.getJson();
        JSONObject baseJsonObject = new JSONObject(baseJson);
        JSONArray resultJson = new JSONArray(baseJsonObject.getString("results"));
        ImageView imageView = new ImageView(context);
        switch (categories){
            case "wow":
                imageView.setImageResource(R.drawable.wow);
                break;
            case "apps":
                imageView.setImageResource(R.drawable.apps);
                break;
            case "imrich":
                imageView.setImageResource(R.drawable.imrich);
                break;
            case "funny":
                imageView.setImageResource(R.drawable.funny);
                break;
            case "android":
                imageView.setImageResource(R.drawable.android);
                break;
            case "diediedie":
                imageView.setImageResource(R.drawable.diediedie_);
                break;
            case "thinking":
                imageView.setImageResource(R.drawable.thinking);
                break;
            case "iOS":
                imageView.setImageResource(R.drawable.ios);
                break;
            case "teamblog":
                imageView.setImageResource(R.drawable.teamblog);
                break;
        }

        for (int i = 0; i < resultJson.length(); i++) {
            JSONObject oneNewsJson = new JSONObject(resultJson.get(i).toString());
            String image = oneNewsJson.getString("cover");
            String time = oneNewsJson.getString("created_at");
            String title = oneNewsJson.getString("title");
            String url = oneNewsJson.getString("url");
            xianduNewsArrayList.add(new XianduNews(image,title,time,url,imageView));
        }
        return xianduNewsArrayList;
    }
}
