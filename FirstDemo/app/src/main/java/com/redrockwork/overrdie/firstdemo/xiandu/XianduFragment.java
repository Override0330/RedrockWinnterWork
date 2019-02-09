package com.redrockwork.overrdie.firstdemo.xiandu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.networktools.HttpsRequestHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class XianduFragment extends Fragment {
    private View gankView;
    private ArrayList<Categories> categoriesArrayList = new ArrayList<>();
    private TabLayout xianduTablayout;
    public static final int SET_TABLAYOUT = 0;
    public static final int INIT_XIANDU = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_TABLAYOUT:
                    xianduTablayout.removeAllTabs();
                    xianduTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    for (int i = 0; i < categoriesArrayList.size(); i++) {
                        xianduTablayout.addTab(xianduTablayout.newTab().setText(categoriesArrayList.get(i).getName()));
                    }
                    break;
                case INIT_XIANDU:
                break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gankView = inflater.inflate(R.layout.xiandu_fragment,container,false);
        final XianduNewsInitHelper xianduNewsInitHelper = new XianduNewsInitHelper(this.getContext());
        xianduTablayout = MainActivity.mTabLayout;
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initSpinnerData();
                    Message message = new Message();
                    message.what = SET_TABLAYOUT;
                    handler.sendMessage(message);
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
        
        return gankView;
    }

    public void initSpinnerData() throws JSONException, TimeoutException, IOException{
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/xiandu/categories");
        String jsonCategories = httpsRequestHelper.start().getJson();

        JSONObject jsonObject = new JSONObject(jsonCategories);
        String result = jsonObject.get("results").toString();
        JSONArray jsonArray = new JSONArray(result);
        for (int i = 0; i < jsonArray.length(); i++) {
            String item = jsonArray.get(i).toString();
            JSONObject itemJson = new JSONObject(item);
            categoriesArrayList.add(new Categories(itemJson.getString("name"),itemJson.getString("en_name")));
        }
    }
}





class Categories{
    String name;
    String en_name;

    public Categories(String name, String en_name) {
        this.name = name;
        this.en_name = en_name;
    }

    public String getName() {
        return name;
    }

    public String getEn_name() {
        return en_name;
    }
}
