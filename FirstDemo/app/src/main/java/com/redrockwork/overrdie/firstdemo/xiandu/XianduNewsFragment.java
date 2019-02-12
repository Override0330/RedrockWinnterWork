package com.redrockwork.overrdie.firstdemo.xiandu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.BrowserPageActivity;
import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.MyImageLoder;
import com.redrockwork.overrdie.firstdemo.developtools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class XianduNewsFragment extends Fragment {
    private View xianduNewsFragment;
    private LinearLayoutManager linearLayoutManager;
    private XianduNewsInitHelper xianduNewsInitHelper;
    private RecyclerView recyclerView;
    private String categories;
    private ArrayList<String> detailCategories = new ArrayList<>();
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
                    linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(xianduNewsInitHelper);
                    xianduNewsInitHelper.setOnItemClick(new XianduNewsInitHelper.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(),BrowserPageActivity.class);
                            intent.putExtra("url",xianduNewsInitHelper.getXianduNewsArrayList().get(position).getUrl());
                            startActivity(intent);
                        }
                    });
                    Message message = new Message();
                    message.what = STARTTOUPLOADIMAGE;
                    handler.sendMessage(message);
                    break;
                case UPLOADIMAGE:
                    try{
                        xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getImageView().setImageBitmap((Bitmap) msg.obj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.d(TAG, "handleMessage: "+categories+"分类的    "+xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getTitle()+"  新闻的图片加载");
                    break;
                case DELETEIMAGE:
                    Log.d(TAG, "handleMessage: "+categories+"分类的    "+xianduNewsInitHelper.getXianduNewsArrayList().get(msg.arg1).getTitle()+"  新闻图片为空");
                    break;
                case STARTTOUPLOADIMAGE:
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            starttouploadimage();
                        }
                    });
                    break;
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
        //通过主分类拿到子分类的所有id
        xianduNewsInitHelper = new XianduNewsInitHelper(this.getContext(),categories);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initDetailCategories(categories);
                    for (int i = 0; i < detailCategories.size(); i++) {
                            xianduNewsInitHelper.initNewsData(detailCategories.get(i));
                    }

                    //等待所有数据加载完毕
                    while (xianduNewsInitHelper.getXianduNewsArrayList().size() != detailCategories.size()){
                        Thread.sleep(20);
                    }
                    Message message = new Message();
                    message.what = INITRECYCLERVIEW;
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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


    private void starttouploadimage(){
        for (int i = 0; i < xianduNewsInitHelper.getXianduNewsArrayList().size(); i++) {
            XianduNews xianduNews = xianduNewsInitHelper.getXianduNewsArrayList().get(i);
            String imageUrl = xianduNews.getImage();
            Log.d(TAG, "run: 解析到"+categories+"分类的   "+xianduNews.getTitle()+"   新闻的图片url为\n"+imageUrl);
            if(imageUrl.equals("none")||imageUrl.equals("null")){
                Message messageDeleteImage = new Message();
                messageDeleteImage.what = DELETEIMAGE;
                messageDeleteImage.arg1 = 1;
                handler.sendMessage(messageDeleteImage);
            }else {
                Bitmap bitmap = MyImageLoder.loderFromNetWork(imageUrl);
                Message messageImage = new Message();
                messageImage.what = UPLOADIMAGE;
                messageImage.arg1 = i;
                messageImage.obj = bitmap;
                handler.sendMessage(messageImage);
            }
        }
    }
}
