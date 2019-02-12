package com.redrockwork.overrdie.firstdemo.xiandu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.MyViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class XianduFragment extends Fragment {
    private View xianduView;
    private ArrayList<Categories> categoriesArrayList = new ArrayList<>();
    private ArrayList<String> titleArrayList = new ArrayList<>();
    private ArrayList<Fragment> xianduFragments = new ArrayList<>();
    private TabLayout xianduTablayout;
    private ViewPager xianduViewPager;
    public static final int SET_TABLAYOUT = 0;
    public static final int INIT_XIANDU = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SET_TABLAYOUT:
                    initTabLayout();
                    break;
                case INIT_XIANDU:

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        xianduView = inflater.inflate(R.layout.xiandu_main_fragment,container,false);
        xianduViewPager = xianduView.findViewById(R.id.vp_xiandu);
//        final XianduNewsInitHelper xianduNewsInitHelper = new XianduNewsInitHelper(this.getContext());
        xianduTablayout = MainActivity.mTabLayout;
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initTabLayoutData();
                    Message message = new Message();
                    message.what = SET_TABLAYOUT;
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
        
        return xianduView;
    }

    public void initTabLayoutData() throws JSONException, TimeoutException, IOException{
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
    private void initTabLayout(){
        xianduTablayout.removeAllTabs();
        xianduTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < categoriesArrayList.size(); i++) {
            String temp = categoriesArrayList.get(i).getName();
            titleArrayList.add(temp);
            xianduTablayout.addTab(xianduTablayout.newTab().setText(temp));
            XianduNewsFragment xianduNewsFragment = XianduNewsFragment.newIntance(categoriesArrayList.get(i).getEn_name());
            xianduFragments.add(xianduNewsFragment);
        }
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(),xianduFragments,titleArrayList);
        xianduTablayout.setupWithViewPager(xianduViewPager);
        xianduViewPager.setAdapter(myViewPagerAdapter);
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
