package com.jason.sqlutil;

public class Order {
    private String key;
    private int orderType;

    public static int ASC = 0;
    public static int DESC = 1;

    public Order(String key, int orderType) {
        this.key = key;
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return key + " " + (orderType == ASC ? "asc" : "desc");
    }
}
