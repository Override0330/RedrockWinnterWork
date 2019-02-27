package com.redrockwork.overrdie.firstdemo.bihu;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.developtools.Time;

import java.io.Serializable;
import java.util.ArrayList;

public class BihuQuestion{
    private String id,title,content,time,exciting,naive,comment,imageUrl,recent,
            authorName,authorAvatar,is_exciting,is_navie,is_favorite,abstractContent;
    private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    private Bitmap bitmap;
    private int authorId;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public BihuQuestion(String id, String title, String content, String time, String exciting, String naive,
                        String comment, String imageUrl, String recent, String authorName, String authorAvatar,
                        String is_exciting, String is_navie, String is_favorite,int authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.exciting = exciting;
        this.naive = naive;
        this.comment = comment;
        this.imageUrl = imageUrl;
        if (!recent.equals("null")){
            String timeString = recent.split(" ")[0]+"/"+recent.split(" ")[1];
            Time time1 = new Time(timeString);
            long timeMillis = time1.dateChangeToTimeMillis();
            long nowTimeMillis = System.currentTimeMillis()/1000;
            long cha = nowTimeMillis-timeMillis;
            this.recent = "更新于"+count(cha)+"前";
        }else {
            this.recent = recent;
        }
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.is_exciting = is_exciting;
        this.is_navie = is_navie;
        this.is_favorite = is_favorite;
        this.authorId = authorId;
        if (content.getBytes().length<150){
            this.abstractContent = content;
        }else {
            this.abstractContent = content.substring(0,50)+"......";
        }
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public void setExciting(String exciting) {
        this.exciting = exciting;
    }

    public void setNaive(String naive) {
        this.naive = naive;
    }

    public void setIs_exciting(String is_exciting) {
        this.is_exciting = is_exciting;
    }

    public void setIs_navie(String is_navie) {
        this.is_navie = is_navie;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRecent() {
        return recent;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public String getIs_exciting() {
        return is_exciting;
    }

    public String getIs_navie() {
        return is_navie;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public ArrayList<ImageView> getImageViewArrayList() {
        return imageViewArrayList;
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

    public String getExciting() {
        return exciting;
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

    private String count(Long cha){
        Long s = cha;
        Long min = s/60;
        if (min==0){
            return "小于1分钟";
        }else if (min<=59){
            return min+"分钟";
        }
        Long h = min/60;
        min = min%60;
        if (h<2){
            return h+"小时"+min+"分钟";
        }else if (h<24){
            return h+"小时";
        }
        long d = h/24;
        if (d<29){
            return d+"天";
        }
        long month = d/30;
        if (month<12){
            return month+"个月";
        }
        long year = month/12;
        return year+"年";
    }
}
