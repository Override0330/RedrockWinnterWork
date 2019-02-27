package com.redrockwork.overrdie.firstdemo.xiandu;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.redrockwork.overrdie.firstdemo.R;

import java.util.ArrayList;

public class XianduNews {
    private String image;
    private String title;
    private String time;
    private String url;
    private ImageView imageView;

    public XianduNews(String image, String title, String time, String url,ImageView imageView) {
        this.image = image;
        this.title = title;
        this.time = time.split("T")[0];
        this.url = url;
        this.imageView = imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
