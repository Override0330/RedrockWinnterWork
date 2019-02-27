package com.redrockwork.overrdie.firstdemo.bihu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.redrockwork.overrdie.firstdemo.MainActivity;
import com.redrockwork.overrdie.firstdemo.R;
import com.redrockwork.overrdie.firstdemo.developtools.MyImageTools;
import com.redrockwork.overrdie.firstdemo.pensonalcenter.PersonalCenterFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BihuLoginActivity extends AppCompatActivity {
    private EditText username,password;
    private Button login;
    private ImageView back;
    private Handler handler = new Handler();
    private Context context = this;
    public static boolean isFromRegister = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bihu_login);
        username = findViewById(R.id.et_login_username);
        password = findViewById(R.id.et_login_password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login = findViewById(R.id.bt_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MainActivity.fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String [] value = {username.getText().toString(),password.getText().toString()};
                            User user = BihuPostTools.login(value);
                            if (user!=null){
                                //成功
                                BihuFragment.nowUser = BihuPostTools.login(value);
                                MainActivity.editor.putString("lastUserName",username.getText().toString());
                                MainActivity.editor.putString("lastUserPassword",password.getText().toString());
                                MainActivity.editor.commit();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Bitmap bitmap = MyImageTools.getBitmap(BihuFragment.nowUser.getAvatar());
                                if (bitmap!=null){
                                    final Bitmap avator = MyImageTools.cutToCircle(bitmap);
                                    MainActivity.avator = bitmap;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.header.setImageBitmap(avator);
                                            MainActivity.naviagtionHeader.setImageBitmap(avator);
                                            MainActivity.avator = avator;
                                        }
                                    });
                                }
                                PersonalCenterFragment.isLogin = true;
                                finish();
                            }else {
                                //失败
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"登录失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
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
        });
        back = findViewById(R.id.iv_login_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是不是从注册哪里过来的
        if (isFromRegister){
            isFromRegister = false;
            finish();
        }
    }
}
