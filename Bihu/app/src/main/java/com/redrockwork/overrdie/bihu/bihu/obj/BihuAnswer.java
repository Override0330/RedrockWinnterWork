package com.redrockwork.overrdie.bihu.bihu.obj;

import android.widget.ImageView;

import java.util.ArrayList;

public class BihuAnswer {
    private int id;
    private String content, imagesUrl, date, best, exciting, naive, isExciting, isNaive;
    private User author;
    private ArrayList<ImageView> imageViews = new ArrayList<>();

    public BihuAnswer(int id, String content, String imagesUrl, String date, String best
            , String exciting, String naive, String isExciting, String isNaive, User author
    ) {
        this.id = id;
        this.content = content;
        this.imagesUrl = imagesUrl;
        this.date = date;
        this.best = best;
        this.exciting = exciting;
        this.naive = naive;
        this.isExciting = isExciting;
        this.isNaive = isNaive;
        this.author = author;
    }

    public void setBest(String best) {
        this.best = best;
    }


    public ArrayList<ImageView> getImageViews() {
        return imageViews;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public String getDate() {
        return date;
    }

    public String getBest() {
        return best;
    }

    public String getExciting() {
        return exciting;
    }

    public String getNaive() {
        return naive;
    }

    public String getIsExciting() {
        return isExciting;
    }

    public String getIsNaive() {
        return isNaive;
    }

    public User getAuthor() {
        return author;
    }

    public void setExciting(String exciting) {
        this.exciting = exciting;
    }

    public void setNaive(String naive) {
        this.naive = naive;
    }

    public void setIsExciting(String isExciting) {
        this.isExciting = isExciting;
    }

    public void setIsNaive(String isNaive) {
        this.isNaive = isNaive;
    }
}
