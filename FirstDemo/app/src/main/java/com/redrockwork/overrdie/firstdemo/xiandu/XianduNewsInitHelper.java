package com.redrockwork.overrdie.firstdemo.xiandu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class XianduNewsInitHelper extends RecyclerView.Adapter<XianduNewsInitHelper.ViewHolder> implements View.OnClickListener{
    private ArrayList<XianduNews> xianduNewsArrayList = new ArrayList<>();
    private Context context;
    private String categories;
    private OnItemClickListener onItemClick = null;

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public XianduNewsInitHelper(Context context, String categories) {
        this.context = context;
        this.categories = categories;
    }

    public ArrayList<XianduNews> getXianduNewsArrayList() {
        return xianduNewsArrayList;
    }

    /**
     * 这个方法用来初始化一个fragment的所有新闻数据,传递参数为detailCategories(闲读的具体子分类),加载全部分类遍历子分类id即可
     */
    public void initNewsData(String detailCategories) {
        final String temp = detailCategories;
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/xiandu/data/id/"+temp+"/count/1/page/1");
                    Recall recall = httpsRequestHelper.start();
                    String baseJson = recall.getJson();
                    JSONObject baseJsonObject = new JSONObject(baseJson);
                    JSONArray resultJson = new JSONArray(baseJsonObject.getString("results"));
                    ImageView imageView = new ImageView(context);
                    switch (categories){
                        case "wow":
                            imageView.setImageResource(R.drawable.wow);
                            break;
                        case "apps":
                            imageView.setImageResource(R.drawable.apps);
                            break;
                        case "imrich":
                            imageView.setImageResource(R.drawable.imrich);
                            break;
                        case "funny":
                            imageView.setImageResource(R.drawable.funny);
                            break;
                        case "android":
                            imageView.setImageResource(R.drawable.android);
                            break;
                        case "diediedie":
                            imageView.setImageResource(R.drawable.diediedie_);
                            break;
                        case "thinking":
                            imageView.setImageResource(R.drawable.thinking);
                            break;
                        case "iOS":
                            imageView.setImageResource(R.drawable.ios);
                            break;
                        case "teamblog":
                            imageView.setImageResource(R.drawable.teamblog);
                            break;
                    }

                    for (int i = 0; i < resultJson.length(); i++) {
                        JSONObject oneNewsJson = new JSONObject(resultJson.get(i).toString());
                        String image = oneNewsJson.getString("cover");
                        String time = oneNewsJson.getString("created_at");
                        String title = oneNewsJson.getString("title");
                        String url = oneNewsJson.getString("url");
                        xianduNewsArrayList.add(new XianduNews(image,title,time,url,imageView));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * RecyclerView的Adapter部分
     * @param viewHolder
     * @param i
     */

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        XianduNews xianduNews = xianduNewsArrayList.get(i);
        viewHolder.title.setText(xianduNews.getTitle());
        viewHolder.time.setText(xianduNews.getTime());
        ImageView imageView;
        imageView = xianduNews.getImageView();
        imageView.setAdjustViewBounds(true);
        try{
            viewHolder.titleImage.removeAllViews();
            viewHolder.titleImage.addView(imageView);
        }catch (Exception e){
            e.printStackTrace();
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
        LinearLayout titleImage;
        TextView title,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleImage = itemView.findViewById(R.id.ll_xiandu_title);
            title = itemView.findViewById(R.id.tv_xiandu_title);
            time = itemView.findViewById(R.id.tv_xiandu_time);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick!=null){
            onItemClick.onItemClick(v,(int)v.getTag());
        }
    }
}
