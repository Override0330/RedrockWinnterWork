package com.redrockwork.overrdie.firstdemo.gank;

import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;

public class News {
    private String desc;
    private ArrayList<String> image = new ArrayList<>();
    private ArrayList<ImageView> imagesList = new ArrayList<>();
    private String url;
    private String who;
    private String time;

    public News(String desc, ArrayList<String> image, String url, String who, String time,ArrayList<ImageView> images) {
        this.desc = desc;
        this.image = image;
        this.url = url;
        this.who = who;
        this.time = time.split("T")[0];
        this.imagesList = images;
    }

    public ArrayList<ImageView> getImages() {
        return imagesList;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
