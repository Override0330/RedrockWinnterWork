package com.redrockwork.overrdie.firstdemo.gank;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.networktools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.networktools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class GankNewsInitHelper extends RecyclerView.Adapter<GankNewsInitHelper.ViewHolder> implements View.OnClickListener{
    private ArrayList<News> news = new ArrayList<>();
    private Context context;
    private String category;

    public void setOnItemClickListenr(OnItemClickListenr onItemClickListenr) {
        this.onItemClickListenr = onItemClickListenr;
    }

    private OnItemClickListenr onItemClickListenr = null;

    public ArrayList<News> getNews() {
        return news;
    }

    public GankNewsInitHelper(String category, Context context) {
        this.category = category;
        this.context = context;
    }


    /**
     * RecyclerViewAdapter相关方法
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView desc,author,time;
        LinearLayout images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_gank_news_desc);
            author = itemView.findViewById(R.id.tv_gank_news_author);
            time = itemView.findViewById(R.id.tv_gank_news_time);
            images = itemView.findViewById(R.id.ll_gank_news_images);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gank_news_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        News gankNews = news.get(i);
        viewHolder.images.removeAllViews();
        viewHolder.desc.setText(gankNews.getDesc());
        viewHolder.author.setText(gankNews.getWho());
        viewHolder.time.setText(gankNews.getTime());
        ImageView imageView;
        try{
            for (int j = 0; j < gankNews.getImage().size(); j++) {
                imageView = gankNews.getImages().get(j);
                viewHolder.images.addView(imageView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    /**
     * 新闻数据初始化相关方法
      * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */

    public void initData() throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/data/"+category+"/10/1");
        Recall recall = httpsRequestHelper.start();
        JSONObject mainJson = new JSONObject(recall.getJson());
        JSONArray resultJson = mainJson.getJSONArray("results");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(720,
                1080);
        for (int i = 0; i < resultJson.length(); i++) {
            JSONObject newsJson = resultJson.getJSONObject(i);
            String desc = newsJson.getString("desc");
            String time = newsJson.getString("publishedAt");
            JSONArray imagesJson = null;
            ArrayList<String> images = null;
            ArrayList<ImageView> imageList = new ArrayList<>();
            try{
                imagesJson = newsJson.getJSONArray("images");
                images = new ArrayList<>(imagesJson.length());
                for (int j = 0; j < imagesJson.length(); j++) {
                    images.add(imagesJson.getString(j));
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.wait);
                    imageView.setLayoutParams(params);
                    imageList.add(imageView);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            String url = newsJson.getString("url");
            String who = newsJson.getString("who");
            news.add(new News(desc,images,url,who,time,imageList));
        }
    }

    public static interface OnItemClickListenr {
        void onItemClick(View view,int position);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListenr!=null){
            onItemClickListenr.onItemClick(v,(int)v.getTag());
        }
    }

}
