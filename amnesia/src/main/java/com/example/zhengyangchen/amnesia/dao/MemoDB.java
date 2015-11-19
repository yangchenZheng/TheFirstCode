package com.example.zhengyangchen.amnesia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.contentProvider.AmnesiaProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by zhengyangchen on 2015/10/25.
 */
public class MemoDB {
    private static MemoDB memoDB;

    public SQLiteDatabase db;

    private Context context;

    /**
     * 将构造私有
     * @param context
     */
    private MemoDB(Context context) {
        this.context = context;
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        db = dbOpenHelper.getWritableDatabase();
    }

    /**
     * 对外提供获取该类实例的方法
     */
    public  static MemoDB getInstance(Context context) {
        if (memoDB == null) {
            synchronized (MemoDB.class) {
                if (memoDB == null) {
                    memoDB = new MemoDB(context);
                }
            }
        }

        return memoDB;
    }

    /**
     * 将memo实体存到数据库
     */
    public void saveMemo(Memo memo) {
        if (memo != null) {
            ContentValues values = new ContentValues();
            values.put(Memo.COLUMN_MEMO_DESC, memo.getMemoDesc());
            values.put(Memo.COLUMN_DATE_STR, memo.getDateStr());
            values.put(Memo.COLUMN_VOICE_URL, memo.getVoiceUrl());
            values.put(Memo.COLUMN_PHOTO_URL, memo.getPhotoUrl());
            values.put(Memo.COLUMN_AV_URL, memo.getAvUrl());
            values.put(Memo.COLUMN_ADDRESS, memo.getAddress());
            long a = db.insert(Memo.TABLE_NAME, null, values);
            Log.d("zyc", "saveMemo run" + a);
        }
    }

    /**
     * 通过contentProvider进行数据的保存
     * @param memo
     */
    public  void saveMemoByContentProider(Memo memo) {
        if (memo != null) {
            ContentValues values = new ContentValues();
            values.put(Memo.COLUMN_MEMO_DESC, memo.getMemoDesc());
            values.put(Memo.COLUMN_DATE_STR, memo.getDateStr());
            values.put(Memo.COLUMN_VOICE_URL, memo.getVoiceUrl());
            values.put(Memo.COLUMN_PHOTO_URL, memo.getPhotoUrl());
            values.put(Memo.COLUMN_AV_URL, memo.getAvUrl());
            values.put(Memo.COLUMN_ADDRESS, memo.getAddress());
            context.getContentResolver().insert(AmnesiaProvider.URI_MEMO_ALL, values);
        }
    }

//    /**
//     * 通过contentProvider保存alarms实例
//     * @param alarms
//     */
//    public void saveAlarmsByContentProvider(Alarms alarms) {
//        if (alarms != null) {
//            ContentValues values = new ContentValues();
//            values.put(Alarms.COLUMN_ID,alarms.getId());
//        }
//    }

    public List<Memo> loadMemo() {
       List<Memo> memoList = new ArrayList<Memo>();
        Cursor cursor = db.query(Memo.TABLE_NAME,null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo(cursor.getInt(cursor.getColumnIndex(Memo.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Memo.COLUMN_MEMO_DESC)),
                        cursor.getLong(cursor.getColumnIndex(Memo.COLUMN_DATE_STR)),
                        cursor.getString(cursor.getColumnIndex(Memo.COLUMN_VOICE_URL)),
                        cursor.getString(cursor.getColumnIndex(Memo.COLUMN_PHOTO_URL)),
                        cursor.getString(cursor.getColumnIndex(Memo.COLUMN_AV_URL)),
                        cursor.getString(cursor.getColumnIndex(Memo.COLUMN_ADDRESS)));
                memoList.add(memo);
                Log.d("zyc", "LoadMemo run");
            } while (cursor.moveToNext());
            cursor.close();
            return memoList;
        }
        return memoList;

    }


    public static void deleteMemoById(int memoId,Context context) {
        if (!(memoId < 0)) {
            Uri uri = Uri.parse("content://" + AmnesiaProvider.AUTHORITY + "/memo/" + memoId);
            context.getContentResolver().delete(uri, null, null);
        }
    }


}
