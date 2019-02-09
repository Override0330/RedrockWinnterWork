package com.redrockwork.overrdie.firstdemo.xiandu;

import android.graphics.Bitmap;

public class XianduNews {
    private String image;
    private String title;
    private String time;
    private String url;

    public XianduNews(String image, String title, String time, String url) {
        this.image = image;
        this.title = title;
        this.time = time;
        this.url = url;
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
