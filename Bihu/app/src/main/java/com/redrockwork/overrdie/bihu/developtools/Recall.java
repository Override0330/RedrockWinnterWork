package com.redrockwork.overrdie.bihu.developtools;


public class Recall {
    private String json;
    private int responseCode;

    public Recall(String json, int responseCode){
        this.json = json;
        this.responseCode = responseCode;
    }

    public String getJson() {
        return json;
    }


    @Override
    public String toString() {
        return "ResponseCode:" + responseCode + "\n" + "JSON:" + json;
    }
}
