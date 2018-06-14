package com.jason.sqlutil;

public class Condition {
    private String key;
    private String value;
    private String operator;
    private int type;

    public Condition(String key, String operator, String value, int type) {
        this.key = key;
        this.value = value;
        this.operator = operator;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return key + " " + operator + " '" + value + "'";
    }

}
