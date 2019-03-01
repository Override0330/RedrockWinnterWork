package com.redrockwork.overrdie.bihu.gank;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redrockwork.overrdie.bihu.BrowserPageActivity;
import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.bihu.developtools.Recall;
import com.redrockwork.overrdie.bihu.developtools.RecyclerViewMyLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class GankNewsFragment extends Fragment {
    private View NewsView;
    public static final int INIT_RECYCLER_VIEW = 0;
    public static final int UPLOAD_IMAGE = 1;
    private GankNewsInitHelper gankNewsInitHelper;
    private RecyclerViewMyLinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler mainHandler = new Handler();
    private ArrayList<News> newsArrayList = new ArrayList<>();
    private Context context;
    private String tab;

    public static GankNewsFragment newInstant(String tab) {
        GankNewsFragment gankNewsFragment = new GankNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        gankNewsFragment.setArguments(bundle);
        return gankNewsFragment;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_RECYCLER_VIEW:
                    swipeRefreshLayout.setRefreshing(false);
                    gankNewsInitHelper = new GankNewsInitHelper(newsArrayList, context, mainHandler);
                    RecyclerView recyclerView = NewsView.findViewById(R.id.rl_gank_news);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(gankNewsInitHelper);
                    gankNewsInitHelper.setOnItemClickListener(new GankNewsInitHelper.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(), BrowserPageActivity.class);
                            intent.putExtra("url", gankNewsInitHelper.getNews().get(position).getUrl());
                            startActivity(intent);
                        }
                    });
                    break;
                case UPLOAD_IMAGE:
                    try {
                        gankNewsInitHelper.getNews().get(msg.arg1).getImages().get(msg.arg2).setImageBitmap((Bitmap) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NewsView = inflater.inflate(R.layout.gank_news_fragment, container, false);
        swipeRefreshLayout = NewsView.findViewById(R.id.sr_gank_news);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSkyBlue));
        Bundle bundle = getArguments();
        tab = bundle.getString("tab");
        linearLayoutManager = new RecyclerViewMyLinearLayoutManager(this.getContext());
//        gankNewsInitHelper = new GankNewsInitHelper(tab,this.getContext(),handler);
        swipeRefreshLayout.setRefreshing(true);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    context = getContext();
                    newsArrayList = initData();
                    Message message = new Message();
                    message.what = INIT_RECYCLER_VIEW;
                    handler.sendMessage(message);
