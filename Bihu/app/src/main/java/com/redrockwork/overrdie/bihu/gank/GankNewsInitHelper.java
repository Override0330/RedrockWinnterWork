package com.redrockwork.overrdie.bihu.gank;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class GankNewsInitHelper extends RecyclerView.Adapter<GankNewsInitHelper.ViewHolder> implements View.OnClickListener{
    private ArrayList<News> news = new ArrayList<>();
    private Context context;
    private android.os.Handler mainHandler;

    public ArrayList<News> getNews() {
        return news;
    }


    public GankNewsInitHelper(ArrayList<News> news, Context context, Handler mainHandler) {
        this.news = news;
        this.context = context;
        this.mainHandler = mainHandler;
    }

    /**
     * RecyclerViewAdapter相关方法
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView desc,author,time;
        ImageView images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_gank_news_desc);
            author = itemView.findViewById(R.id.tv_gank_news_author);
            time = itemView.findViewById(R.id.tv_gank_news_time);
            images = itemView.findViewById(R.id.iv_gank_news_image);
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final News gankNews = news.get(i);
        viewHolder.desc.setText(gankNews.getDesc());
        viewHolder.author.setText(gankNews.getWho());
        viewHolder.time.setText(gankNews.getTime());
        viewHolder.images.setImageResource(R.drawable.ganknews);
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
        viewHolder.images.startAnimation(animation);
        viewHolder.itemView.setTag(i);
        if (gankNews.getImage()!=null)
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = MyImageTools.getBitmap(gankNews.getImage().get(0));
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: 加载"+gankNews.getDesc()+"的张图片");
                            viewHolder.images.setImageBitmap(bitmap);
                            //设置动画效果!
                            Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
                            viewHolder.images.startAnimation(animation);
                        }
                    });
                }
            });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }



    /**
     * RecyclerViewItem的点击事件(接口回调
     */

    private OnItemClickListener OnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        this.OnItemClickListener = OnItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    @Override
    public void onClick(View v) {
        if (OnItemClickListener!=null){
            OnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

}
