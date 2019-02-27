package com.redrockwork.overrdie.firstdemo.developtools;

import java.io.UnsupportedEncodingException;

public class Recall {
    private String json;
    private int responseCode;

    public Recall(String json,int responseCode) throws UnsupportedEncodingException {
        this.json = json;
        this.responseCode = responseCode;
    }

    public String getJson() {
        return json;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return "ResponseCode:"+responseCode+"\n"+"JSON:"+json;
    }
}
