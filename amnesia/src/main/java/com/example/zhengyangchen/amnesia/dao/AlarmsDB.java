package com.example.zhengyangchen.amnesia.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.contentProvider.AmnesiaProvider;
import com.example.zhengyangchen.amnesia.fragment.AlarmsFragment;
import com.example.zhengyangchen.amnesia.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 处理业务中的数据库操作
 *
 * Created by zhengyangchen on 2015/10/28.
 */
public class AlarmsDB {
    private Context mContext;
    public SQLiteDatabase mDb;
    private static AlarmsDB mAlarmsDB;

    private AlarmsDB(Context context) {
        mContext = context;
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        mDb = dbOpenHelper.getWritableDatabase();
    }

    public static  AlarmsDB getInstance(Context context) {
        if (mAlarmsDB == null) {
            synchronized (AlarmsDB.class) {
                if (mAlarmsDB == null) {
                    mAlarmsDB = new AlarmsDB(context);
                }
            }
        }

        return mAlarmsDB;
    }

    /**
     * 通过contentProvider保存alarms实例
     * @param alarms 闹钟实例
     */
    public void saveAlarmsByContentProvider(Alarms alarms) {
        if (alarms != null) {
            ContentValues values = new ContentValues();
            values.put(Alarms.COLUMN_ALARMS_DESC, alarms.getAlarmsDesc());
            values.put(Alarms.COLUMN_DATE_LONG, (new Date()).getTime());
            values.put(Alarms.COLUMN_ALARMS_RUN_TIME, alarms.getAlarmsRunTime());
            values.put(Alarms.COLUMN_ALARMS_ADDRESS, alarms.getAlarmsAddress());
            values.put(Alarms.COLUMN_IS_ALARMS_RUN, alarms.getIsAlarmsRun());
            values.put(Alarms.COLUMN_ALARMS_CYCLE_INDEX, alarms.getAlarmsCycleIndex());
            mContext.getContentResolver().insert(AmnesiaProvider.URI_ALARMS_ALL, values);
        }

    }

    /**
     * 通过id删除该条数据
     *
     * @param alarmsId 闹钟数据库id
     * @param context 上下文
     */
    public static void deleteAlarmsById(int alarmsId,Context context) {
        if (!(alarmsId < 0)) {
            Uri uri = Uri.parse("content://" + AmnesiaProvider.AUTHORITY + "/alarms/" + alarmsId);
            context.getContentResolver().delete(uri, null, null);
        }
    }

    public static void updateAlarmStatus(int alarmsId,int alarmStatus,Context context) {
        if (!(alarmsId < 0)) {
            Uri uri = Uri.parse("content://" + AmnesiaProvider.AUTHORITY + "/alarms/" + alarmsId);
            ContentValues values = new ContentValues();
            values.put(Alarms.COLUMN_IS_ALARMS_RUN, alarmStatus);
            context.getContentResolver().update(uri, values, null, null);
        }

    }

    /**
     * 由百度语音的语义解析结果jsonArray转化为alarms的实例，并传递出来
     */
    public static Alarms getAlarmsInstance(JSONArray result) throws JSONException {
        Alarms alarms = new Alarms();
        if (result != null ) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);
                //
                if (jsonObject.getString("domain").equals("alarm")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("object");
                    alarms.setAlarmsDesc(jsonObject1.getString("event"));
                    //拼接日期时间
                    String dateAndTime = jsonObject1.getString("date") + " " + jsonObject1.getString("time");
                    long date = DateUtil.parseDate(dateAndTime).getTime();
                    alarms.setAlarmsRunTime(date);
                    return alarms;
                }
            }
        }
        Log.d(AlarmsFragment.TAG, "getAlarmInstance run");
        return alarms;
    }
}
