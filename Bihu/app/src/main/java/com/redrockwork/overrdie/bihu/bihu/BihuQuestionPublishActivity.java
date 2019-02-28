package com.redrockwork.overrdie.bihu.bihu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.redrockwork.overrdie.bihu.MainActivity;
import com.redrockwork.overrdie.bihu.R;
import com.redrockwork.overrdie.bihu.developtools.MyImageTools;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class BihuQuestionPublishActivity extends AppCompatActivity {
    private ImageView backButton,add;
    private TextView title,content;
    private Button publish;
    private LinearLayout imagesView;
    private Context context = this;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<Bitmap> bitmapsArrayList = new ArrayList<>();
    private final static int SHOWTOASTMESSAGE = 0;
    private final static int CHOOSE_PHOTO = 1;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOWTOASTMESSAGE:
                    Toast.makeText(context, (String) msg.obj,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_publish);
        backButton = findViewById(R.id.iv_question_publish_back);
        title = findViewById(R.id.et_publish_question_title);
        content = findViewById(R.id.et_publish_question_content);
        publish = findViewById(R.id.bt_publish_question_yes);
        add = findViewById(R.id.iv_default);
        imagesView = findViewById(R.id.ll_question_publish_images);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"点击了发布按钮",Toast.LENGTH_SHORT).show();
                if (BihuFragment.nowUser!=null){
                    //用户属性正常
                    final String titleString = title.getText().toString();
                    final String contentString = content.getText().toString();
                    //加入问题图片的处理
                    for (int i = 0; i < bitmapsArrayList.size(); i++) {
                        final int finalI = i;
                        final int finalI1 = i;
                        MainActivity.fixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = bitmapsArrayList.get(finalI);
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                                File file = MyImageTools.saveBitmapFile(bitmap,df.format(new Date())+ finalI1);
                                MyImageTools.postFileToQiniu(file,df.format(new Date())+ finalI1);
                                images.add("http://pnffhnnkk.bkt.clouddn.com/"+df.format(new Date())+ finalI1);
                            }
                        });
                    }
                    //等待图片上传完毕
                    while(images.size()!=bitmapsArrayList.size()){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //发布问题处理
                    if (!titleString.equals("")&&!contentString.equals("")){
                        MainActivity.fixedThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (publishQuestion(titleString,contentString)){
                                        Log.i("发布","成功");
                                        //成功Toast
                                        Message successfulMessage = new Message();
                                        successfulMessage.what = SHOWTOASTMESSAGE;
                                        successfulMessage.obj = "发布成功";
                                        handler.sendMessage(successfulMessage);
                                        BihuSquareFragment.isClickThePublishButton = true;
                                        finish();
                                    }else {
                                        Log.i("发布","失败");
                                        //失败Toast
                                        Message falseMessge = new Message();
                                        falseMessge.what = SHOWTOASTMESSAGE;
                                        falseMessge.obj = "发布失败,网络链接可能已经断开,请重试";
                                        handler.sendMessage(falseMessge);
                                    }
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (UnCurrentUserException e) {
                                    //身份验证失败
                                    disposeUnCurrentUser();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else {
                        //内容为空提示
                        Message emptyMessage = new Message();
                        emptyMessage.what = SHOWTOASTMESSAGE;
                        emptyMessage.obj = "标题和内容不能为空";
                        handler.sendMessage(emptyMessage);
                    }
                }else {
                    //进入离线模式
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提示框提示用户不会保存数据或者自己实现保存数据
                finish();
            }
        });
        //添加图片
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"点击了添加图片的按钮",Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
    }

    private boolean publishQuestion(String title,String content) throws TimeoutException, JSONException, IOException, UnCurrentUserException {
        if (BihuPostTools.publishQuestion(BihuFragment.nowUser,title,content,images)){
            return true;
        }
        return false;
    }

    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    //Toast
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    if (Build.VERSION.SDK_INT >=19){
                        //4.4up
                        handleImageOnKitKat(data);
                    }else{
                        //4.4down
                        handlerImageBeforeKiKat(data);
                    }
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        //根据图片路径添加图片displayImage
        addImage(imagePath);
    }

    private void handlerImageBeforeKiKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        //根据图片路径添加图片displayImage
        addImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor !=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void addImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmapsArrayList.add(bitmap);
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(3,0,3,0);
            imagesView.addView(imageView);
        }
    }

    private void disposeUnCurrentUser(){
        Message message = new Message();
        message.what = SHOWTOASTMESSAGE;
        message.obj = "身份验证过期,请重新登录";
        handler.sendMessage(message);
        try {
            BihuFragment.nowUser = BihuPostTools.login(BihuFragment.defaultUserInformation);
            MainActivity.avator = MyImageTools.changeToBitmap(R.drawable.defultuser,context);
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
        Intent intent = new Intent(context,BihuLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
