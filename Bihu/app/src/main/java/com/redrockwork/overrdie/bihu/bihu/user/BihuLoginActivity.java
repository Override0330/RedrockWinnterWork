package com.redrockwork.overrdie.bihu.bihu.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.obj.User;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;
import com.redrockwork.overrdie.bihu.pensonalcenter.PersonalCenterFragment;

import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class BihuLoginActivity extends AppCompatActivity {
    private EditText username, password;
    private TextView register;
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
        register = findViewById(R.id.tv_login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BihuRegisterActivity.class);
                startActivity(intent);
            }
        });
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        login = findViewById(R.id.bt_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MainActivity.fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String[] value = {username.getText().toString(), password.getText().toString()};
                            User user = BihuPostTools.login(value);
                            if (user != null) {
                                //成功
                                BihuFragment.nowUser = BihuPostTools.login(value);
                                MainActivity.editor.putString("lastUserName", username.getText().toString());
                                MainActivity.editor.putString("lastUserPassword", password.getText().toString());
                                MainActivity.editor.commit();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "登录成功,正在初始化数据,请稍后", Toast.LENGTH_SHORT).show();
                                        MainActivity.userName.setText(BihuFragment.nowUser.getUsername());
                                    }
                                });
                                Bitmap bitmap = MyImageTools.getBitmap(BihuFragment.nowUser.getAvatar());
                                if (bitmap != null) {
                                    final Bitmap avator = MyImageTools.cutToCircle(bitmap);
                                    MainActivity.avator = bitmap;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                                            MainActivity.header.setImageBitmap(avator);
                                            MainActivity.header.startAnimation(animation);
                                            MainActivity.naviagtionHeader.setImageBitmap(avator);
                                            MainActivity.naviagtionHeader.startAnimation(animation);
                                            MainActivity.avator = avator;
                                        }
                                    });
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.header.setImageResource(R.drawable.defultuser);
                                            MainActivity.naviagtionHeader.setImageResource(R.drawable.defultuser);
                                            MainActivity.avator = MyImageTools.changeToBitmap(R.drawable.defultuser, context);
                                        }
                                    });
                                }
                                PersonalCenterFragment.isLogin = true;
                                finish();
                            } else {
                                //失败
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "当前无网络连接,请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "网络连接超时,请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
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
        if (isFromRegister) {
            isFromRegister = false;
            PersonalCenterFragment.isLogin = true;
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
            MainActivity.avator = MyImageTools.changeToBitmap(R.drawable.defultuser,context);
            MainActivity.header.setImageResource(R.drawable.defultuser);
            MainActivity.header.startAnimation(animation);
            MainActivity.naviagtionHeader.setImageResource(R.drawable.defultuser);
            MainActivity.naviagtionHeader.startAnimation(animation);
            MainActivity.userName.setText(BihuFragment.nowUser.getUsername());
            finish();
        }
        //nmd终于找到bug了,如果身份验证失败的话会导致nowUser不是null,在身份验证失败后加入一个login默认账号的逻辑就好了
        if (BihuFragment.nowUser != null && !BihuFragment.nowUser.getUsername().equals("temp")) {
            finish();
        }
    }
}
