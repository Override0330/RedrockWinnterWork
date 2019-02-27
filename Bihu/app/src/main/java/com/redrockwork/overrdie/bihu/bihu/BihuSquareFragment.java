package com.redrockwork.overrdie.bihu.bihu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.developtools.RecyclerViewMyLinearLayoutManager;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static android.content.ContentValues.TAG;

public class BihuSquareFragment extends Fragment implements BihuSquareAdapter.ExcitingClickListener , BihuSquareAdapter.NaiveClickListener ,BihuSquareAdapter.FavoriteClickListener {
    private View bihuSquareView;
    public static ArrayList<BihuQuestion> bihuQuestionArrayList = new ArrayList<>();
    private RecyclerView squareRecyclerView;
    private BihuSquareAdapter.ViewHolder viewHolder;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static BihuSquareAdapter bihuSquareAdapter;
    private Handler mainHandler = new Handler();
    public static boolean isClickThePublishButton = false;
    private static final int SETRECYCLERVIEWADAPTER = 0;
    private static final int SHOWTOASTMESSAGE = 1;
    private static final int SETEXCITINGTEXTVIEWICON = 2;
    private static final int SETNAIVETEXTVIEWICON = 3;
    private static final int CHANGEIMAGES = 4;
    private static final int REFRESH = 5;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SETRECYCLERVIEWADAPTER:
                    initRecyclerView();
                    break;
                case SHOWTOASTMESSAGE:
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case SETEXCITINGTEXTVIEWICON:
                    Drawable excitingDrawable = (Drawable)msg.obj;
                    excitingDrawable.setBounds(0,0,40,40);
                    viewHolder.exciting.setCompoundDrawables(excitingDrawable,null,null,null);
                    String nowExcitingCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg2==1){
                        nowExcitingCount = Integer.parseInt(bihuQuestionArrayList.get(msg.arg1).getExciting())+1+"";
                        bihuQuestionArrayList.get(msg.arg1).setExciting(nowExcitingCount);
                    }else {
                        nowExcitingCount = Integer.parseInt(bihuQuestionArrayList.get(msg.arg1).getExciting())-1+"";
                        bihuQuestionArrayList.get(msg.arg1).setExciting(nowExcitingCount);
                    }
                    viewHolder.exciting.setText(nowExcitingCount+" 赞");
                    break;
                case SETNAIVETEXTVIEWICON:
                    Drawable naiveDrawable = (Drawable)msg.obj;
                    naiveDrawable.setBounds(0,0,40,40);
                    viewHolder.naive.setCompoundDrawables(naiveDrawable,null,null,null);
                    String nowNaiveCount;
                    //如果表示数值增加就是1,减少就是2
                    if (msg.arg2==1){
                        nowNaiveCount = Integer.parseInt(bihuQuestionArrayList.get(msg.arg1).getNaive())+1+"";
                        bihuQuestionArrayList.get(msg.arg1).setNaive(nowNaiveCount);
                    }else {
                        nowNaiveCount = Integer.parseInt(bihuQuestionArrayList.get(msg.arg1).getNaive())-1+"";
                        bihuQuestionArrayList.get(msg.arg1).setNaive(nowNaiveCount);
                    }
                    viewHolder.naive.setText(nowNaiveCount+" 踩");
                    break;
                case CHANGEIMAGES:
                    BihuSquareAdapter.ViewHolder viewHolder = (BihuSquareAdapter.ViewHolder)msg.obj;
                    if (msg.arg1==1){
                        bihuQuestionArrayList.get(msg.arg2).setIs_favorite("true");
                        viewHolder.favorite.setImageResource(R.drawable.favorite);
                        BihuAboutMeFragment.bihuSquareAdapter.add(0,bihuQuestionArrayList.get(msg.arg2));
                    }else {
                        bihuQuestionArrayList.get(msg.arg2).setIs_favorite("false");
                        viewHolder.favorite.setImageResource(R.drawable.unfavorite);
                        Message message = new Message();
                        message.what = REFRESH;
                        BihuAboutMeFragment bihuAboutMeFragment = (BihuAboutMeFragment) BihuFragment.bihuFragments.get(1);
                        bihuAboutMeFragment.handler.sendMessage(message);
                    }
                    break;
                case REFRESH:
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
//                                Log.d(TAG, "run: 使用默认账号登录");
//                                Log.d(TAG, "run: 此时token"+BihuFragment.nowUser.getToken());
                                bihuQuestionArrayList = BihuPostTools.initQuestionData(BihuFragment.nowUser.getToken(),"0","20");
                                Collections.reverse(bihuQuestionArrayList);
                                Message message = new Message();
                                message.what = SETRECYCLERVIEWADAPTER;
                                handler.sendMessage(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "网络连接超时!请检查网路连接";
                                handler.sendMessage(message);
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (UnCurrentUserException e) {
                                //用户认证错误
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "身份验证过期,请重新登录";
                                handler.sendMessage(message);
                                Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                                startActivity(intent);
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bihuSquareView = inflater.inflate(R.layout.bihu_square_fragment,container,false);
        swipeRefreshLayout = bihuSquareView.findViewById(R.id.sr_bihu_square);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSkyBlue));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (BihuFragment.nowUser!=null){
                    //网络正常的情况(不是无网络,也不是连接超时
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                            BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                                Log.d(TAG, "run: 使用"+BihuFragment.nowUser.getUsername()+"登录");
                                Log.d(TAG, "run: 此时token"+BihuFragment.nowUser.getToken());
                                bihuQuestionArrayList = BihuPostTools.initQuestionData(BihuFragment.nowUser.getToken(),"0","20");
                                Collections.reverse(bihuQuestionArrayList);
                                Message message = new Message();
                                message.what = SETRECYCLERVIEWADAPTER;
                                handler.sendMessage(message);
                                swipeRefreshLayout.setRefreshing(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (UnCurrentUserException e) {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "身份验证过期,请重新登录";
                                handler.sendMessage(message);
                                Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                                startActivity(intent);
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    //网络不正常的时候
                    try {
                        bihuQuestionArrayList = BihuPostTools.initQuestionDataWithoutNetWork("0");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"当前无网络连接,将使用缓存数据,一些操作将不能进行,要恢复请前往个人中心重新登录",Toast.LENGTH_LONG).show();
                            }
                        });
                        //消息机制放在里面,因为如果上面报错也没有在设置adapter的必要
                        Collections.reverse(bihuQuestionArrayList);
                        Message message = new Message();
                        message.what = SETRECYCLERVIEWADAPTER;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

            }
        });
        swipeRefreshLayout.setRefreshing(true);
        MainActivity.fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (BihuFragment.nowUser!=null){
                    //网络正常的情况(不是无网络,也不是连接超时
                    try {
//                    BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                    Log.d(TAG, "run: 使用"+BihuFragment.nowUser.getUsername()+"登录");
                    Log.d(TAG, "run: 此时token"+BihuFragment.nowUser.getToken());
                        bihuQuestionArrayList = BihuPostTools.initQuestionData(BihuFragment.nowUser.getToken(),"0","20");
                        Collections.reverse(bihuQuestionArrayList);
                        Message message = new Message();
                        message.what = SETRECYCLERVIEWADAPTER;
                        handler.sendMessage(message);
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        Message message = new Message();
                        message.what = SHOWTOASTMESSAGE;
                        message.obj = "身份验证过期,请重新登录";
                        handler.sendMessage(message);
                        Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }else {
                    //网络不正常的时候
                    try {
                        bihuQuestionArrayList = BihuPostTools.initQuestionDataWithoutNetWork("0");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"当前无网络连接,将使用缓存数据,一些操作将不能进行,要恢复请前往个人中心重新登录",Toast.LENGTH_LONG).show();
                            }
                        });
                        //消息机制放在里面,因为如果上面报错也没有在设置adapter的必要
                        Collections.reverse(bihuQuestionArrayList);
                        Message message = new Message();
                        message.what = SETRECYCLERVIEWADAPTER;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

            }
        });
        Log.d(TAG, "onCreateView: 完成广场的view加载");
        return bihuSquareView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isClickThePublishButton){
            swipeRefreshLayout.setRefreshing(true);
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    if (BihuFragment.nowUser!=null){
                        //网络正常的时候
                        try {
//                        BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                            Log.d(TAG, "run: 使用"+BihuFragment.nowUser.getUsername()+"登录");
                            Log.d(TAG, "run: 此时token"+BihuFragment.nowUser.getToken());
                            bihuQuestionArrayList = BihuPostTools.initQuestionData(BihuFragment.nowUser.getToken(),"0","20");
                            Collections.reverse(bihuQuestionArrayList);
                            Message message = new Message();
                            message.what = SETRECYCLERVIEWADAPTER;
                            handler.sendMessage(message);
                            isClickThePublishButton = false;
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (UnCurrentUserException e) {
                            Message message = new Message();
                            message.what = SHOWTOASTMESSAGE;
                            message.obj = "身份验证过期,请重新登录";
                            handler.sendMessage(message);
                            Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                            startActivity(intent);
                            e.printStackTrace();
                        }
                    }else {
                        //网络不正常的时候
                        try {
                            bihuQuestionArrayList = BihuPostTools.initQuestionDataWithoutNetWork("0");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"当前无网络连接,将使用缓存数据,一些操作将不能进行",Toast.LENGTH_SHORT).show();
                                }
                            });
                            //消息机制放在里面,因为如果上面报错也没有在设置adapter的必要
                            Collections.reverse(bihuQuestionArrayList);
                            Message message = new Message();
                            message.what = SETRECYCLERVIEWADAPTER;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                }
            });
        }
    }

    private void initRecyclerView(){
        squareRecyclerView = bihuSquareView.findViewById(R.id.rv_bihu_square);
        bihuSquareAdapter = new BihuSquareAdapter(bihuQuestionArrayList,this.getContext(),mainHandler);
        bihuSquareAdapter.setOnItemClickListener(new BihuSquareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //加载问答详情页面
                Intent intent = new Intent(getContext(),BihuQuestionDetailActivity.class);
                intent.putExtra("thisQuestionIndex",position);
                startActivity(intent);
            }
        });
        bihuSquareAdapter.setExcitingClickListener(this);
        bihuSquareAdapter.setNaiveClickListener(this);
        bihuSquareAdapter.setFavoriteClickListener(this);
        RecyclerViewMyLinearLayoutManager linearLayoutManager = new RecyclerViewMyLinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        squareRecyclerView.setLayoutManager(linearLayoutManager);
        squareRecyclerView.setAdapter(bihuSquareAdapter);
    }


    //item点击事件的处理

    @Override
    public void onExcitingClickListener(final int position, final BihuSquareAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser==null){
            //首先进行nowUser是否为null判断避免报错
            Toast.makeText(getContext(),"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
        }else if (BihuFragment.nowUser.getUsername().equals("temp")){
            //加载登录界面
            Intent intent = new Intent(getContext(),BihuLoginActivity.class);
            startActivity(intent);
        } else{
            //点赞&取消事件处理
            this.viewHolder = viewHolder;
            final BihuQuestion bihuQuestion = bihuQuestionArrayList.get(position);
            String qidString = bihuQuestion.getId();
            final String qid = qidString;
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bihuQuestion.getIs_exciting().equals("false")){
                            if(BihuPostTools.exciting(BihuFragment.nowUser,qid,1)){
                                bihuQuestion.setIs_exciting("true");
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "Exciting!";
                                handler.sendMessage(message);
                                //更改图标
                                Drawable exciting = ContextCompat.getDrawable(getContext(),R.drawable.exciting_fill);
                                Message messageExciting = new Message();
                                messageExciting.what = SETEXCITINGTEXTVIEWICON;
                                messageExciting.obj = exciting;
                                messageExciting.arg1 = position;
                                //如果表示数值增加就是1,减少就是2
                                messageExciting.arg2 = 1;
                                handler.sendMessage(messageExciting);
                            }else {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "出错乐!";
                                handler.sendMessage(message);
                            }
                        } else {
                            if(BihuPostTools.cancelExciting(BihuFragment.nowUser,qid,1)){
                                bihuQuestion.setIs_exciting("false");
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "CancelExciting!";
                                handler.sendMessage(message);
                                //更改图标
                                Drawable exciting = ContextCompat.getDrawable(getContext(),R.drawable.exciting_unfill);
                                Message messageExciting = new Message();
                                messageExciting.what = SETEXCITINGTEXTVIEWICON;
                                messageExciting.obj = exciting;
                                messageExciting.arg1 = position;
                                //如果表示数值增加就是1,减少就是2
                                messageExciting.arg2 = 2;
                                handler.sendMessage(messageExciting);
                            }else {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "出错乐!";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        Message message = new Message();
                        message.what = SHOWTOASTMESSAGE;
                        message.obj = "身份验证过期,请重新登录";
                        handler.sendMessage(message);
                        Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void onNaiveClickListener(final int position, BihuSquareAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser==null){
            Toast.makeText(getContext(),"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
        }else if (BihuFragment.nowUser.getUsername().equals("temp")){
            //加载登录界面
            Intent intent = new Intent(getContext(),BihuLoginActivity.class);
            startActivity(intent);
        }else {
            //踩&取消事件处理
            this.viewHolder = viewHolder;
            Toast.makeText(getContext(),"Naive!",Toast.LENGTH_SHORT).show();
            final BihuQuestion bihuQuestion = bihuQuestionArrayList.get(position);
            String qidString = bihuQuestion.getId();
            final String qid = qidString;
            System.out.println("qid"+qid);
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bihuQuestion.getIs_navie().equals("false")){
                            if (BihuPostTools.naive(BihuFragment.nowUser,qid,1)){
                                bihuQuestion.setIs_navie("true");
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "Naive!";
                                handler.sendMessage(message);
                                //图片更改
                                Message naiveMessage = new Message();
                                Drawable naiveDrawable = ContextCompat.getDrawable(getContext(),R.drawable.naive_fill);
                                naiveMessage.what = SETNAIVETEXTVIEWICON;
                                naiveMessage.arg1 = position;
                                naiveMessage.arg2 = 1;
                                naiveMessage.obj = naiveDrawable;
                                handler.sendMessage(naiveMessage);
                            }else {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "出错乐!";
                                handler.sendMessage(message);
                            }
                        }else {
                            if (BihuPostTools.cancelNaive(BihuFragment.nowUser,qid,1)){
                                bihuQuestion.setIs_navie("false");
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "CancelNaive!";
                                handler.sendMessage(message);
                                //图片更改
                                Message naiveMessage = new Message();
                                Drawable naiveDrawable = ContextCompat.getDrawable(getContext(),R.drawable.naive_unfill);
                                naiveMessage.what = SETNAIVETEXTVIEWICON;
                                naiveMessage.arg1 = position;
                                naiveMessage.arg2 = 2;
                                naiveMessage.obj = naiveDrawable;
                                handler.sendMessage(naiveMessage);
                            }else {
                                Message message = new Message();
                                message.what = SHOWTOASTMESSAGE;
                                message.obj = "出错乐!";
                                handler.sendMessage(message);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        Message message = new Message();
                        message.what = SHOWTOASTMESSAGE;
                        message.obj = "身份验证过期,请重新登录";
                        handler.sendMessage(message);
                        Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //收藏事件处理
    @Override
    public void onFavoriteClickListener(final int position, final BihuSquareAdapter.ViewHolder viewHolder) {
        if (BihuFragment.nowUser==null){
            Toast.makeText(getContext(),"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
        }else if (BihuFragment.nowUser.getUsername().equals("temp")){
            //加载登录界面
            Intent intent = new Intent(getContext(),BihuLoginActivity.class);
            startActivity(intent);
        }else {
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bihuQuestionArrayList.get(position).getIs_favorite().equals("false")){
                            if (BihuPostTools.favorite(BihuFragment.nowUser,Integer.parseInt(bihuQuestionArrayList.get(position).getId()))){
                                //收藏成功
                                Message successfulMessage = new Message();
                                successfulMessage.what = SHOWTOASTMESSAGE;
                                successfulMessage.obj = "收藏成功";
                                handler.sendMessage(successfulMessage);
                                Message change = new Message();
                                change.what = CHANGEIMAGES;
                                change.arg1=1;
                                change.arg2 = position;
                                change.obj = viewHolder;
                                handler.sendMessage(change);
                            }else {
                                //收藏失败
                                Message falseMessage = new Message();
                                falseMessage.what = SHOWTOASTMESSAGE;
                                falseMessage.obj = "收藏失败";
                                handler.sendMessage(falseMessage);
                            }
                        }else{
                            if (BihuPostTools.cancelFavorite(BihuFragment.nowUser,Integer.parseInt(bihuQuestionArrayList.get(position).getId()))){
                                //收藏成功
                                Message successfulMessage = new Message();
                                successfulMessage.what = SHOWTOASTMESSAGE;
                                successfulMessage.obj = "取消收藏成功";
                                handler.sendMessage(successfulMessage);
                                Message change = new Message();
                                change.what = CHANGEIMAGES;
                                change.arg1 = 2;
                                change.arg2 = position;
                                change.obj = viewHolder;
                                handler.sendMessage(change);
                            }else {
                                //收藏失败
                                Message falseMessage = new Message();
                                falseMessage.what = SHOWTOASTMESSAGE;
                                falseMessage.obj = "取消收藏失败";
                                handler.sendMessage(falseMessage);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        Message message = new Message();
                        message.what = SHOWTOASTMESSAGE;
                        message.obj = "身份验证过期,请重新登录";
                        handler.sendMessage(message);
                        Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
