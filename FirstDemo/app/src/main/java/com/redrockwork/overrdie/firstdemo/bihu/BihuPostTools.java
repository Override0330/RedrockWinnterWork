package com.redrockwork.overrdie.firstdemo.bihu;

import com.redrockwork.overrdie.firstdemo.developtools.HttpsRequestHelper;
import com.redrockwork.overrdie.firstdemo.developtools.Recall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class BihuPostTools {
    private static String baseUrl = "http://bihu.jay86.com/";
    public static ArrayList<BihuQuestion> initQuestionData(String token,String page,String count) throws JSONException, TimeoutException, IOException {
        ArrayList<BihuQuestion> bihuQuestionArrayList = new ArrayList<>();
        String [] key = {"token","page","count"};
        String [] value = {token,page,count};
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"getQuestionList.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        JSONObject mainJson = new JSONObject(recall.getJson());
        String dataString = mainJson.getString("data");
        JSONObject dataJson = new JSONObject(dataString);
        JSONArray questionsJson = new JSONArray(dataJson.getString("questions"));
        for (int i = 0; i < questionsJson.length(); i++) {
            JSONObject questionJson = new JSONObject(questionsJson.get(i).toString());
            String id = questionJson.getString("id");
            String title = questionJson.getString("title");
            String content = questionJson.getString("content");
            String imageUrls = questionJson.getString("images");
            String time = questionJson.getString("date");
            String exciting = questionJson.getString("exciting");
            String naive = questionJson.getString("naive");
            String recent = questionJson.getString("recent");
            String answerCount = questionJson.getString("answerCount");
            String authorName = questionJson.getString("authorName");
            String authorAvatar = questionJson.getString("authorAvatar");
            String isExciting = questionJson.getString("is_exciting");
            String isNaive = questionJson.getString("is_naive");
            String isFavorite = questionJson.getString("is_favorite");
            bihuQuestionArrayList.add(new BihuQuestion(id,title,content,time,exciting,naive,answerCount,imageUrls,recent,
                    authorName,authorAvatar,isExciting,isNaive,isFavorite));
        }
        return bihuQuestionArrayList;
    }


    /**
     * 登录方法
     * @param value
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */

    public static User login(String [] value) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"login.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = new String[]{"username","password"};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        User user = null;
        if (status==200){
            //登录成功
            JSONObject data = new JSONObject(baseJson.getString("data"));
            int id = data.getInt("id");
            String userName = data.getString("username");
            String avatar = data.getString("avatar");
            String token = data.getString("token");
            user = new User(id,userName,avatar,token);
        }else if (status==400){
            //密码错误
        }else if (status==500){
            //未知错误
        }
        return user;
    }

    /**
     * 注册方法
     * @param value
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static User register(String [] value) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"register.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = new String[]{"username","password"};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        User user = null;
        if (status==200){
            //登录成功
            JSONObject data = new JSONObject(baseJson.getString("data"));
            int id = data.getInt("id");
            String userName = data.getString("username");
            String avatar = data.getString("avatar");
            String token = data.getString("token");
            user = new User(id,userName,avatar,token);
        }else if (status==400){
            //密码错误
        }else if (status==500){
            //未知错误
        }
        return user;
    }

    /**
     * 修改头像方法
     * @param user
     * @param url
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean modifyAvatar(User user,String url) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"modifyAvatar.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","avatar"};
        String [] value = {user.getToken(),url};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 修改密码方法
     * @param user
     * @param password
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean changePassword(User user,String password) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"changePassword.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","password"};
        String [] value = {user.getToken(),password};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 发布问题方法
     * @param user
     * @param title
     * @param content
     * @param imagesUrl
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean publishQuestion(User user,String title,String content,String [] imagesUrl) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"question.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String images = "";
        for (int i = 0; i < imagesUrl.length; i++) {
            images = images+imagesUrl[i];
            if (i!=imagesUrl.length-1){
                images = images+",";
            }
        }
        String [] key = {"token","title","content","images"};
        String [] value = {user.getToken(),title,content,images};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 发布回答
     * @param user
     * @param qid
     * @param content
     * @param imagesUrl
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean answer(User user, int qid, String content, String [] imagesUrl) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"question.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String images = "";
        for (int i = 0; i < imagesUrl.length; i++) {
            images = images+imagesUrl[i];
            if (i!=imagesUrl.length-1){
                images = images+",";
            }
        }
        String [] key = new String[]{"token","qid","content","images"};
        String [] value = new String[]{user.getToken(), Integer.toString(qid), content, images};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 取回答列表方法
     * @param user
     * @param page
     * @param count
     * @param qid
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */

    public static ArrayList<BihuAnswer> getAnswerList(User user,String page,String count, int qid) throws JSONException, TimeoutException, IOException {
        ArrayList<BihuAnswer> bihuAnswerArrayList = new ArrayList<>();
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"getAnswerList.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = new String[]{"page","count","qid","token"};
        String [] value = new String[]{page,count,Integer.toString(qid),user.getToken()};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        JSONObject baseJson = new JSONObject(recall.getJson());
        int status = baseJson.getInt("status");
        if (status==200){
            JSONObject dataJson = baseJson.getJSONObject("data");
            JSONArray answers = dataJson.getJSONArray("answers");
            for (int i = 0; i < answers.length(); i++) {
                JSONObject answerJson = new JSONObject(answers.get(i).toString());
                int id = answerJson.getInt("id");
                String content = answerJson.getString("content");
                String imageUrls = answerJson.getString("images");
                String time = answerJson.getString("date");
                String best = answerJson.getString("best");
                String exciting = answerJson.getString("exciting");
                String naive = answerJson.getString("naive");
                int authorId = answerJson.getInt("anthorId");
                String authorName = answerJson.getString("authorName");
                String authorAvatar = answerJson.getString("authorAvatar");
                String isExciting = answerJson.getString("is_exciting");
                String isNaive = answerJson.getString("is_naive");
                User author = new User(authorId,authorName,authorAvatar);
                bihuAnswerArrayList.add(new BihuAnswer(id,content,imageUrls,time,best,exciting,naive,isExciting,isNaive,author));
            }
        }else if (status==401){
            bihuAnswerArrayList = null;
            //用户认证错误
        }else{
            bihuAnswerArrayList = null;
            //未知错误
        }
        return bihuAnswerArrayList;
    }

    /**
     * 收藏方法
     * @param user
     * @param qid
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */

    public static boolean favorite(User user,int qid) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"favorite.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid"};
        String [] value = {user.getToken(),Integer.toString(qid)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 取消收藏方法
     * @param user
     * @param qid
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean cancelFavorite(User user,int qid) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"cancelFavorite.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid"};
        String [] value = {user.getToken(),Integer.toString(qid)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 取收藏列表方法
     * @param user
     * @param page
     * @param count
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static ArrayList<BihuQuestion> getFavoriteList(User user, int page, int count) throws JSONException, TimeoutException, IOException {
        ArrayList<BihuQuestion> bihuQuestionArrayList = new ArrayList<>();
        String [] key = {"token","page","count"};
        String [] value = {user.getToken(),Integer.toString(page),Integer.toString(count)};
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"getFavoriteList.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        JSONObject mainJson = new JSONObject(recall.getJson());
        JSONObject dataJson = new JSONObject(mainJson.getString("data"));
        JSONArray questionsJson = new JSONArray(dataJson.getString("questions"));
        for (int i = 0; i < questionsJson.length(); i++) {
            JSONObject questionJson = new JSONObject(questionsJson.get(i).toString());
            String id = questionJson.getString("id");
            String title = questionJson.getString("title");
            String content = questionJson.getString("content");
            String imageUrls = questionJson.getString("images");
            String time = questionJson.getString("date");
            String exciting = questionJson.getString("exciting");
            String naive = questionJson.getString("naive");
            String recent = questionJson.getString("recent");
            String answerCount = questionJson.getString("answerCount");
            String authorName = questionJson.getString("authorName");
            String authorAvatar = questionJson.getString("authorAvatar");
            String isExciting = questionJson.getString("is_exciting");
            String isNaive = questionJson.getString("is_naive");
            bihuQuestionArrayList.add(new BihuQuestion(id,title,content,time,exciting,naive,answerCount,imageUrls,recent,
                    authorName,authorAvatar,isExciting,isNaive,"true"));
        }
        return bihuQuestionArrayList;
    }

    /**
     * 采纳方法
     * @param user
     * @param qid
     * @param aid
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean accept(User user,int qid,int aid) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"accept.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid","aid"};
        String [] value = {user.getToken(),Integer.toString(qid),Integer.toString(aid)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * exciting方法
     * @param user
     * @param qid
     * @param type
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean exciting(User user,int qid,int type) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"exciting.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid","type"};
        String [] value = {user.getToken(),Integer.toString(qid),Integer.toString(type)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 取消exciting方法
     * @param user
     * @param qid
     * @param type
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean cancelExciting(User user,int qid,int type) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"cancelExciting.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid","type"};
        String [] value = {user.getToken(),Integer.toString(qid),Integer.toString(type)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * naive方法
     * @param user
     * @param qid
     * @param type
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean naive(User user,int qid,int type) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"naive.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid","type"};
        String [] value = {user.getToken(),Integer.toString(qid),Integer.toString(type)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }

    /**
     * 取消naive方法
     * @param user
     * @param qid
     * @param type
     * @return
     * @throws JSONException
     * @throws TimeoutException
     * @throws IOException
     */
    public static boolean cancelNaive(User user,int qid,int type) throws JSONException, TimeoutException, IOException {
        HttpsRequestHelper httpsRequestHelper = new HttpsRequestHelper(baseUrl+"cancelNaive.php");
        httpsRequestHelper.setMethod(HttpsRequestHelper.POST);
        String [] key = {"token","qid","type"};
        String [] value = {user.getToken(),Integer.toString(qid),Integer.toString(type)};
        httpsRequestHelper.setPostArguments(key,value);
        Recall recall = httpsRequestHelper.start();
        String mainJson = recall.getJson();
        JSONObject baseJson = new JSONObject(mainJson);
        int status = baseJson.getInt("status");
        if (status == 200){
            return true;
        }else if (status==401){
            //用户认证错误
        }else{
            //未知错误
        }
        return false;
    }
}
