package com.redrockwork.overrdie.firstdemo.bihu;

import android.widget.ImageView;

import java.util.ArrayList;

public class BihuQuestion {
    private String id,title,content,time,exting,naive,comment,imageUrl,recent,
            authorName,authorAvatar,is_exciting,is_navie,is_favorite,abstractContent;
    private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();

    public BihuQuestion(String id, String title, String content, String time, String exting, String naive,
                        String comment, String imageUrl, String recent, String authorName, String authorAvatar,
                        String is_exciting, String is_navie, String is_favorite) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.exting = exting+" 赞";
        this.naive = naive+" 踩";
        this.comment = comment+" 回答";
        this.imageUrl = imageUrl;
        this.recent = recent;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.is_exciting = is_exciting;
        this.is_navie = is_navie;
        this.is_favorite = is_favorite;
        this.abstractContent = content.substring(0,50)+"......";
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getExting() {
        return exting;
    }

    public String getNaive() {
        return naive;
    }

    public String getComment() {
        return comment;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
