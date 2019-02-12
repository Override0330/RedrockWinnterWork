package com.redrockwork.overrdie.firstdemo.bihu;

public class BihuAnswer {
    private int id;
    private String content,imagesUrl,date,best,exciting,naive,isExciting,isNaive;
    private User author;

    public BihuAnswer(int id, String content, String imagesUrl, String date, String best, String exciting, String naive, String isExciting, String isNaive, User author) {
        this.id = id;
        this.content = content;
        this.imagesUrl = imagesUrl;
        this.date = date;
        this.best = best;
        this.exciting = exciting;
        this.naive = naive;
        this.isExciting = isExciting;
        this.isNaive = isNaive;
        this.author = author;
    }
}
