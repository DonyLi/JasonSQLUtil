package com.jason.sqlutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Link link;


    public DataBaseHelper(Context context, String name, Link link, int version) {
        super(context, name, null, version);
        this.link = link;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        link.linkDataBase(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        link.linkDataBase(sqLiteDatabase);

    }
}
