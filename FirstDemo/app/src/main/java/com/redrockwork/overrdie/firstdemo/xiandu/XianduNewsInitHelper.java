package com.redrockwork.overrdie.firstdemo.xiandu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.networktools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.networktools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class XianduNewsInitHelper extends RecyclerView.Adapter<XianduNewsInitHelper.ViewHodler> {
    private String categories = "qdaily";
    private ArrayList<XianduNews> xianduNewsArrayList = new ArrayList<>();
    private Context context;

    public XianduNewsInitHelper(Context context) {
        this.context = context;
    }

    public void initData(String categories) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper("http://gank.io/api/xiandu/data/id/"+categories+"/count/10/page/1");
        Recall recall = httpsRequestHelper.start();
        String baseJson = recall.getJson();
        JSONObject baseJsonObject = new JSONObject(baseJson);
        JSONArray resultJson = new JSONArray(baseJsonObject.getString("results"));
        for (int i = 0; i < resultJson.length(); i++) {
            JSONObject oneNewsJson = new JSONObject(resultJson.get(i).toString());
            String image = oneNewsJson.getString("cover");
            String time = oneNewsJson.getString("created_at");
            JSONObject detail = new JSONObject(oneNewsJson.getString("raw"));
            String title = detail.getString("title");
            String url = detail.getString("originId");
            xianduNewsArrayList.add(new XianduNews(image,title,time,url));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler viewHodler, int i) {
        viewHodler.title.setText(xianduNewsArrayList.get(i).getTitle());
        viewHodler.time.setText(xianduNewsArrayList.get(i).getTime());
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.xiandu_news_item,viewGroup,false);;
        ViewHodler viewHodler = new ViewHodler(view);
        return viewHodler;
    }

    @Override
    public int getItemCount() {
        return xianduNewsArrayList.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder{
        ImageView titleImage;
        TextView title,time;
        public ViewHodler(@NonNull View itemView) {
            super(itemView);
            titleImage = itemView.findViewById(R.id.iv_xiandu_title);
            title = itemView.findViewById(R.id.tv_xiandu_title);
            time = itemView.findViewById(R.id.tv_xiandu_time);

        }
    }
}
