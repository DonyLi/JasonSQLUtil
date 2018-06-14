package com.jason.sqlutil;

public class Page {
    private int page;

    private int limitNum;

    public Page(int page, int limitNum) {
        this.page = page;
        this.limitNum = limitNum;
    }

    @Override
    public String toString() {
        return " limit " + ((page - 1) * limitNum) + "," + limitNum;
    }
}
