package com.example.zhengyangchen.amnesia.bean;

/**
 * Created by zhengyangchen on 2015/10/24.
 */
public class Memo {
    /**
     * 主键
     */
    private int _id;
    /**
     * 备忘描述
     */
    private String memoDesc;
    /**
     * 备忘生成时间形
     */
    private long dateStr;
    /**
     * 备忘生成的语音URL
     */
    private String voiceUrl;
    /**
     * 备忘生成的照片的URL
     */
    private String photoUrl;
    /**
     * 备忘生成的录像url
     */
    private String avUrl;
    /**
     * 备忘生成的地址
     */
    private String address;

    public static final String TABLE_NAME = "tb_memo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MEMO_DESC = "memoDesc";
    public static final String COLUMN_DATE_STR = "dateStr";
    public static final String COLUMN_PHOTO_URL = "photoUrl";
    public static final String COLUMN_VOICE_URL = "voiceUrl";
    public static final String COLUMN_AV_URL = "avUrl";
    public static final String COLUMN_ADDRESS = "address";

    public Memo() {
    }

    public Memo(int _id, String memoDesc, long dateStr, String voiceUrl, String photoUrl, String avUrl, String address) {
        this._id = _id;
        this.memoDesc = memoDesc;
        this.dateStr = dateStr;
        this.voiceUrl = voiceUrl;
        this.photoUrl = photoUrl;
        this.avUrl = avUrl;
        this.address = address;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getMemoDesc() {
        return memoDesc;
    }

    public void setMemoDesc(String memoDesc) {
        this.memoDesc = memoDesc;
    }

    public long getDateStr() {
        return dateStr;
    }

    public void setDateStr(long dateStr) {
        this.dateStr = dateStr;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAvUrl() {
        return avUrl;
    }

    public void setAvUrl(String avUrl) {
        this.avUrl = avUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
