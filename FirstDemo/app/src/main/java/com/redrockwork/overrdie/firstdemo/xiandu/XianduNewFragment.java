package com.redrockwork.overrdie.firstdemo.xiandu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class XianduNewFragment extends Fragment {
    private View xianduNewsFragment;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        xianduNewsFragment = inflater.inflate(R.layout.xiandu_news_fragment,container,false);
        final XianduNewsInitHelper xianduNewsInitHelper = new XianduNewsInitHelper(getContext());
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    xianduNewsInitHelper.initData("qdaily");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return xianduNewsFragment;
    }

    private void setRecyclerViewAdapter(){

    }
}
