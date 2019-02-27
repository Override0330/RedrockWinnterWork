package com.redrockwork.overrdie.bihu.developtools;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    ArrayList<String> title;


    public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, ArrayList<String> title) {
        super(fm);
        this.fragments = fragments;
        this.title = title;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}
