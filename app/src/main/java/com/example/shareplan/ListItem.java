package com.example.shareplan;

public class ListItem {

    private String name;
    private String info;

    public void ListItem(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getInfo() { return this.info; }

    public void setInfo(String info) { this.info = info; }
}
