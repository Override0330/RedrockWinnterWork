package com.redrockwork.overrdie.bihu.pensonalcenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuLoginActivity;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.UnCurrentUserException;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextView userName;
    private EditText lastPassword,password,passwordAgain;
    private Button change;
    private ImageView back;
    private Handler mainHandler = new Handler();
    private Context context = this;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        userName = findViewById(R.id.tv_change_password_username);
        userName.setText("当前用户: "+BihuFragment.nowUser.getUsername());
        Drawable passwordDrawable = ContextCompat.getDrawable(this,R.drawable.password_morelittle);
        passwordDrawable.setBounds(0,0,30,30);
        lastPassword = findViewById(R.id.et_change_password_last_password);
        lastPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        lastPassword.setCompoundDrawables(passwordDrawable,null,null,null);
        password = findViewById(R.id.et_change_password_password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password.setCompoundDrawables(passwordDrawable,null,null,null);
        passwordAgain = findViewById(R.id.et_change_password_password_again);
        passwordAgain.setCompoundDrawables(passwordDrawable,null,null,null);
        passwordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
        back = findViewById(R.id.iv_change_password_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        change = findViewById(R.id.bt_change_password);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (BihuFragment.nowUser!=null){
                    MainActivity.fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            String [] value = {BihuFragment.nowUser.getUsername(),lastPassword.getText().toString()};
                            try {
                                BihuFragment.nowUser = BihuPostTools.login(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (BihuFragment.nowUser!=null){
                                //输入旧密码正确
                                if (password.getText().toString().equals(passwordAgain.getText().toString())){
                                    //两次新密码一致
                                    try {
                                        if (BihuPostTools.changePassword(BihuFragment.nowUser,password.getText().toString())){
                                            String [] newValue = {BihuFragment.nowUser.getUsername(),password.getText().toString()};
                                            BihuFragment.nowUser = BihuPostTools.login(newValue);
                                            MainActivity.editor.putString("lastUserPassword",password.getText().toString());
                                            MainActivity.editor.commit();
                                            //更改成功
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context,"更改成功",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });
                                        }else {
                                            //不成功
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context,"更改失败,网络链接可能已经断开,请重试",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
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
                                                Toast.makeText(context,"身份验证过期,请重新登录",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context,BihuLoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                }else {
                                    //两次新密码不一致
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            password.setText("");
                                            passwordAgain.setText("");
                                            Toast.makeText(context,"两次密码不一致!请重新输入",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else {
                                //输入旧密码错误
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastPassword.setText("");
                                        password.setText("");
                                        passwordAgain.setText("");
                                        Toast.makeText(context,"旧密码错误或网络连接已断开",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    //突然间没有网络链接
                    Message message = new Message();
                    message.what = 1;
                    message.obj = "网络链接已经断开!进入离线模式";
                    MainActivity.handler.sendMessage(message);
                    BihuFragment.nowUser = null;
                    MainActivity.mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.header.setImageResource(R.drawable.without_network);
                        }
                    });
                }

            }
        });
    }

}
