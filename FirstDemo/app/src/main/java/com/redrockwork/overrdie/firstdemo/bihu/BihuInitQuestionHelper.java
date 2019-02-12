package com.redrockwork.overrdie.firstdemo.bihu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.redrockwork.overrdie.firstdemo.R;
import java.util.ArrayList;

public class BihuInitQuestionHelper extends RecyclerView.Adapter<BihuInitQuestionHelper.ViewHolder> implements View.OnClickListener{
    private ArrayList<BihuQuestion> bihuQuestionArrayList = new ArrayList<>();
    private static String baseUrl = "http://bihu.jay86.com/";

    public BihuInitQuestionHelper(ArrayList<BihuQuestion> bihuQuestionArrayList) {
        this.bihuQuestionArrayList = bihuQuestionArrayList;
    }

    /**
     * RecyclerViewAdapter部分
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,content,time,exting,naive,comment;
        LinearLayout abstractImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_question_title);
            content = itemView.findViewById(R.id.tv_question_content);
            time = itemView.findViewById(R.id.tv_question_time);
            exting = itemView.findViewById(R.id.tv_question_exciting);
            naive = itemView.findViewById(R.id.tv_question_naive);
            comment = itemView.findViewById(R.id.tv_question_comment_count);
            abstractImage = itemView.findViewById(R.id.ll_question_abstract);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bihu_question_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BihuQuestion bihuQuestion = bihuQuestionArrayList.get(i);
        viewHolder.title.setText(bihuQuestion.getTitle());
        viewHolder.content.setText(bihuQuestion.getAbstractContent());
        viewHolder.time.setText(bihuQuestion.getTime());
        viewHolder.exting.setText(bihuQuestion.getExting());
        viewHolder.naive.setText(bihuQuestion.getNaive());
        viewHolder.comment.setText(bihuQuestion.getComment());
    }

    @Override
    public int getItemCount() {
        return bihuQuestionArrayList.size();
    }

    /**
     * RecyclerViewItem点击事件接口回调
     */
    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListenr(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener!=null){
            onItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
}
