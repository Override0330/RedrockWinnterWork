package com.redrockwork.overrdie.bihu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redrockwork.overrdie.bihu.pensonalcenter.PersonalCenterFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuLoginActivity;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.BihuQuestionPublishActivity;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;
import com.redrockwork.overrdie.bihu.gank.GankFragment;
import com.redrockwork.overrdie.bihu.xiandu.XianduFragment;

import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity{
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    public static TabLayout mTabLayout;
    public static ImageView header,naviagtionHeader;
    private TextView userName;
    public static TextView title;
    private static Context context;
    public static Handler mainHandler = new Handler();
    public static Bitmap avator;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static  ImageView publish;
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(context,(String)msg.obj,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("userdata",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        publish = findViewById(R.id.iv_message);
        context = this;
        final ImageView naviagtion = findViewById(R.id.iv_navigation);
        mTabLayout = findViewById(R.id.tl_toolbar);
        header = findViewById(R.id.iv_toolbar_head);
        title = findViewById(R.id.tv_toolbar_title);
        final DrawerLayout mDrawerLayout = findViewById(R.id.drawerlayout_main);
        final NavigationView mNavigationView = findViewById(R.id.navigation_main);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationView.getMenu().getItem(0).setChecked(true);
//        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
//        setSupportActionBar(mainToolbar);
//
//        mTabLayout = findViewById(R.id.tl_main);
//

//        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mainToolbar,
//                R.string.drawer_open, R.string.drawer_close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mainToolbar.setNavigationIcon(R.drawable.baseline_sort_black_18dp);

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //判断登录的账号
                    if (sharedPreferences.getString("lastUserName","temp").equals("temp")){
                        BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"当前使用游客账号登录",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        String [] value = {sharedPreferences.getString("lastUserName","temp"),sharedPreferences.getString("lastUserPassword","huanglong2019")};
                        BihuFragment.nowUser = BihuPostTools.login(value);
                        if (BihuFragment.nowUser == null){
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context," 身份验证过期,请重新登录!当前使用游客账号登录",Toast.LENGTH_SHORT).show();
                                }
                            });
                            BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                        }
                    }
                    if (BihuFragment.nowUser==null){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                header.setImageResource(R.drawable.without_network);
                            }
                        });
                    }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                        //若是默认账号则设置默认头像
                        Bitmap bitmap = MyImageTools.changeToBitmap(R.drawable.defultuser,context);
                        avator = MyImageTools.cutToCircle(bitmap);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                header.setImageBitmap(avator);
                            }
                        });
                    }else {
                        Log.v("加载图像","开始加载用户头像"+BihuFragment.nowUser.getUsername());
                        Bitmap bitmap = MyImageTools.getBitmap(BihuFragment.nowUser.getAvatar());
                        if (bitmap!=null){
                            avator = MyImageTools.cutToCircle(bitmap);
                        }else {
                            bitmap = MyImageTools.changeToBitmap(R.drawable.defultuser,context);
                            avator = MyImageTools.cutToCircle(bitmap);
                        }
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (avator!=null)
                                header.setImageBitmap(avator);
                                Animation animation = AnimationUtils.loadAnimation(context,R.anim.fadein);
                                header.startAnimation(animation);
                                Toast.makeText(context,BihuFragment.nowUser.getUsername()+" 欢迎进入bi乎",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }catch (UnknownHostException e){
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            header.setImageResource(R.drawable.without_network);
                            avator = MyImageTools.changeToBitmap(R.drawable.without_network,context);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"当前无网络链接,将使用缓存数据,部分服务将不可用",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        naviagtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavigtionView的布局在被第一次打开后才被创建
                mDrawerLayout.openDrawer(Gravity.LEFT);
                //在这里初始化所有的NavigationView的资源
                naviagtionHeader = findViewById(R.id.iv_header);
                userName = findViewById(R.id.tv_header);
                if (BihuFragment.nowUser==null){
                    if (sharedPreferences.getString("lastUserName","temp").equals("temp")){
                        userName.setText("未登录(离线)");
                    }else {
                        userName.setText(sharedPreferences.getString("lastUserName","temp")+"\n(离线)");
                    }
                    naviagtionHeader.setImageResource(R.drawable.without_network);
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,BihuLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }else if(BihuFragment.nowUser.getUsername().equals("temp")){
                    userName.setText("游客\n请登录");
                    naviagtionHeader.setImageResource(R.drawable.defultuser);
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,BihuLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }else {
                    userName.setText(BihuFragment.nowUser.getUsername());
                    naviagtionHeader.setImageBitmap(avator);
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new PersonalCenterFragment()).commit();
                            mDrawerLayout.closeDrawers();
                        }
                    });
                }
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavigtionView的布局在被第一次打开后才被创建
                mDrawerLayout.openDrawer(Gravity.LEFT);
                //在这里初始化所有的NavigationView的资源
                naviagtionHeader = findViewById(R.id.iv_header);
                userName = findViewById(R.id.tv_header);
                if (BihuFragment.nowUser==null){
                    if (sharedPreferences.getString("lastUserName","temp").equals("temp")){
                        userName.setText("未登录(离线)");
                    }else {
                        userName.setText(sharedPreferences.getString("lastUserName","temp")+"\n(离线)");
                    }
                    naviagtionHeader.setImageResource(R.drawable.without_network);
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,BihuLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }else if(BihuFragment.nowUser.getUsername().equals("temp")){
                    userName.setText("游客\n请登录");
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context,BihuLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    naviagtionHeader.setImageResource(R.drawable.defultuser);
                }else {
                    userName.setText(BihuFragment.nowUser.getUsername());
                    naviagtionHeader.setImageBitmap(avator);
                    naviagtionHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new PersonalCenterFragment()).commit();
                            mDrawerLayout.closeDrawers();
                        }
                    });
                }
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                        //gank新闻
                    case R.id.it_news:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new GankFragment()).commit();
                        publish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BihuFragment.nowUser==null){
                                    //首先进行nowUser是否为null判断避免报错
                                    Toast.makeText(context,"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                                    //加载登录界面
                                    Intent intent = new Intent(context,BihuLoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context,BihuQuestionPublishActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                        //闲读
                    case R.id.it_xiandu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new XianduFragment()).commit();
                        publish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BihuFragment.nowUser==null){
                                    //首先进行nowUser是否为null判断避免报错
                                    Toast.makeText(context,"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                                    //加载登录界面
                                    Intent intent = new Intent(context,BihuLoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context,BihuQuestionPublishActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                        //逼乎
                    case R.id.it_answer:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new BihuFragment()).commit();
                        publish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BihuFragment.nowUser==null){
                                    //首先进行nowUser是否为null判断避免报错
                                    Toast.makeText(context,"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                                    //加载登录界面
                                    Intent intent = new Intent(context,BihuLoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context,BihuQuestionPublishActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                        //个人中心
                    case R.id.it_about_me:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new PersonalCenterFragment()).commit();
                        publish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BihuFragment.nowUser==null){
                                    //首先进行nowUser是否为null判断避免报错
                                    Toast.makeText(context,"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                                    //加载登录界面
                                    Intent intent = new Intent(context,BihuLoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context,BihuQuestionPublishActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                        //关于该app
                    case R.id.it_about:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new AboutFragment()).commit();
                        publish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BihuFragment.nowUser==null){
                                    //首先进行nowUser是否为null判断避免报错
                                    Toast.makeText(context,"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                                    //加载登录界面
                                    Intent intent = new Intent(context,BihuLoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context,BihuQuestionPublishActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        //设置默认启动的Fragment
        if (savedInstanceState == null) {
            mNavigationView.getMenu().performIdentifierAction(R.id.it_news, 0);
        }
        //运行时权限(假233333
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"授权成功,可以进行图片缓存和发布图片",Toast.LENGTH_SHORT).show();
                }else {
                    //Toast
                    Toast.makeText(this,"授权失败,无法进行图片缓存和发布图片",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
