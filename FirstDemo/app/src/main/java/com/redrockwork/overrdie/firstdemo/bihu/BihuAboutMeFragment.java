package com.redrockwork.overrdie.firstdemo.bihu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redrockwork.overrdie.firstdemo.R;

public class BihuAboutMeFragment extends Fragment {
    private View bihuAboutMeView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bihuAboutMeView = inflater.inflate(R.layout.bihu_aboutme_fragment,container,false);
        return bihuAboutMeView;
    }
}
