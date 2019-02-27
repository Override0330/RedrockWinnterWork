package com.redrockwork.overrdie.firstdemo.bihu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.MyViewPagerAdapter;
import java.util.ArrayList;

public class BihuFragment extends Fragment {
    private View bihuView;
    private String [] tab = {"问答广场","我的收藏"};
    private TabLayout bihuTablayout;
    public static ArrayList<Fragment> bihuFragments = new ArrayList<>();
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
        MainActivity.title.setText("");
        bihuViewPager = bihuView.findViewById(R.id.vp_bihu);
        bihuTablayout = MainActivity.mTabLayout;
        bihuTablayout.removeAllTabs();
        for (int i = 0; i < tab.length;i++) {
            titles.add(tab[i]);
            bihuTablayout.addTab(bihuTablayout.newTab().setText(tab[i]));
        }
        if (bihuFragments.size()==2){
            //无操作
        }else if (bihuFragments.size()==0){
            bihuFragments.add(new BihuSquareFragment());
            bihuFragments.add(new BihuAboutMeFragment());
        }else {
            bihuFragments.clear();
            bihuFragments.add(new BihuSquareFragment());
            bihuFragments.add(new BihuAboutMeFragment());
        }

        bihuTablayout.setTabMode(TabLayout.MODE_FIXED);
        FragmentManager fragmentManager = getChildFragmentManager();
        MyViewPagerAdapter bihuViewPagerAdapter = new MyViewPagerAdapter(fragmentManager,bihuFragments,titles);
        bihuTablayout.setupWithViewPager(bihuViewPager);
        bihuViewPager.setAdapter(bihuViewPagerAdapter);

    }
}
