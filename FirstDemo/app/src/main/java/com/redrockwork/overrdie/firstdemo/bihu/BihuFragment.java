package com.redrockwork.overrdie.firstdemo.bihu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.MyViewPagerAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class BihuFragment extends Fragment {
    private View bihuView;
    private String [] tab = {"问答广场","关于我的"};
    private TabLayout bihuTablayout;
    private ArrayList<Fragment> bihuFragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ViewPager bihuViewPager;
    public static User nowUser;
    public static String [] defaultUserInformation= new String[]{"temp","huanglong2019"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bihuView = inflater.inflate(R.layout.bihu_main_fragment,container,false);
        initTabLayout();
        return bihuView;
    }

    public void initTabLayout(){
        bihuViewPager = bihuView.findViewById(R.id.vp_bihu);
        bihuTablayout = MainActivity.mTabLayout;
        bihuTablayout.removeAllTabs();
        for (int i = 0; i < tab.length;i++) {
            titles.add(tab[i]);
            bihuTablayout.addTab(bihuTablayout.newTab().setText(tab[i]));
        }
        bihuFragments.add(new BihuSquareFragment());
        bihuFragments.add(new BihuAboutMeFragment());
        bihuTablayout.setTabMode(TabLayout.MODE_FIXED);
        FragmentManager fragmentManager = getChildFragmentManager();
        MyViewPagerAdapter bihuViewPagerAdapter = new MyViewPagerAdapter(fragmentManager,bihuFragments,titles);
        bihuTablayout.setupWithViewPager(bihuViewPager);
        bihuViewPager.setAdapter(bihuViewPagerAdapter);

    }
}
