package com.jason.myapplication;

import com.jason.sqlutil.Column;
import com.jason.sqlutil.Id;
import com.jason.sqlutil.Table;

import java.lang.reflect.Field;

@Table
public class UserInfoDB {
    @Id
    public int id;
    @Column
    public String name;
    @Column
    public long time;
    @Column
    public double number;
    @Column
    public float floatNum;
    @Column
    public int numInt;

    @Override
    public String toString() {
        Field[] fields = getClass().getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Object o = field.get(this);
                builder.append(field.getName()).append("=").append(String.valueOf(o)).append(",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
