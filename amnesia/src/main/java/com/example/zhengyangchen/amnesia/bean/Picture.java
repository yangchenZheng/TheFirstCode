package com.example.zhengyangchen.amnesia.bean;

import java.io.PipedReader;

/**
 * Created by zhengyangchen on 2015/11/7.
 */
public class Picture {

    /**
     * id主键
     */
    private int _id;
    /**
     * 相关联的memoId
     */
    private int memoId;



    /**
     * 图片的地址
     */
    private String picturePath;
    /**
     * 图片的使用状态
     */
    private int onState;


    public static final String TABLE_NAME = "picture";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MEMO_ID = "memoId";
    public static final String COLUMN_ON_STATE = "onState";
    public static final String COLUMN_PICTURE_PATH = "picturePath";
    public static final int IS_USED = 1;
    public static final int IS_NOT_USED = 0;

    /**
     * 默认构造
     */
    public Picture() {
    }

    /**
     * 有参数构造
     * @param memoId id
     * @param picturePath picturePath
     * @param onState  onState
     */
    public Picture(int memoId, String picturePath, int onState) {
        this.memoId = memoId;
        this.picturePath = picturePath;
        this.onState = onState;

    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMemoId() {
        return memoId;
    }

    public void setMemoId(int memoId) {
        this.memoId = memoId;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getOnState() {
        return onState;
    }

    public void setOnState(int onState) {
        this.onState = onState;
    }




}
