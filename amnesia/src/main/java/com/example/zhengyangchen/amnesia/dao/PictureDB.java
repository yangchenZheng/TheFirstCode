package com.example.zhengyangchen.amnesia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zhengyangchen.amnesia.bean.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作数据相关
 * Created by zhengyangchen on 2015/11/7.
 */
public class PictureDB {
    private static PictureDB mPictureDB;
    private SQLiteDatabase mDb;
    private Context mContext;

    private PictureDB(Context context) {
        this.mContext = context;
        mDb = (new DBOpenHelper(context)).getWritableDatabase();
    }

    public static PictureDB getInstace(Context context) {
        if (mPictureDB == null) {
            synchronized (PictureDB.class) {
                if (mPictureDB == null) {
                    mPictureDB = new PictureDB(context);
                }
            }
        }
        return mPictureDB;
    }

    /**
     * 保存picture实例到数据库
     *
     * @param picture 实例
     * @return 是否保存 true 成功 false 失败：实例为空
     */
    public boolean savePicture(Picture picture) {
        if (picture != null) {
            ContentValues values = new ContentValues();
            values.put(Picture.COLUMN_MEMO_ID, picture.getMemoId());
            values.put(Picture.COLUMN_PICTURE_PATH, picture.getPicturePath());
            values.put(Picture.COLUMN_ON_STATE, picture.getOnState());
            //加数据添加进数据库
            mDb.insert(Picture.TABLE_NAME, null, values);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将没有使用的picture从数据库中删除
     */
    public void deleteByPictureOnState() {
        String whereClause = Picture.COLUMN_ON_STATE + " = ?";
        String[] whereArgs = new String[]{String.valueOf(Picture.IS_NOT_USED)};
        mDb.delete(Picture.TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * 将没有使用的picture实例全部遍历出来
     * @return List<Picture>
     */
    public List<Picture> getIsNotUsedPicture() {
        List<Picture> pictureIsNotUsed = new ArrayList<>();
        Cursor cursor = mDb.query(Picture.TABLE_NAME, new String[]{Picture.COLUMN_ON_STATE}, Picture.COLUMN_ON_STATE + " = ?",
                new String[]{String.valueOf(Picture.IS_NOT_USED)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Picture picture = new Picture();
                picture.set_id(cursor.getInt(cursor.getColumnIndex(Picture.COLUMN_ID)));
                picture.setMemoId(cursor.getInt(cursor.getColumnIndex(Picture.COLUMN_MEMO_ID)));
                picture.setPicturePath(cursor.getString(cursor.getColumnIndex(Picture.COLUMN_PICTURE_PATH)));
                picture.setOnState(cursor.getInt(cursor.getColumnIndex(Picture.COLUMN_ON_STATE)));
                pictureIsNotUsed.add(picture);
            } while (cursor.moveToNext());
            cursor.close();
            return pictureIsNotUsed;
        }
        return pictureIsNotUsed;
    }

}
