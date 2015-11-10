package com.example.zhengyangchen.amnesia.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.bean.Picture;

/**
 * 创建数据库
 * Created by zhengyangchen on 2015/10/15.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "amnesia";
    /**
     * 数据库版本号
     */
    public static final int VERSION = 1;
    /**
     * 创建memo的表的sql
     */
    public static final String CREATE_MEMO = "create table "
            + Memo.TABLE_NAME + "("
            + Memo.COLUMN_ID + " integer primary key autoincrement,"
            + Memo.COLUMN_MEMO_DESC + " text,"
            + Memo.COLUMN_DATE_STR + " real,"
            + Memo.COLUMN_VOICE_URL + " text,"
            + Memo.COLUMN_PHOTO_URL + " text,"
            + Memo.COLUMN_AV_URL + " text,"
            + Memo.COLUMN_ADDRESS + " text)";

    /**
     * 创建闹钟的表的sql
     */
    public static final String CREATE_ALARMS = "create table "
            + Alarms.TABLE_NAME + "("
            + Alarms.COLUMN_ID + " integer primary key autoincrement,"
            + Alarms.COLUMN_DATE_LONG + " real,"
            + Alarms.COLUMN_ALARMS_DESC + " text,"
            + Alarms.COLUMN_ALARMS_CYCLE_INDEX + " integer,"
            + Alarms.COLUMN_ALARMS_RUN_TIME + " real,"
            + Alarms.COLUMN_ALARMS_ADDRESS + " real,"
            + Alarms.COLUMN_IS_ALARMS_RUN + " integer)";

    public static final String CREATE_PICTURE = "create table "
            + Picture.TABLE_NAME + "("
            + Picture.COLUMN_ID + " integer primary key autoincrement,"
            + Picture.COLUMN_MEMO_ID + " integer,"
            + Picture.COLUMN_PICTURE_PATH + " text,"
            + Picture.COLUMN_ON_STATE + " integer)";



    public DBOpenHelper(Context context) {
        //初始化数据库
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //执行sql
        sqLiteDatabase.execSQL(CREATE_MEMO);
        sqLiteDatabase.execSQL(CREATE_ALARMS);
        sqLiteDatabase.execSQL(CREATE_PICTURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
