package com.redrockwork.overrdie.firstdemo.gank;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.developtools.MyViewPagerAdapter;
import com.redrockwork.overrdie.firstdemo.R;

import java.util.ArrayList;

public class GankFragment extends Fragment {
    private View gankView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> newsFragments = new ArrayList();
    public static final int SET_SPINNER = 0;
    public static final int INIT_NEWS = 1;
    private String [] stringTitle = {"Android","iOS","前端","拓展资源","瞎推荐","App"};
    private ArrayList<String> titles = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gankView = inflater.inflate(R.layout.gank_main_fragment, container, false);
        initTabLayout();
        return gankView;
    }

    private void initTabLayout(){
        mTabLayout = MainActivity.mTabLayout;
        MainActivity.title.setText("");
        mViewPager = gankView.findViewById(R.id.gank_vp);
        for (int i = 0; i < stringTitle.length ;i++) {
            GankNewsFragment gankNewsFragment = GankNewsFragment.newInstant(stringTitle[i]);
            newsFragments.add(gankNewsFragment);
        }
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < stringTitle.length; i++) {
            titles.add(stringTitle[i]);
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(),newsFragments,titles);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(myViewPagerAdapter);
    }

}
