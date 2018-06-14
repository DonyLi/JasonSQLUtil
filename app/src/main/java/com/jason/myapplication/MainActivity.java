package com.jason.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jason.sqlutil.ConditionBuilder;
import com.jason.sqlutil.Order;
import com.jason.sqlutil.Page;
import com.jason.sqlutil.SqliteUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SqliteUtil util = new SqliteUtil(this, "user_info");
        util.createTable(UserInfoDB.class);
//        for (int i = 0; i < 10; i++) {
//            UserInfoDB infoDB = new UserInfoDB();
//            infoDB.floatNum = (float) (0.2 * i);
//            infoDB.number = 0.5 * i;
//            infoDB.numInt = 2 * i;
//            infoDB.time = System.currentTimeMillis();
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            infoDB.name = "name" + i;
//            util.insert(infoDB);
//        }

        ConditionBuilder conditionBuilder = new ConditionBuilder();
        conditionBuilder.addAndCondition("number", ">", 1).addOrder("id", Order.DESC)
                .setPage(1, 5);
        Log.e("TAG1", util.countByCondition(conditionBuilder, UserInfoDB.class) + "");
        List<UserInfoDB> list = util.selectByCondition(conditionBuilder, UserInfoDB.class);

        for (int i = 0; i < list.size(); i++) {
            Log.e("TAG" + i, list.get(i).toString());

        }
    }
}
