package com.example.shareplan;

public class TodoInfo {
    String type;
    String title;
    String date;
    int registerNum;

    public TodoInfo(String type, String title, String date, int registerNum) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.registerNum = registerNum;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getRegisterNum() {
        return registerNum;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRegisterNum(int registerNum) {
        this.registerNum = registerNum;
    }

}
