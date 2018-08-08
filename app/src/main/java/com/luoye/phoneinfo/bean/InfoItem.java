package com.luoye.phoneinfo.bean;

/**
 * Created by zyw on 18-8-8.
 */

public class InfoItem {


    public InfoItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String title;
    private  String content;
}
