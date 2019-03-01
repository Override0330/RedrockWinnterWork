package com.redrockwork.overrdie.bihu.bihu.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.obj.BihuAnswer;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.user.BihuLoginActivity;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.obj.BihuQuestion;
import com.redrockwork.overrdie.bihu.bihu.UnCurrentUserException;
import com.redrockwork.overrdie.bihu.bihu.obj.User;
import com.redrockwork.overrdie.bihu.bihu.square.BihuSquareFragment;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;
import com.redrockwork.overrdie.bihu.developtools.RecyclerViewMyLinearLayoutManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class BihuQuestionDetailActivity extends AppCompatActivity implements BihuAnswerAdapter.ExcitingClickListener, BihuAnswerAdapter.NaiveClickListener, BihuAnswerAdapter.BestClickListener, BihuAnswerAdapter.TextViewLongClickListener {
    private TextView author, title, content, time, exciting, naive, comment;
    private ImageView authorAvatar, back, send, add;
    private LinearLayout images, publishImages;
    private EditText answerContent;
    private BihuQuestion bihuQuestion;
    private BihuAnswer bihuAnswer;
    private RecyclerView bihuAnswerList;
    private BihuAnswerAdapter bihuAnswerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<BihuAnswer> bihuAnswerArrayList = new ArrayList<>();
    private ArrayList<String> answerImagesUrl = new ArrayList<>();
    private ArrayList<Bitmap> publishBitmap = new ArrayList<>();
    private BihuAnswerAdapter.ViewHolder viewHolder;
    private Context context = this;
    private static final int CHOOSE_PHOTO = 0;
    private static final int SET_AUTHOR_AVATAR = 0;
    private static final int INIT_QUESTION_IMAGES = 1;
    private static final int INIT_ANSWER_ADAPTER = 2;
    private static final int SET_QUESTION_EXCITING_TEXT_VIEW_ICON = 3;
    private static final int SET_QUESTION_NAIVE_TEXT_VIEW_ICON = 4;
    private static final int SET_ANSWER_EXCITING_TEXT_VIEW_ICON = 5;
    private static final int SET_ANSWER_NAIVE_TEXT_VIEW_ICON = 6;
    private static final int SHOW_TOAST_MESSAGE = 7;
    private static final int CHANGE_BEST_STATE = 8;
    private static final int ADD_ANSWER = 9;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_AUTHOR_AVATAR:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    authorAvatar.setImageBitmap(bitmap);
                    break;
                case INIT_QUESTION_IMAGES:
                    ArrayList<Bitmap> bitmapArrayList = (ArrayList<Bitmap>) msg.obj;
                    for (int i = 0; i < bitmapArrayList.size(); i++) {
                        if (bitmapArrayList.get(i) == null) {
                            break;
                        }
                        ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(bitmapArrayList.get(i));
                        //去白边
                        imageView.setAdjustViewBounds(true);
                        //留白
                        imageView.setPadding(0, 10, 0, 10);
                        images.addView(imageView, i + 1);
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        imageView.startAnimation(animation);
                        images.removeViewAt(images.getChildCount() - 1);
                    }
                    break;
                case INIT_ANSWER_ADAPTER:
                    initAnswerAdapter();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case SET_QUESTION_EXCITING_TEXT_VIEW_ICON:
                    Drawable excitingDrawable = (Drawable) msg.obj;
                    excitingDrawable.setBounds(0, 0, 40, 40);
                    exciting.setCompoundDrawables(excitingDrawable, null, null, null);
                    String nowExcitingCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg1 == 1) {
                        nowExcitingCount = Integer.parseInt(bihuQuestion.getExciting()) + 1 + "";
                        bihuQuestion.setExciting(nowExcitingCount);
                    } else {
                        nowExcitingCount = Integer.parseInt(bihuQuestion.getExciting()) - 1 + "";
                        bihuQuestion.setExciting(nowExcitingCount);
                    }
                    exciting.setText(nowExcitingCount + " 赞");
                    break;
                case SET_QUESTION_NAIVE_TEXT_VIEW_ICON:
                    Drawable naiveDrawable = (Drawable) msg.obj;
                    naiveDrawable.setBounds(0, 0, 40, 40);
                    naive.setCompoundDrawables(naiveDrawable, null, null, null);
                    String nowNaiveCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg1 == 1) {
                        nowNaiveCount = Integer.parseInt(bihuQuestion.getNaive()) + 1 + "";
                        bihuQuestion.setNaive(nowNaiveCount);
                    } else {
                        nowNaiveCount = Integer.parseInt(bihuQuestion.getNaive()) - 1 + "";
                        bihuQuestion.setNaive(nowNaiveCount);
                    }
                    naive.setText(nowNaiveCount + " 踩");
                    break;
                case SET_ANSWER_EXCITING_TEXT_VIEW_ICON:
                    Drawable excitingDrawable2 = (Drawable) msg.obj;
                    bihuAnswer = bihuAnswerArrayList.get(msg.arg2);
                    excitingDrawable2.setBounds(0, 0, 40, 40);
                    viewHolder.exciting.setCompoundDrawables(excitingDrawable2, null, null, null);
                    String nowAnswerExcitingCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg1 == 1) {
                        nowAnswerExcitingCount = Integer.parseInt(bihuAnswer.getExciting()) + 1 + "";
                        bihuAnswer.setExciting(nowAnswerExcitingCount);
                    } else {
                        nowAnswerExcitingCount = Integer.parseInt(bihuAnswer.getExciting()) - 1 + "";
                        bihuAnswer.setExciting(nowAnswerExcitingCount);
                    }
                    viewHolder.exciting.setText(nowAnswerExcitingCount + " 赞");
                    break;
                case SET_ANSWER_NAIVE_TEXT_VIEW_ICON:
                    Drawable naiveDrawable2 = (Drawable) msg.obj;
                    bihuAnswer = bihuAnswerArrayList.get(msg.arg2);
                    naiveDrawable2.setBounds(0, 0, 40, 40);
                    viewHolder.naive.setCompoundDrawables(naiveDrawable2, null, null, null);
                    String nowAnswerNaiveCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg1 == 1) {
                        nowAnswerNaiveCount = Integer.parseInt(bihuAnswer.getNaive()) + 1 + "";
                        bihuAnswer.setNaive(nowAnswerNaiveCount);
                    } else {
                        nowAnswerNaiveCount = Integer.parseInt(bihuAnswer.getNaive()) - 1 + "";
                        bihuAnswer.setNaive(nowAnswerNaiveCount);
                    }
                    viewHolder.naive.setText(nowAnswerNaiveCount + " 踩");
                    break;
                case SHOW_TOAST_MESSAGE:
                    swipeRefreshLayout.setRefreshing(false);
                    String message = (String) msg.obj;
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if (msg.arg1 == 0) {
                        answerContent.setText("");
                        publishImages.removeAllViews();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    break;
                case CHANGE_BEST_STATE:
                    BihuAnswerAdapter.ViewHolder viewHolder = (BihuAnswerAdapter.ViewHolder) msg.obj;
                    viewHolder.best.setImageResource(R.drawable.best);
                    viewHolder.best.setPadding(0, 0, 0, 0);
                    bihuAnswerArrayList.get(msg.arg1).setBest("1");
                    break;
                case ADD_ANSWER:
                    bihuAnswerAdapter.addItem((BihuAnswer) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        int index = intent.getIntExtra("thisQuestionIndex", 0);
        bihuQuestion = BihuSquareFragment.bihuQuestionArrayList.get(index);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bihu_question_detail);
        swipeRefreshLayout = findViewById(R.id.sr_bihu_question_detail);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSkyBlue));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (BihuFragment.nowUser != null) {
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //加载回答的文字信息
                                initAnswer(BihuFragment.nowUser, "0", "20", bihuQuestion.getId());
                                Message message = new Message();
                                message.what = INIT_ANSWER_ADAPTER;
                                handler.sendMessage(message);
                                //加载回答的图片
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (UnCurrentUserException e) {
                                //身份验证过期
                                disposeUnCurrentUser();
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    try {
                        BihuPostTools.bihuAnswerArrayList.clear();
                        bihuAnswerArrayList = BihuPostTools.getAnswerListWithoutNetwork("0", bihuQuestion.getId() + "");
                        swipeRefreshLayout.setRefreshing(false);
                        Message message = new Message();
                        message.what = INIT_ANSWER_ADAPTER;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        initView();
    }

    private void initView() {
        answerContent = findViewById(R.id.et_answer_content);
        answerContent.clearFocus();
        publishImages = findViewById(R.id.ll_answer_images);
        send = findViewById(R.id.iv_answer_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser == null) {
                    //首先进行nowUser是否为null判断避免报错
                    Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
                } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
                    //加载登录界面
                    Intent intent = new Intent(context, BihuLoginActivity.class);
                    startActivity(intent);
                } else {
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (answer()) {
                                //回答成功
                                Message successfulMessage = new Message();
                                successfulMessage.what = SHOW_TOAST_MESSAGE;
                                //成功传入0
                                successfulMessage.arg1 = 0;
                                successfulMessage.obj = "回答成功";
                                handler.sendMessage(successfulMessage);
                            } else {
                                //回答失败
                                Message falseMessage = new Message();
                                falseMessage.what = SHOW_TOAST_MESSAGE;
                                //失败传入1
                                falseMessage.arg1 = 1;
                                falseMessage.obj = "回答失败,网络链接可能已经断开";
                                handler.sendMessage(falseMessage);
                            }
                        }
                    });
                }


            }
        });
        add = findViewById(R.id.iv_answer_add_images);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publishBitmap.size() < 6) {
//                    Toast.makeText(context,"点击了添加图片的按钮",Toast.LENGTH_SHORT).show();
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openAlbum();
                    }
                } else {
                    Toast.makeText(context, "不能再加了奥,最多只能添加5张图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back = findViewById(R.id.iv_question_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        author = findViewById(R.id.tv_question_detail_author);
        author.setText(bihuQuestion.getAuthorName());
        authorAvatar = findViewById(R.id.iv_question_detail_author);
        //先设置默认头像
        Bitmap bitmap = MyImageTools.changeToBitmap(R.drawable.defultuser, context);
        bitmap = MyImageTools.cutToCircle(bitmap);
        authorAvatar.setImageBitmap(bitmap);
        //加载提问者头像
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = MyImageTools.getBitmap(bihuQuestion.getAuthorAvatar());
                if (bitmap != null) {
                    bitmap = MyImageTools.cutToCircle(bitmap);
                    Message message = new Message();
                    message.what = SET_AUTHOR_AVATAR;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                }
            }
        });
        title = findViewById(R.id.tv_question_detail_title);
        title.setText(bihuQuestion.getTitle());
        content = findViewById(R.id.tv_question_detail_content);
        content.setText(bihuQuestion.getContent());
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData mClipData;
                mClipData = ClipData.newPlainText("content", content.getText().toString());
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(context, "已复制问题内容", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        time = findViewById(R.id.tv_question_detail_time);
        time.setText(bihuQuestion.getTime());
        comment = findViewById(R.id.tv_question_detail_comment_count);
        comment.setText(bihuQuestion.getComment() + " 回答");
        images = findViewById(R.id.ll_question_detail_content);

        //设置赞的点击事件
        exciting = findViewById(R.id.tv_question_detail_exciting);
        exciting.setText(bihuQuestion.getExciting() + " 赞");
        if (bihuQuestion.getIs_exciting().equals("false")) {
            Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_unfill);
            excitingDrawable.setBounds(0, 0, 40, 40);
            exciting.setCompoundDrawables(excitingDrawable, null, null, null);
        } else {
            Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_fill);
            excitingDrawable.setBounds(0, 0, 40, 40);
            exciting.setCompoundDrawables(excitingDrawable, null, null, null);
        }
        exciting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser == null) {
                    //首先进行nowUser是否为null判断避免报错
                    Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
                } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
                    //加载登录界面
                    Intent intent = new Intent(context, BihuLoginActivity.class);
                    startActivity(intent);
                } else {
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (bihuQuestion.getIs_exciting().equals("false")) {
                                    if (BihuPostTools.exciting(BihuFragment.nowUser, bihuQuestion.getId(), 1)) {
                                        bihuQuestion.setIs_exciting("true");
                                        Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_fill);
                                        Message message = new Message();
                                        message.what = SET_QUESTION_EXCITING_TEXT_VIEW_ICON;
                                        message.obj = excitingDrawable;
                                        message.arg1 = 1;
                                        handler.sendMessage(message);
                                    } else {
                                        //出错
                                    }
                                } else {
                                    if (BihuPostTools.cancelExciting(BihuFragment.nowUser, bihuQuestion.getId(), 1)) {
                                        bihuQuestion.setIs_exciting("false");
                                        Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_unfill);
                                        Message message = new Message();
                                        message.what = SET_QUESTION_EXCITING_TEXT_VIEW_ICON;
                                        message.obj = excitingDrawable;
                                        message.arg1 = 2;
                                        handler.sendMessage(message);
                                    } else {
                                        //出错
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (UnCurrentUserException e) {
                                //身份验证过期
                                disposeUnCurrentUser();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        //设置踩的点击事件
        naive = findViewById(R.id.tv_question_detail_naive);
        naive.setText(bihuQuestion.getNaive() + " 踩");
        if (bihuQuestion.getIs_navie().equals("false")) {
            Drawable naiveDrawable = ContextCompat.getDrawable(context, R.drawable.naive_unfill);
            naiveDrawable.setBounds(0, 0, 40, 40);
            naive.setCompoundDrawables(naiveDrawable, null, null, null);
        } else {
            Drawable naiveDrawable = ContextCompat.getDrawable(context, R.drawable.naive_fill);
            naiveDrawable.setBounds(0, 0, 40, 40);
            naive.setCompoundDrawables(naiveDrawable, null, null, null);
        }
        naive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser == null) {
                    //首先进行nowUser是否为null判断避免报错
                    Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
                } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
                    //加载登录界面
                    Intent intent = new Intent(context, BihuLoginActivity.class);
                    startActivity(intent);
                } else {
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (bihuQuestion.getIs_navie().equals("false")) {
                                    if (BihuPostTools.naive(BihuFragment.nowUser, bihuQuestion.getId(), 1)) {
                                        bihuQuestion.setIs_navie("true");
                                        Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.naive_fill);
                                        Message message = new Message();
                                        message.what = SET_QUESTION_NAIVE_TEXT_VIEW_ICON;
                                        message.obj = excitingDrawable;
                                        message.arg1 = 1;
                                        handler.sendMessage(message);
                                    } else {
                                        //出错
                                    }
                                } else {
                                    if (BihuPostTools.cancelNaive(BihuFragment.nowUser, bihuQuestion.getId(), 1)) {
                                        bihuQuestion.setIs_navie("false");
                                        Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.naive_unfill);
                                        Message message = new Message();
                                        message.what = SET_QUESTION_NAIVE_TEXT_VIEW_ICON;
                                        message.obj = excitingDrawable;
                                        message.arg1 = 2;
                                        handler.sendMessage(message);
                                    } else {
                                        //出错
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (UnCurrentUserException e) {
                                //身份验证过期
                                disposeUnCurrentUser();
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });


        //加载问题的图像
        if (!bihuQuestion.getImageUrl().equals("null")) {
            final String[] imagesUrlArray = bihuQuestion.getImageUrl().split(",");
            for (int i = 0; i < imagesUrlArray.length; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.icon_1024);
                //去白边
                imageView.setAdjustViewBounds(true);
                //留白
                imageView.setPadding(0, 10, 0, 10);
                images.addView(imageView);
            }
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                    for (int i = 0; i < imagesUrlArray.length; i++) {
                        Bitmap bitmap = MyImageTools.getBitmap(imagesUrlArray[i]);
                        bitmapArrayList.add(bitmap);
                    }
                    Message message = new Message();
                    message.what = INIT_QUESTION_IMAGES;
                    message.obj = bitmapArrayList;
                    handler.sendMessage(message);
                }
            });
        }
        //加载回答
        swipeRefreshLayout.setRefreshing(true);
        if (BihuFragment.nowUser != null) {
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //加载回答的文字信息
                        initAnswer(BihuFragment.nowUser, "0", "20", bihuQuestion.getId());
                        Message message = new Message();
                        message.what = INIT_ANSWER_ADAPTER;
                        handler.sendMessage(message);
                        //加载回答的图片
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        //身份验证过期
                        disposeUnCurrentUser();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                BihuPostTools.bihuAnswerArrayList.clear();
                bihuAnswerArrayList = BihuPostTools.getAnswerListWithoutNetwork("0", bihuQuestion.getId() + "");
                Collections.reverse(bihuAnswerArrayList);
                Message message = new Message();
                message.what = INIT_ANSWER_ADAPTER;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initAnswer(User user, String page, String count, String qid) throws TimeoutException, JSONException, IOException, UnCurrentUserException {
        BihuPostTools.bihuAnswerArrayList.clear();
        try {
            bihuAnswerArrayList = BihuPostTools.getAnswerList(user, page, count, qid);
        } catch (UnknownHostException e) {
            try {
                BihuPostTools.bihuAnswerArrayList.clear();
                bihuAnswerArrayList = BihuPostTools.getAnswerListWithoutNetwork("0", bihuQuestion.getId() + "");
                swipeRefreshLayout.setRefreshing(false);
                Message message = new Message();
                message.what = INIT_ANSWER_ADAPTER;
                handler.sendMessage(message);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }


//        Collections.reverse(bihuAnswerArrayList);
    }

    private void initAnswerAdapter() {
        bihuAnswerList = findViewById(R.id.rv_bihu_answer);
        Collections.reverse(bihuAnswerArrayList);
        //最笨的算法置顶采纳
        for (int i = 0; i < bihuAnswerArrayList.size(); i++) {
            if (!bihuAnswerArrayList.get(i).getBest().equals("0")) {
                BihuAnswer bestAnswer = bihuAnswerArrayList.get(i);
                bihuAnswerArrayList.remove(i);
                bihuAnswerArrayList.add(0, bestAnswer);
            }
        }
        bihuAnswerAdapter = new BihuAnswerAdapter(bihuAnswerArrayList, this, handler, bihuQuestion.getAuthorId());
        bihuAnswerAdapter.setExcitingClickListener(this);
        bihuAnswerAdapter.setNaiveClickListener(this);
        bihuAnswerAdapter.setBestClickListener(this);
        bihuAnswerAdapter.setTextViewLongClickListener(this);
        RecyclerViewMyLinearLayoutManager layoutManager = new RecyclerViewMyLinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        bihuAnswerList.setHasFixedSize(true);
        bihuAnswerList.setNestedScrollingEnabled(false);
        bihuAnswerList.setLayoutManager(layoutManager);
        bihuAnswerList.setAdapter(bihuAnswerAdapter);
    }

    //回答的点赞事件
    @Override
    public void onExcitingClickListener(final int position, BihuAnswerAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser == null) {
            //首先进行nowUser是否为null判断避免报错
            Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
        } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
            //加载登录界面
            Intent intent = new Intent(context, BihuLoginActivity.class);
            startActivity(intent);
        } else {
            this.viewHolder = viewHolder;
            final BihuAnswer bihuAnswer = bihuAnswerArrayList.get(position);
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bihuAnswer.getIsExciting().equals("false")) {
                            if (BihuPostTools.exciting(BihuFragment.nowUser, bihuAnswer.getId() + "", 2)) {
                                bihuAnswer.setIsExciting("true");
                                Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_fill);
                                Message message = new Message();
                                message.what = SET_ANSWER_EXCITING_TEXT_VIEW_ICON;
                                message.obj = excitingDrawable;
                                message.arg1 = 1;
                                message.arg2 = position;
                                handler.sendMessage(message);
                            } else {
                                //出错
                            }
                        } else {
                            if (BihuPostTools.cancelExciting(BihuFragment.nowUser, bihuAnswer.getId() + "", 2)) {
                                bihuAnswer.setIsExciting("false");
                                Drawable excitingDrawable = ContextCompat.getDrawable(context, R.drawable.exciting_unfill);
                                Message message = new Message();
                                message.what = SET_ANSWER_EXCITING_TEXT_VIEW_ICON;
                                message.obj = excitingDrawable;
                                message.arg1 = 2;
                                message.arg2 = position;
                                handler.sendMessage(message);
                            } else {
                                //出错
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        //身份验证过期
                        disposeUnCurrentUser();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //回答的踩事件
    @Override
    public void onNaiveClickListener(final int position, BihuAnswerAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser == null) {
            //首先进行nowUser是否为null判断避免报错
            Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
        } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
            //加载登录界面
            Intent intent = new Intent(context, BihuLoginActivity.class);
            startActivity(intent);
        } else {
            this.viewHolder = viewHolder;
            final BihuAnswer bihuAnswer = bihuAnswerArrayList.get(position);
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bihuAnswer.getIsNaive().equals("false")) {
                            if (BihuPostTools.naive(BihuFragment.nowUser, bihuAnswer.getId() + "", 2)) {
                                bihuAnswer.setIsNaive("true");
                                Drawable naiveDrawable = ContextCompat.getDrawable(context, R.drawable.naive_fill);
                                Message message = new Message();
                                message.what = SET_ANSWER_NAIVE_TEXT_VIEW_ICON;
                                message.obj = naiveDrawable;
                                message.arg1 = 1;
                                message.arg2 = position;
                                handler.sendMessage(message);
                            } else {
                                //出错
                            }
                        } else {
                            if (BihuPostTools.cancelNaive(BihuFragment.nowUser, bihuAnswer.getId() + "", 2)) {
                                bihuAnswer.setIsNaive("false");
                                Drawable naiveDrawable = ContextCompat.getDrawable(context, R.drawable.naive_unfill);
                                Message message = new Message();
                                message.what = SET_ANSWER_NAIVE_TEXT_VIEW_ICON;
                                message.obj = naiveDrawable;
                                message.arg1 = 2;
                                message.arg2 = position;
                                handler.sendMessage(message);
                            } else {
                                //出错
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        //身份验证过期
                        disposeUnCurrentUser();
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    //发布回答的点击事件
    private boolean answer() {
        final Boolean[] result = {false};
        final String answer = answerContent.getText().toString();
        try {
            if (publishBitmap.size() != 0) {
                for (int i = 0; i < publishBitmap.size(); i++) {
                    String fileName = "qid_" + bihuQuestion.getId() + "author_" + BihuFragment.nowUser.getUsername() + "time_" + System.currentTimeMillis() + "picId_" + i;
                    Bitmap bitmap = publishBitmap.get(i);
                    File file = MyImageTools.saveBitmapFile(bitmap, fileName);
                    MyImageTools.postFileToQiniu(file, fileName);
                    answerImagesUrl.add("http://pnffhnnkk.bkt.clouddn.com/" + fileName);
                    result[0] = BihuPostTools.answer(BihuFragment.nowUser, Integer.parseInt(bihuQuestion.getId()), answer, answerImagesUrl);

                }
            } else {
                result[0] = BihuPostTools.answer(BihuFragment.nowUser, Integer.parseInt(bihuQuestion.getId()), answer, answerImagesUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnCurrentUserException e) {
            //身份验证过期
            disposeUnCurrentUser();
            e.printStackTrace();
        }
        if (result[0]) {
            String images = "";
            for (int i = 0; i < answerImagesUrl.size(); i++) {
                images = images + answerImagesUrl.get(i);
                if (i != answerImagesUrl.size() - 1) {
                    images = images + ",";
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BihuAnswer bihuAnswer = new BihuAnswer(0, answer, images, df.format(new Date()), "0", "0", "0", "false", "false", BihuFragment.nowUser);
            Message message = new Message();
            message.what = ADD_ANSWER;
            message.obj = bihuAnswer;
            handler.sendMessage(message);
        }
        return result[0];
    }

    //打开相册的逻辑
    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    //Toast
                    Toast.makeText(context, "相册打开失败,是不是没授权鸭", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4up
                        handleImageOnKitKat(data);
                    } else {
                        //4.4down
                        handlerImageBeforeKiKat(data);
                    }
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        //根据图片路径添加图片displayImage
        addImage(imagePath);
    }

    private void handlerImageBeforeKiKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        //根据图片路径添加图片displayImage
        addImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void addImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            publishBitmap.add(bitmap);
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(200);
            imageView.setPadding(3, 5, 3, 5);
            publishImages.addView(imageView);
        }
    }

    //采纳
    @Override
    public void onBestClickListener(final int position, final BihuAnswerAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser == null) {
            //首先进行nowUser是否为null判断避免报错
            Toast.makeText(context, "当前无网络连接,无法进行该操作", Toast.LENGTH_SHORT).show();
        } else if (BihuFragment.nowUser.getUsername().equals("temp")) {
            //加载登录界面
            Intent intent = new Intent(context, BihuLoginActivity.class);
            startActivity(intent);
        } else {
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (BihuPostTools.accept(BihuFragment.nowUser, Integer.parseInt(bihuQuestion.getId()), bihuAnswerArrayList.get(position).getId())) {
                            Message successfulMessage = new Message();
                            successfulMessage.what = SHOW_TOAST_MESSAGE;
                            successfulMessage.obj = "采纳成功";
                            handler.sendMessage(successfulMessage);
                            Message changeState = new Message();
                            changeState.what = CHANGE_BEST_STATE;
                            changeState.obj = viewHolder;
                            changeState.arg1 = position;
                            handler.sendMessage(changeState);
                        } else {
                            Message falseMessage = new Message();
                            falseMessage.what = SHOW_TOAST_MESSAGE;
                            falseMessage.obj = "采纳失败";
                            handler.sendMessage(falseMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        //身份验证过期
                        disposeUnCurrentUser();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void disposeUnCurrentUser() {
        Message message = new Message();
        message.what = SHOW_TOAST_MESSAGE;
        message.obj = "身份验证过期,请重新登录";
        handler.sendMessage(message);
        try {
            BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
            MainActivity.avator = MyImageTools.changeToBitmap(R.drawable.defultuser, context);
            MainActivity.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.header.setImageResource(R.drawable.defultuser);
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (TimeoutException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Intent intent = new Intent(context, BihuLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onTextViewLongClickListener(int position, BihuAnswerAdapter.ViewHolder viewHolder) {
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData mClipData;
        mClipData = ClipData.newPlainText("content", viewHolder.content.getText().toString());
        mClipboardManager.setPrimaryClip(mClipData);
        Toast.makeText(context, "已复制回答内容", Toast.LENGTH_SHORT).show();
    }
}
