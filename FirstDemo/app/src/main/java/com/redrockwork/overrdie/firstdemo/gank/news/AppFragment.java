package com.redrockwork.overrdie.firstdemo.gank.news;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.BrowserPageActivity;
import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.gank.GankNewsInitHelper;
import com.redrockwork.overrdie.firstdemo.networktools.MyImageLoder;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AppFragment extends Fragment {
    private View NewsView;
    public static final int INITRECYCLERVIEW = 0;
    public static final int UPLOADIMAGE = 1;
    private GankNewsInitHelper gankNewsInitHelper;
    private LinearLayoutManager linearLayoutManager;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INITRECYCLERVIEW:
                    RecyclerView recyclerView = NewsView.findViewById(R.id.rl_gank_app);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(gankNewsInitHelper);
                    gankNewsInitHelper.setOnItemClickListenr(new GankNewsInitHelper.OnItemClickListenr() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getContext(),BrowserPageActivity.class);
                            intent.putExtra("url", gankNewsInitHelper.getNews().get(position).getUrl());
                            startActivity(intent);
                        }
                    });
                    break;
                case UPLOADIMAGE:
                    gankNewsInitHelper.getNews().get(msg.arg1).getImages().get(msg.arg2).setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NewsView = inflater.inflate(R.layout.gank_app_fragment,container,false);
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        gankNewsInitHelper = new GankNewsInitHelper("App",this.getContext());
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    gankNewsInitHelper.initData();
                    Message message = new Message();
                    message.what = INITRECYCLERVIEW;
                    handler.sendMessage(message);
                    for (int i = 0; i < gankNewsInitHelper.getNews().size(); i++) {
                        for (int j = 0; j < gankNewsInitHelper.getNews().get(i).getImages().size(); j++) {
                            Bitmap bitmap = MyImageLoder.loderFromNetWork(gankNewsInitHelper.getNews().get(i).getImage().get(j));
                            Message message2 = new Message();
                            message2.what = UPLOADIMAGE;
                            message2.arg1 = i;
                            message2.arg2 = j;
                            message2.obj = bitmap;
                            handler.sendMessage(message2);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return NewsView;
    }
}
