package com.redrockwork.overrdie.bihu.bihu.user;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.bihu.BihuFragment;
import com.redrockwork.overrdie.bihu.bihu.BihuPostTools;
import com.redrockwork.overrdie.bihu.bihu.obj.User;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BihuRegisterActivity extends AppCompatActivity {
    private EditText username, password, passwordAgain;
    private Button login;
    private ImageView back;
    private Handler handler = new Handler();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bihu_register);
        username = findViewById(R.id.et_register_username);
        password = findViewById(R.id.et_register_password);
        passwordAgain = findViewById(R.id.et_register_password_again);
        login = findViewById(R.id.bt_register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (password.getText().toString().equals(passwordAgain.getText().toString())) {
                    if (username.getText().toString().toCharArray().length<12){
                        MainActivity.fixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String[] value = {username.getText().toString(), password.getText().toString()};
                                    User user = BihuPostTools.register(value);
                                    if (user != null) {
                                        //成功
                                        BihuFragment.nowUser = BihuPostTools.login(value);
                                        MainActivity.editor.putString("lastUserName", username.getText().toString());
                                        MainActivity.editor.putString("lastUserPassword", password.getText().toString());
                                        MainActivity.editor.commit();
                                        MainActivity.avator = MyImageTools.getBitmap(BihuFragment.nowUser.getAvatar());
                                        BihuLoginActivity.isFromRegister = true;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        finish();
                                    } else {
                                        //失败
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
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
                    }else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "名字不要超过12个字符奥", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else{
                    Toast.makeText(context, "两次密码不一样奥", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    passwordAgain.setText("");
                }
            }
        });
        back = findViewById(R.id.iv_register_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