//                    for (int i = 0; i < gankNewsInitHelper.getNews().size(); i++) {
//                        for (int j = 0; j < gankNewsInitHelper.getNews().get(i).getImages().size(); j++) {
//                            Log.d(TAG, "run: 开始加载第"+i+"条新闻的第"+j+"个图片");
//                            Bitmap bitmap = MyImageTools.loderFromNetWork(gankNewsInitHelper.getNews().get(i).getImage().get(j));
//                            Message message2 = new Message();
//                            message2.what = UPLOAD_IMAGE;
//                            message2.arg1 = i;
//                            message2.arg2 = j;
//                            message2.obj = bitmap;
//                            handler.sendMessage(message2);
//                        }
//                    }
                } catch (UnknownHostException e) {
                    //网络超时则使用缓存的数据
                    try {
                        Log.d(TAG, "run: 储存的json:\n" + MainActivity.sharedPreferences.getString(tab, ""));
                        newsArrayList = new ArrayList<>();
                        newsArrayList = initDataWithoutNetWork();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = INIT_RECYCLER_VIEW;
                    handler.sendMessage(message);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    //网络超时则使用缓存的数据
                    try {
                        Log.d(TAG, "run: 储存的json:\n" + MainActivity.sharedPreferences.getString(tab, ""));
                        newsArrayList = initDataWithoutNetWork();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = INIT_RECYCLER_VIEW;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
        Log.d(TAG, "onCreateView: 完成newsView加载主进程");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            context = getContext();
                            newsArrayList = initData();
                            Message message = new Message();
                            message.what = INIT_RECYCLER_VIEW;
                            handler.sendMessage(message);
//                            for (int i = 0; i < newsArrayList.size(); i++) {
//                                for (int j = 0; j < newsArrayList.get(i).getImages().size(); j++) {
//                                    Log.d(TAG, "run: 开始加载第"+i+"条新闻的第"+j+"个图片");
//                                    Bitmap bitmap = MyImageTools.getBitmap(newsArrayList.get(i).getImage().get(j));
//                                    Message message2 = new Message();
//                                    message2.what = UPLOAD_IMAGE;
//                                    message2.arg1 = i;
//                                    message2.arg2 = j;
//                                    message2.obj = bitmap;
//                                    handler.sendMessage(message2);
//                                }
//                            }
                        } catch (UnknownHostException e) {
                            //网络超时则使用缓存的数据
                            try {
                                Log.d(TAG, "run: 储存的json:\n" + MainActivity.sharedPreferences.getString(tab, ""));
                                newsArrayList = initDataWithoutNetWork();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = INIT_RECYCLER_VIEW;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            //网络超时则使用缓存的数据
                            try {
                                newsArrayList = initDataWithoutNetWork();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = INIT_RECYCLER_VIEW;
                            handler.sendMessage(message);
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            //奇怪,用通用的Exception捕获不到 UnknownHostException 异常
                            e.printStackTrace();
                        } finally {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }
                });
            }
        });
        return NewsView;
    }

    /**
     * 新闻数据初始化相关方法
     *
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */

    public ArrayList<News> initData() throws JSONException, TimeoutException, IOException {
        ArrayList<News> news = new ArrayList<>();
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/data/" + tab + "/10/1");
        Recall recall = httpsRequestHelper.start();
        JSONObject mainJson = new JSONObject(recall.getJson());
        JSONArray resultJson = mainJson.getJSONArray("results");
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(720,
//                1080);
        for (int i = 0; i < resultJson.length(); i++) {
            JSONObject newsJson = resultJson.getJSONObject(i);
            String desc = newsJson.getString("desc");
            String time = newsJson.getString("publishedAt");
            JSONArray imagesJson;
            ArrayList<String> images = null;
            ArrayList<ImageView> imageList = new ArrayList<>();
            try {
                imagesJson = newsJson.getJSONArray("images");
                images = new ArrayList<>(imagesJson.length());
                for (int j = 0; j < imagesJson.length(); j++) {
                    images.add(imagesJson.getString(j));
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.wait);
                    imageList.add(imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = newsJson.getString("url");
            String who = newsJson.getString("who");
            news.add(new News(desc, images, url, who, time, imageList));
        }
        MainActivity.editor.putString(tab, recall.getJson());
        MainActivity.editor.commit();
        return news;
    }

    public ArrayList<News> initDataWithoutNetWork() throws JSONException {
        ArrayList<News> news = new ArrayList<>();
        JSONObject mainJson = new JSONObject(MainActivity.sharedPreferences.getString(tab, ""));
        JSONArray resultJson = mainJson.getJSONArray("results");
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(720,
//                1080);
        for (int i = 0; i < resultJson.length(); i++) {
            JSONObject newsJson = resultJson.getJSONObject(i);
            String desc = newsJson.getString("desc");
            String time = newsJson.getString("publishedAt");
            JSONArray imagesJson;
            ArrayList<String> images = null;
            ArrayList<ImageView> imageList = new ArrayList<>();
            try {
                imagesJson = newsJson.getJSONArray("images");
                images = new ArrayList<>(imagesJson.length());
                for (int j = 0; j < imagesJson.length(); j++) {
                    images.add(imagesJson.getString(j));
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.wait);
                    imageList.add(imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String url = newsJson.getString("url");
            String who = newsJson.getString("who");
            news.add(new News(desc, images, url, who, time, imageList));
        }
        return news;
    }
}
