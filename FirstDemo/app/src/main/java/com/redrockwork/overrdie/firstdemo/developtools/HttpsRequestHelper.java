package com.redrockwork.overrdie.firstdemo.developtools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * 封装好的网络请求类,默认使用GET方法,构造器设置url,返回一个Recall对象,会抛出超时异常和另一个超时之外的异常.
 */
public class HttpsRequestHelper {
    private String url = null;
    private int method = 0;
    public static final int GET = 0;
    public static final int POST = 1;
    private String [] key;
    private String [] value;
    private int responseCode;

    public HttpsRequestHelper(String url) {
        this.url = url;
    }

    public void setMethod(int method){
        this.method = method;
    }

    public void setPostArguments(String [] key,String [] value){
        this.key = key;
        this.value = value;
    }

    public Recall start() throws IOException, TimeoutException, JSONException {

        if (method==0){
            return new Recall(get(url),responseCode);
        }else {
            return new Recall(post(url,key,value),responseCode);
        }

    }
    public String get(String url) throws IOException, TimeoutException, JSONException {
        HttpURLConnection connection = null;
        try {
            URL mURL = new URL(url);
            long start = System.currentTimeMillis();
            connection = (HttpURLConnection) mURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            responseCode = connection.getResponseCode();
            long end = System.currentTimeMillis();
            if ((end - start) > 5000) throw new TimeoutException();
            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                String response = new JSONObject(getStringFromInputStream(is)).toString();
                return response;
            } else {
                throw new TimeoutException();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new TimeoutException("请求超时,请检查网络连接");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JSONException("网络请求格式错误");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String post(String url,String [] key,String[] value) throws IOException, TimeoutException, JSONException {
        HttpURLConnection connection = null;
        try {
            URL mURL = new URL(url);
            String data = "";
            long start = System.currentTimeMillis();
            connection = (HttpURLConnection) mURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            for (int i = 0; i < key.length;i++) {
                data = data+key[i]+"="+value[i];
                if (i!=key.length-1){
                    data = data+"&";
                }
            }
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", data.length()+"");
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(data.getBytes());

            responseCode = connection.getResponseCode();
            long end = System.currentTimeMillis();
            if ((end - start) > 5000) throw new TimeoutException();
            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                String response = new JSONObject(getStringFromInputStream(is)).toString();
                return response;
            } else {
                throw new TimeoutException();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new TimeoutException("请求超时,请检查网络连接");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private String getStringFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }

}
