package com.redrockwork.overrdie.firstdemo.bihu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class BihuSquareFragment extends Fragment {
    private View bihuSquareView;
    private ArrayList<BihuQuestion> bihuQuestionArrayList = new ArrayList<>();
    private RecyclerView squareRecyclerView;
    private static final int SETRECYCLERVIEWADAPTER = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SETRECYCLERVIEWADAPTER:
                    initRecyclerView();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bihuSquareView = inflater.inflate(R.layout.bihu_square_fragment,container,false);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                    Log.d(TAG, "run: 使用默认账号登录");
                    Log.d(TAG, "run: 此时token"+BihuFragment.nowUser.getToken());
                    bihuQuestionArrayList = BihuPostTools.initQuestionData(BihuFragment.nowUser.getToken(),"","");
                    Message message = new Message();
                    message.what = SETRECYCLERVIEWADAPTER;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d(TAG, "onCreateView: 完成广场的view加载");
        return bihuSquareView;
    }

    private void initRecyclerView(){
        squareRecyclerView = bihuSquareView.findViewById(R.id.rv_bihu_square);
        BihuInitQuestionHelper bihuInitQuestionHelper = new BihuInitQuestionHelper(bihuQuestionArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        squareRecyclerView.setLayoutManager(linearLayoutManager);
        squareRecyclerView.setAdapter(bihuInitQuestionHelper);
    }
}
