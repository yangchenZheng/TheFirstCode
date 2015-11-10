package com.example.zhengyangchen.amnesia.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zhengyangchen.amnesia.Broadcast.LockScreenReceiver;


/**
 * 用于注册屏幕亮起的广播
 * Created by zhengyangchen on 2015/10/30.
 */
public class registerScreenActionReceiverService extends Service {
    LockScreenReceiver mLockScreenReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("zyc", "SpeechIntentService onCreate");
        mLockScreenReceiver = new LockScreenReceiver();
        mLockScreenReceiver.registerScreenActionReceiver(getBaseContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLockScreenReceiver.unRegisterScreenActionReceiver(getApplicationContext());
        Log.d("zyc", "unRegisterScreenActionReceiver() is run");

    }

    public static void onStartService(Context context) {
        Intent intent = new Intent(context, registerScreenActionReceiverService.class);
        context.startService(intent);
    }
}
