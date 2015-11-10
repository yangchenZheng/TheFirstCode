package com.example.zhengyangchen.amnesia.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.dao.AlarmsDB;
import com.example.zhengyangchen.amnesia.service.SpeechService;
import com.example.zhengyangchen.amnesia.util.BaiduSpeech;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by zhengyangchen on 2015/10/15.
 */
public class LockScreenReceiver extends BroadcastReceiver {
    private String TAG = "ScreenActionReceiver";
    private boolean isRegisterReceiver = false;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.d(TAG, "屏幕亮起广播...");
            Intent intent1 = new Intent(context, SpeechService.class);
            intent1.putExtra("zyc", true);
            context.startService(intent1);
        }
    }


    public void registerScreenActionReceiver(Context mContext) {
        if (!isRegisterReceiver) {
            isRegisterReceiver = true;

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            Log.d("zyc", "注册屏幕解锁、加锁广播接收者...");
            mContext.registerReceiver(LockScreenReceiver.this, filter);
        }
    }

    public void unRegisterScreenActionReceiver(Context mContext) {
        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            Log.d("zyc", "注销屏幕解锁、加锁广播接收者...");
            mContext.unregisterReceiver(LockScreenReceiver.this);
        }
    }
}
