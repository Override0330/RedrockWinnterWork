package com.redrockwork.overrdie.firstdemo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    private View aboutFragment;
    private TextView jumpToGithub;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        aboutFragment = inflater.inflate(R.layout.about_fragment,container,false);
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.title.setText("关于");
        jumpToGithub = aboutFragment.findViewById(R.id.tv_jump_to_github);
        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.github);
        drawable.setBounds(0,0,40,40);
        jumpToGithub.setCompoundDrawables(null,null,drawable,null);
        jumpToGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),BrowserPageActivity.class);
                intent.putExtra("url","https://github.com/Override0330/RedrockWinnterWork");
                startActivity(intent);
            }
        });
        return aboutFragment;
    }
}
