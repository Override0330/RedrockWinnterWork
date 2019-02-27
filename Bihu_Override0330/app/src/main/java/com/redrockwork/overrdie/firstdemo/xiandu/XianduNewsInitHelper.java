package com.redrockwork.overrdie.firstdemo.xiandu;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.MyImageTools;
import com.redrockwork.overrdie.firstdemo.developtools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class XianduNewsInitHelper extends RecyclerView.Adapter<XianduNewsInitHelper.ViewHolder> implements View.OnClickListener{
    private ArrayList<XianduNews> xianduNewsArrayList = new ArrayList<>();
    private Context context;
    private String categories;
    private android.os.Handler mainHandler;


    public XianduNewsInitHelper(ArrayList<XianduNews> xianduNewsArrayList, Context context, Handler mainHandler) {
        this.xianduNewsArrayList = xianduNewsArrayList;
        this.context = context;
        this.mainHandler = mainHandler;
    }

    public ArrayList<XianduNews> getXianduNewsArrayList() {
        return xianduNewsArrayList;
    }



    /**
     * RecyclerView的Adapter部分
     * @param viewHolder
     * @param i
     */

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final XianduNews xianduNews = xianduNewsArrayList.get(i);
        viewHolder.title.setText(xianduNews.getTitle());
        viewHolder.time.setText(xianduNews.getTime());
        viewHolder.titleImage.setImageDrawable(xianduNews.getImageView().getDrawable());
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
        viewHolder.titleImage.startAnimation(animation);
        viewHolder.itemView.setTag(i);
        final String url = xianduNews.getImage();
        if (url.equals("null")||url.equals("none")){

        }else {
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: 开始加载"+xianduNews.getTitle()+"的图片 url:"+url);
                    final Bitmap bitmap = MyImageTools.getBitmap(url);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (bitmap.getHeight()>viewHolder.title.getHeight()+150){
                                viewHolder.titleImage.setImageBitmap(bitmap);
                                Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
                                viewHolder.titleImage.startAnimation(animation);
                            }
                        }
                    });
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.xiandu_news_item,viewGroup,false);
        ViewHolder viewHodler = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHodler;
    }

    @Override
    public int getItemCount() {
        return xianduNewsArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView titleImage;
        TextView title,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleImage = itemView.findViewById(R.id.iv_xiandu_title);
            title = itemView.findViewById(R.id.tv_xiandu_title);
            time = itemView.findViewById(R.id.tv_xiandu_time);
        }
    }

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
