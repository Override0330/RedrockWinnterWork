package com.redrockwork.overrdie.firstdemo;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.redrockwork.overrdie.firstdemo.activity.ActivityFragment;
import com.redrockwork.overrdie.firstdemo.bihu.BihuFragment;
import com.redrockwork.overrdie.firstdemo.gank.GankFragment;
import com.redrockwork.overrdie.firstdemo.message.MessageFragment;
import com.redrockwork.overrdie.firstdemo.tools.ToolsFragment;
import com.redrockwork.overrdie.firstdemo.xiandu.XianduFragment;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    public static TabLayout mTabLayout;
    private ImageView header;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView naviagtion = findViewById(R.id.iv_navigation);
        mTabLayout = findViewById(R.id.tl_toolbar);
        header = findViewById(R.id.iv_toolbar_head);
        Resources r = this.getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.user);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(is);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        final Bitmap newBitmap = cutToCircle(bitmap);
        header.setImageBitmap(newBitmap);
//        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
//        setSupportActionBar(mainToolbar);
//
//        mTabLayout = findViewById(R.id.tl_main);
//
        final DrawerLayout mDrawerLayout = findViewById(R.id.drawerlayout_main);
//        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mainToolbar,
//                R.string.drawer_open, R.string.drawer_close);
//        mDrawerToggle.syncState();
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mainToolbar.setNavigationIcon(R.drawable.baseline_sort_black_18dp);

        naviagtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavigtionView的布局在被第一次打开后才被创建
                mDrawerLayout.openDrawer(Gravity.LEFT);
                //在这里初始化所有的NavigationView的资源
                ImageView naviagtionHeader = findViewById(R.id.iv_header);
                naviagtionHeader.setImageBitmap(newBitmap);
            }
        });

        NavigationView mNavigationView = findViewById(R.id.navigation_main);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.it_news:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new GankFragment()).commit();
                        break;
                    case R.id.it_xiandu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new XianduFragment()).commit();
                        break;
                    case R.id.it_answer:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new BihuFragment()).commit();
                        break;
                    case R.id.it_messsage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new MessageFragment()).commit();
                        break;
                    case R.id.it_activity:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new ActivityFragment()).commit();
                        break;
                    case R.id.it_tools:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new ToolsFragment()).commit();
                        break;
                    case R.id.it_about:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new AboutFragment()).commit();
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return false;
            }
        });


    }

    public Bitmap cutToCircle(Bitmap bitmap){
        int radius = 0;
        if (bitmap.getWidth()>bitmap.getHeight()){
            radius = bitmap.getHeight();
        }else {
            radius = bitmap.getWidth();
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0,0,radius,radius);
        canvas.drawRoundRect(rectF,radius/2,radius/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,null,rectF,paint);
        return newBitmap;
    }
}
