package com.example.zhengyangchen.amnesia.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.example.zhengyangchen.amnesia.service.SpeechService;
import com.example.zhengyangchen.amnesia.service.registerScreenActionReceiverService;


/**
 *
 * Created by zhengyangchen on 2015/10/31.
 */
public class UserPresentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        registerScreenActionReceiverService.onStartService(context.getApplicationContext());
        VoiceRecognitionClient.getInstance(context).stopVoiceRecognition();
        Log.d(SpeechService.TAG, "解锁..启动注册服务");
    }

}
