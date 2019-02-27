package com.redrockwork.overrdie.bihu.developtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class MyImageTools {
    public static Bitmap loderFromNetWork(String url){
        Log.d(TAG, "loderFromNetWork: 开始从网络加载图片,url:"+url);
        HttpURLConnection connection = null;
        URL mUrl;
        try {
            mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(30000);
            int responseCode = connection.getResponseCode();
            if (responseCode==200){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inPreferredConfig=Bitmap.Config.ARGB_4444;
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                inputStream.close();
                String [] temp1 = url.split("/");
                String temp2 = temp1[temp1.length-1];
                String [] temp3 = temp2.split(".");
                String name;
                if (temp3.length>1){
                    name = temp3[0];
                }else {
                    name = temp2;
                }
                saveBitmapFile(bitmap,name);
                return bitmap;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
    public static Bitmap cutToCircle(Bitmap bitmap){
        int radius = 0;
        if (bitmap.getWidth()>bitmap.getHeight()){
            radius = bitmap.getHeight();
        }else {
            radius = bitmap.getWidth();
        }
        //先剪成正方形!
        Bitmap newBitmap = Bitmap.createBitmap(radius,radius,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0,0,radius,radius);
        //剪成圆形!
        canvas.drawRoundRect(rectF,radius/2,radius/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,null,rectF,paint);
        return newBitmap;
    }

    public static Bitmap changeToBitmap(int resoure, Context context){
        Resources r = context.getResources();
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(resoure);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(is);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    public static File saveBitmapFile(Bitmap bitmap,String name){
        //设置保存路径,如果没有这个路径要先创建!!!
        File dirPath = new File("/mnt/sdcard/pic/");
        if (!dirPath.exists()){
            dirPath.mkdir();
        }
        File file = new File("/mnt/sdcard/pic/"+name+".jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void postFileToQiniu(File file,String key){
        //生成上传token
        String accessKey = "lvhc4vYVNRu8J26GtLJOKbAAJQEQHVZcdGHt-57e";
        String secretKey = "LqcSe1xtgb5_UbAiC3CSYkYJLIkNR0GhwPnOZpKO";
        Auth auth = Auth.create(accessKey,secretKey);
        String token = auth.uploadToken("images");
        //开始上传
        Configuration configuration = new Configuration.Builder()
                .zone(FixedZone.zone0)
                .build();
        UploadManager uploadManager = new UploadManager(configuration);
        uploadManager.put(file, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()){
                    Log.i("七牛图床","上传成功");
                }else {
                    Log.i("七牛图床","上传失败");
                }
                Log.i("七牛图床",key+",\r\n"+info+",\r\n"+response);
            }
        },null);
    }

    public static File findFile(String baseDirName,String fileName){
        File dir = new File(baseDirName);
        File [] files = dir.listFiles();
        if (files!=null){
            if (files.length==0){
                Log.i(TAG, "findFile: 空文件夹");
                return null;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(fileName)){
                    return files[i].getAbsoluteFile();
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmap(String url){
        String [] temp1 = url.split("/");
        String temp2 = temp1[temp1.length-1];
        String [] temp3 = temp2.split(".");
        String name;
        if (temp3.length>1){
            name = temp3[0];
        }else {
            name = temp2;
        }
        Bitmap bitmap;
        File findResult = findFile("/mnt/sdcard/pic/",name+".jpg");
        if (findResult!=null){
            bitmap = BitmapFactory.decodeFile(findResult.getPath());
            Log.i(TAG, "getBitmap: 图片从内存中加载");
        }else {
            bitmap = loderFromNetWork(url);
        }
        return bitmap;
    }
}
