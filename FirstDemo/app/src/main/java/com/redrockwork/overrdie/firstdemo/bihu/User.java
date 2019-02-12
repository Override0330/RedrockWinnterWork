package com.redrockwork.overrdie.firstdemo.bihu;

public class User {
    private int id;
    private String username;
    private String avatar;
    private String token;

    public User(int id, String username, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    public User(int id, String username, String avatar, String token) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getToken() {
        return token;
    }
}
