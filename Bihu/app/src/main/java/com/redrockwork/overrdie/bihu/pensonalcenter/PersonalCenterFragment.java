package com.redrockwork.overrdie.bihu.pensonalcenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuLoginActivity;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.UnCurrentUserException;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class PersonalCenterFragment extends Fragment {
    private View personalCenter;
    private ImageView userAvatar;
    private TextView userName,changePassword,quit;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    public static boolean isChangeAvatar = false;
    public static boolean isLogin = false;
    public static Bitmap tempBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        personalCenter = inflater.inflate(R.layout.personal_center_fragment,container,false);
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.title.setText("个人中心");
        //加入一个个人中心的tab
//        swipeRefreshLayout = personalCenter.findViewById(R.id.sr_personal);
//        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorSkyBlue));
        userAvatar = personalCenter.findViewById(R.id.iv_user_avatar);
        userAvatar.setImageBitmap(MainActivity.avator);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser==null){
                    Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                    startActivity(intent);
                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                    //加载登录界面
                    Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getContext(),ChangeAvatarActivity.class);
                    startActivity(intent);
                }
            }
        });
        userName = personalCenter.findViewById(R.id.tv_userName);
        if (BihuFragment.nowUser==null){
            userName.setText(MainActivity.sharedPreferences.getString("lastUserName","无网络链接")+"(离线)");
        }else if (BihuFragment.nowUser.getUsername().equals("temp")){
            userName.setText("游客,点击登录");
        }else {
            userName.setText(BihuFragment.nowUser.getUsername());
        }
        changePassword = personalCenter.findViewById(R.id.tv_change_password);
        Drawable passwordDrawable = ContextCompat.getDrawable(getContext(),R.drawable.password);
        passwordDrawable.setBounds(0,0,50,50);
        changePassword.setCompoundDrawables(passwordDrawable,null,null,null);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser==null){
                    //首先进行nowUser是否为null判断避免报错
                    Toast.makeText(getContext(),"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                    //加载登录界面
                    Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                    startActivity(intent);
                }else {
                    //更改密码activity
                    Intent intent = new Intent(getContext(),ChangePasswordActivity.class);
                    startActivity(intent);
                }

            }
        });
        quit = personalCenter.findViewById(R.id.tv_quit);
        Drawable quitDrawable = getResources().getDrawable(R.drawable.quit);
        quitDrawable.setBounds(0,0,50,50);
        quit.setCompoundDrawables(quitDrawable,null,null,null);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BihuFragment.nowUser==null){
                    //首先进行nowUser是否为null判断避免报错
                    Toast.makeText(getContext(),"当前无网络连接,无法进行该操作",Toast.LENGTH_SHORT).show();
                }else if (BihuFragment.nowUser.getUsername().equals("temp")){
                    //加载登录界面
                    Toast.makeText(getContext(),"你还没登录奥",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                    startActivity(intent);
                }else {
                    //退出登录
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
                                MainActivity.editor.putString("lastUserName","temp");
                                MainActivity.editor.putString("lastUserPassword","huanglong2019");
                                MainActivity.editor.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    userAvatar.setImageResource(R.drawable.defultuser);
                                    MainActivity.header.setImageResource(R.drawable.defultuser);
                                    userName.setText("游客,点击登录");
                                }
                            });
                        }
                    });

                }
            }
        });
        return personalCenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isChangeAvatar){
//            swipeRefreshLayout.setRefreshing(true);
            MainActivity.fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //本地先备份,加载的时候就不需要重新网络请求了
                    String fileName = df.format(new Date());
                    File file = MyImageTools.saveBitmapFile(tempBitmap,fileName);
                    MyImageTools.postFileToQiniu(file,fileName);
                    String url = "http://pnffhnnkk.bkt.clouddn.com/"+fileName;
                    try {
                        BihuPostTools.modifyAvatar(BihuFragment.nowUser,url);
//                        swipeRefreshLayout.setRefreshing(false);
                        isChangeAvatar = false;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"更改成功!",Toast.LENGTH_SHORT);
                                Bitmap bitmap = MyImageTools.cutToCircle(tempBitmap);
                                MainActivity.avator = bitmap;
                                MainActivity.naviagtionHeader.setImageBitmap(bitmap);
                                MainActivity.header.setImageBitmap(bitmap);
                                userAvatar.setImageBitmap(bitmap);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnCurrentUserException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"身份验证过期,请重新登录",Toast.LENGTH_SHORT);
                                Intent intent = new Intent(getContext(),BihuLoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        }
        if (isLogin){
            userAvatar.setImageBitmap(MainActivity.avator);
            userName.setText(BihuFragment.nowUser.getUsername());
            isLogin = false;
        }
    }
}
