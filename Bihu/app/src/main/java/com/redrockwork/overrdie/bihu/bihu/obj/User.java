package com.redrockwork.overrdie.bihu.bihu.obj;

public class User {
    private String id;
    private String username;
    private String avatar;
    private String token;

    public User(String id, String username, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    public User(String id, String username, String avatar, String token) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.token = token;
    }

    public String getId() {
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
