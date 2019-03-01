package com.redrockwork.overrdie.bihu.bihu.square;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
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

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.obj.BihuQuestion;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BihuSquareAdapter extends RecyclerView.Adapter<BihuSquareAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<BihuQuestion> bihuQuestionArrayList;
    private Context context;
    private android.os.Handler mainHandler;
    private int lastPosition = -1;
    private int type;

    public BihuSquareAdapter(ArrayList<BihuQuestion> bihuQuestionArrayList, Context context, android.os.Handler handler, int type) {
        this.bihuQuestionArrayList = bihuQuestionArrayList;
        this.context = context;
        this.mainHandler = handler;
        this.type = type;
    }

    public ArrayList<BihuQuestion> getBihuQuestionArrayList() {
        return bihuQuestionArrayList;
    }

    public void setBihuQuestionArrayList(ArrayList<BihuQuestion> bihuQuestionArrayList) {
        this.bihuQuestionArrayList = bihuQuestionArrayList;
    }

    /**
     * RecyclerViewAdapter部分
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, time, exciting, naive, comment, author, createTime;
        public ImageView abstractImage;
        public ImageView imageView, favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_question_title);
            content = itemView.findViewById(R.id.tv_question_content);
            time = itemView.findViewById(R.id.tv_question_time);
            exciting = itemView.findViewById(R.id.tv_question_exciting);
            naive = itemView.findViewById(R.id.tv_question_naive);
            comment = itemView.findViewById(R.id.tv_question_comment_count);
            abstractImage = itemView.findViewById(R.id.iv_question_abstract);
            imageView = itemView.findViewById(R.id.iv_question_author);
            author = itemView.findViewById(R.id.tv_question_author);
            favorite = itemView.findViewById(R.id.iv_question_favorite);
            createTime = itemView.findViewById(R.id.tv_question_create_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bihu_question_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        //设置动画效果
        int adapterPosition = viewHolder.getAdapterPosition();
        if (adapterPosition > lastPosition) {
            Animator animator = ObjectAnimator.ofFloat(viewHolder.itemView, "alpha", 0, 1f);
            animator.setDuration(800).start();
            lastPosition = adapterPosition;
        } else {
            ViewCompat.setAccessibilityLiveRegion(viewHolder.itemView, 1);
        }
        final BihuQuestion bihuQuestion = bihuQuestionArrayList.get(position);
        viewHolder.title.setText(bihuQuestion.getTitle());
        viewHolder.content.setText(bihuQuestion.getAbstractContent());
        //选择显示更新时间还是发布时间
        if (bihuQuestion.getRecent().equals("null")) {
            viewHolder.time.setText("没有动态😣");
        } else {
            viewHolder.time.setText(bihuQuestion.getRecent());
        }

        viewHolder.createTime.setText(bihuQuestion.getTime());

        //收藏状态显示
        if (bihuQuestion.getIs_favorite().equals("true")) {
            viewHolder.favorite.setImageResource(R.drawable.favorite);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.unfavorite);
        }
        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteClickListener.onFavoriteClickListener(position, viewHolder);
            }
        });
        viewHolder.author.setText(bihuQuestion.getAuthorName());
        viewHolder.exciting.setText(bihuQuestion.getExciting() + " 赞");
        if (bihuQuestion.getIs_exciting().equals("false")) {
            Drawable exciting = ContextCompat.getDrawable(context, R.drawable.exciting_unfill);
            exciting.setBounds(0, 0, 40, 40);
            viewHolder.exciting.setCompoundDrawables(exciting, null, null, null);
        } else {
            Drawable exciting = ContextCompat.getDrawable(context, R.drawable.exciting_fill);
            exciting.setBounds(0, 0, 40, 40);
            viewHolder.exciting.setCompoundDrawables(exciting, null, null, null);
        }
        viewHolder.exciting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excitingClickListener.onExcitingClickListener(position, viewHolder);
            }
        });
        viewHolder.naive.setText(bihuQuestion.getNaive() + " 踩");
        if (bihuQuestion.getIs_navie().equals("false")) {
            Drawable naive = ContextCompat.getDrawable(context, R.drawable.naive_unfill);
            naive.setBounds(0, 0, 40, 40);
            viewHolder.naive.setCompoundDrawables(naive, null, null, null);
        } else {
            Drawable naive = ContextCompat.getDrawable(context, R.drawable.naive_fill);
            naive.setBounds(0, 0, 40, 40);
            viewHolder.naive.setCompoundDrawables(naive, null, null, null);
        }
        viewHolder.naive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naiveClickListener.onNaiveClickListener(position, viewHolder);
            }
        });
        viewHolder.comment.setText(bihuQuestion.getComment() + " 回答");
        Bitmap defaultBitmap = MyImageTools.changeToBitmap(R.drawable.defultuser, context);
        bihuQuestion.setBitmap(defaultBitmap);
        viewHolder.imageView.setImageBitmap(bihuQuestion.getBitmap());

        final String imageUrl = bihuQuestion.getAuthorAvatar();
        //为何要以数组实现???
        final Bitmap[] bitmap = new Bitmap[1];
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (!imageUrl.equals("null")) {
                    Log.d(TAG, "onBindViewHolder: " + bihuQuestion.getTitle() + "提问者头像开始加载 url是" + imageUrl);
                    bitmap[0] = MyImageTools.getBitmap(imageUrl);
                    if (bitmap[0] != null) {
                        Bitmap bitmapCircle = MyImageTools.cutToCircle(bitmap[0]);
                        bihuQuestion.setBitmap(bitmapCircle);
                    }
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            viewHolder.imageView.setImageBitmap(bihuQuestion.getBitmap());
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                            viewHolder.imageView.startAnimation(animation);
                        }
                    });
                }
            }
        });
        viewHolder.abstractImage.setImageResource(R.drawable.icon_1024);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (!bihuQuestion.getImageUrl().equals("null")) {
                    String imageUrl = bihuQuestion.getImageUrl().split(",")[0];
                    Log.d(TAG, "run: 问题的详情图片url: " + imageUrl);
                    final Bitmap bitmap = MyImageTools.getBitmap(imageUrl);
                    if (bitmap != null) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.abstractImage.setImageBitmap(bitmap);
                                Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                                viewHolder.abstractImage.startAnimation(animation);
                            }
                        });
                    }
                }
            }
        });
        viewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return bihuQuestionArrayList.size();
    }

    /**
     * RecyclerViewItem点击事件的接口回调
     */
    private OnItemClickListener OnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        this.OnItemClickListener = OnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View v) {
        if (OnItemClickListener != null) {
            OnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    /**
     * item中的exciting点击事件的接口回调
     */

    public interface ExcitingClickListener {
        void onExcitingClickListener(int position, ViewHolder viewHolder);
    }

    private ExcitingClickListener excitingClickListener = null;

    public void setExcitingClickListener(ExcitingClickListener excitingClickListener) {
        this.excitingClickListener = excitingClickListener;
    }

    /**
     * item中的naive点击事件的接口回调
     */
    public interface NaiveClickListener {
        void onNaiveClickListener(int position, ViewHolder viewHolder);
    }

    private NaiveClickListener naiveClickListener = null;

    public void setNaiveClickListener(NaiveClickListener naiveClickListener) {
        this.naiveClickListener = naiveClickListener;
    }

    /**
     * itme中的favorite点击事件的接口回调
     */
    public interface MoreClickListener {
        void onMoreClickListener(int position, ViewHolder viewHolder);
    }

    private MoreClickListener moreClickListener = null;

    public void setMoreClickListener(MoreClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }


    /**
     * itme中的favorite点击事件的接口回调
     */
    public interface FavoriteClickListener {
        void onFavoriteClickListener(int position, ViewHolder viewHolder);
    }

    private FavoriteClickListener favoriteClickListener = null;

    public void setFavoriteClickListener(FavoriteClickListener favoriteClickListener) {
        this.favoriteClickListener = favoriteClickListener;
    }

    /**
     * RecyclerView删除/增加一个item
     */
    public void remove(int position) {
        bihuQuestionArrayList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void add(int position, BihuQuestion bihuQuestion) {
        bihuQuestionArrayList.add(position, bihuQuestion);
        notifyItemInserted(position);
    }

    /**
     * RecyclerView更新一个item
     */
    public void update() {
        notifyDataSetChanged();
    }


}
