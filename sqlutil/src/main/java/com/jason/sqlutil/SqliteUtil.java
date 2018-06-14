package com.jason.sqlutil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SqliteUtil {
    private Context context;
    private String DBname;
    private int version;
    DataBaseHelper helper;
    SQLiteDatabase sqLiteDatabase;

    public SqliteUtil(Context context, String DBname, int version) {
        this.context = context;
        this.DBname = DBname;
        this.version = version;
        initHelper();
    }


    public SqliteUtil(Context context, String DBname) {
        this(context, DBname, 1);
    }

    private void initHelper() {
        helper = new DataBaseHelper(context, DBname, new Link() {
            @Override
            public void linkDataBase(SQLiteDatabase sqLiteDatabase) {
                SqliteUtil.this.sqLiteDatabase = sqLiteDatabase;
            }
        }, version);
        sqLiteDatabase = helper.getWritableDatabase();
    }

    public void createTable(Class<?> table) {
        sqLiteDatabase.execSQL(TableFinder.createTable(table));

    }

    public void deleteTable(Class<?> table) {
        sqLiteDatabase.execSQL("drop table if exists " + TableFinder.getTableName(table));
    }

    public void insert(Object object) {
        sqLiteDatabase.execSQL(TableFinder.insert(object));
    }

    public void delete(Object object) {
        sqLiteDatabase.execSQL(TableFinder.delete(object));
    }

    public void clear(Class<?> table) {
        sqLiteDatabase.execSQL("delete from " + TableFinder.getTableName(table));

    }

    public void update(Object object) {
        sqLiteDatabase.execSQL(TableFinder.update(object));
    }

    public <T> List<T> selectAll(Class<T> table) {
        List<T> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TableFinder.getTableName(table), null);
        while (cursor.moveToNext()) {
            try {
                T object = table.newInstance();
                list.add(object);
                TableFinder.selectAll(object, cursor);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    public int count(Class<?> table) {
        Cursor cursor = sqLiteDatabase.rawQuery("select count(1) from " + TableFinder.getTableName(table), null);
        int countNum = 0;
        if (cursor.moveToNext()) {
            countNum = cursor.getInt(0);
        }
        cursor.close();
        return countNum;
    }

    //根绝条件查询
    public <T> List<T> selectByCondition(ConditionBuilder conditionBuilder, Class<T> table) {
        List<T> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TableFinder.getTableName(table) + conditionBuilder.toString(), null);
        while (cursor.moveToNext()) {
            try {
                T object = table.newInstance();
                list.add(object);
                TableFinder.selectAll(object, cursor);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    public void deleteByCondition(ConditionBuilder conditionBuilder, Class<?> table) {
        sqLiteDatabase.execSQL("delete from " + TableFinder.getTableName(table) + conditionBuilder.toString());
    }

    //根据条件获取数目
    public int countByCondition(ConditionBuilder conditionBuilder, Class<?> table) {
        Cursor cursor = sqLiteDatabase.rawQuery("select count(1) from " + TableFinder.getTableName(table) + conditionBuilder.toString(), null);
        int countNum = 0;
        if (cursor.moveToNext()) {
            countNum = cursor.getInt(0);
        }
        cursor.close();
        return countNum;
    }


}
