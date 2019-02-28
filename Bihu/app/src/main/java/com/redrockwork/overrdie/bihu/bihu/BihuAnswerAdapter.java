package com.redrockwork.overrdie.bihu.bihu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;

import java.util.ArrayList;

public class BihuAnswerAdapter extends RecyclerView.Adapter<BihuAnswerAdapter.ViewHolder>{
    private ArrayList<BihuAnswer> bihuAnswerArrayList = new ArrayList<>();
    private Context context;
    private android.os.Handler mainHandler;
    private int authorId;
    private int lastPosition = -1;
    public BihuAnswerAdapter(ArrayList<BihuAnswer> bihuAnswerArrayList, Context context, android.os.Handler handler,int authorId) {
        this.bihuAnswerArrayList = bihuAnswerArrayList;
        this.context = context;
        this.mainHandler = handler;
        this.authorId = authorId;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bihu_answer_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        //设置动画效果
        int adapterPosition = viewHolder.getAdapterPosition();
        if (adapterPosition > lastPosition) {
            Animator animator = ObjectAnimator.ofFloat(viewHolder.itemView, "alpha", 0, 1f);
            animator.setDuration(1000).start();
            lastPosition = adapterPosition;
        } else {
            ViewCompat.setAccessibilityLiveRegion(viewHolder.itemView, 1);
        }
        final BihuAnswer bihuAnswer = bihuAnswerArrayList.get(position);
        viewHolder.authorName.setText(bihuAnswer.getAuthor().getUsername());
        viewHolder.content.setText(bihuAnswer.getContent());
        viewHolder.time.setText(bihuAnswer.getDate());
        //采纳图片设置
        if (bihuAnswer.getBest().equals("0")){
            viewHolder.best.setImageBitmap(null);
        }
        if (authorId==Integer.parseInt(BihuFragment.nowUser.getId())&&bihuAnswer.getBest().equals("0")){
            viewHolder.best.setImageResource(R.drawable.unbest);
            viewHolder.best.setPadding(30,20,20,30);
            viewHolder.best.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bestClickListener.onBestClickListener(position,viewHolder);
                }
            });
        }
        //赞和踩
        viewHolder.exciting.setText(bihuAnswer.getExciting()+" 赞");
        if (bihuAnswer.getIsExciting().equals("false")){
            Drawable exciting = ContextCompat.getDrawable(context,R.drawable.exciting_unfill);
            exciting.setBounds(0,0,40,40);
            viewHolder.exciting.setCompoundDrawables(exciting,null,null,null);
        }else {
            Drawable exciting = ContextCompat.getDrawable(context,R.drawable.exciting_fill);
            exciting.setBounds(0,0,40,40);
            viewHolder.exciting.setCompoundDrawables(exciting,null,null,null);
        }
        viewHolder.exciting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excitingClickListener.onExcitingClickListener(position,viewHolder);
            }
        });
        viewHolder.naive.setText(bihuAnswer.getNaive()+" 踩");
        if (bihuAnswer.getIsNaive().equals("false")){
            Drawable naive = ContextCompat.getDrawable(context,R.drawable.naive_unfill);
            naive.setBounds(0,0,40,40);
            viewHolder.naive.setCompoundDrawables(naive,null,null,null);
        }else {
            Drawable naive = ContextCompat.getDrawable(context,R.drawable.naive_fill);
            naive.setBounds(0,0,40,40);
            viewHolder.naive.setCompoundDrawables(naive,null,null,null);
        }
        viewHolder.naive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naiveClickListener.onNaiveClickListener(position,viewHolder);
            }
        });
        //先设置默认头像,然后网络请求结束后更换
        viewHolder.authorAvatar.setImageResource(R.drawable.defultuser);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String imageUrl = bihuAnswer.getAuthor().getAvatar();
                if (!imageUrl.equals("null")){
                    final Bitmap bitmap = MyImageTools.getBitmap(imageUrl);
                    if (bitmap!=null)
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap avatar = MyImageTools.cutToCircle(bitmap);
                            viewHolder.authorAvatar.setImageBitmap(avatar);
                            Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
                            viewHolder.authorAvatar.startAnimation(animation);
                        }
                    });
                }
            }
        });
        if (!bihuAnswer.getImagesUrl().equals("null")&&!bihuAnswer.getImagesUrl().equals("")){
            final String [] imagesUrl = bihuAnswer.getImagesUrl().split(",");
            for (int i = 0; i < imagesUrl.length; i++) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.icon_1024);
                bihuAnswer.getImageViews().add(imageView);
                viewHolder.images.addView(bihuAnswer.getImageViews().get(i));
            }
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < imagesUrl.length; i++) {
                        Bitmap bitmap = MyImageTools.getBitmap(imagesUrl[i]);
                        if (bitmap!=null){
                            final ImageView imageView = new ImageView(context);
                            imageView.setImageBitmap(bitmap);
                            //去白边
                            imageView.setAdjustViewBounds(true);
                            //留白
                            imageView.setPadding(0,10,20,10);
                            final int finalI = i+1;
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
                                    imageView.startAnimation(animation);
                                    viewHolder.images.addView(imageView, finalI);
                                    viewHolder.images.removeViewAt(viewHolder.images.getChildCount()-1);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bihuAnswerArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView authorAvatar,best;
        TextView authorName,content,time,exciting,naive;
        LinearLayout images;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorAvatar = itemView.findViewById(R.id.iv_answer_author);
            authorName = itemView.findViewById(R.id.tv_answer_author);
            content = itemView.findViewById(R.id.tv_answer_content);
            time = itemView.findViewById(R.id.tv_answer_time);
            exciting = itemView.findViewById(R.id.tv_answer_exciting);
            naive = itemView.findViewById(R.id.tv_answer_naive);
            images = itemView.findViewById(R.id.ll_answer_content);
            best = itemView.findViewById(R.id.iv_answer_is_best);
        }
    }

    /**
     * item中的exciting点击事件的接口回调
     */

    public interface ExcitingClickListener{
        void onExcitingClickListener(int position,ViewHolder viewHolder);
    }

    private ExcitingClickListener excitingClickListener = null;

    public void setExcitingClickListener(ExcitingClickListener excitingClickListener) {
        this.excitingClickListener = excitingClickListener;
    }

    /**
     * item中的naive点击事件的接口回调
     */
    public interface NaiveClickListener{
        void onNaiveClickListener(int position,ViewHolder viewHolder);
    }

    private NaiveClickListener naiveClickListener = null;

    public void setNaiveClickListener(NaiveClickListener naiveClickListener){
        this.naiveClickListener = naiveClickListener;
    }

    /**
     * item中的采纳点击事件的接口回调
     */

    public interface BestClickListener{
        void onBestClickListener(int position,ViewHolder viewHolder);
    }

    private BestClickListener bestClickListener = null;

    public void setBestClickListener(BestClickListener bestClickListener){
        this.bestClickListener = bestClickListener;
    }

    /**
     * 增加一个item
     * @param bihuAnswer
     */
    public void addItem(BihuAnswer bihuAnswer){
        int index = bihuAnswerArrayList.size();
        bihuAnswerArrayList.add(index,bihuAnswer);
        notifyItemInserted(index);
    }
}
