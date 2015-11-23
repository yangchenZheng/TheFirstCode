package com.example.zhengyangchen.amnesia.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zhengyangchen.amnesia.R;

/**
 *添加闹钟界面的activity
 */
public class AddAlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("添加提醒");
    }

    /**
     * 开启这个界面的方法
     * @param context 上下文
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddAlarmActivity.class);
        context.startActivity(intent);
    }
}
