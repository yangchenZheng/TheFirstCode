package com.example.zhengyangchen.amnesia.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.example.zhengyangchen.amnesia.service.SpeechService;
import com.example.zhengyangchen.amnesia.service.registerScreenActionReceiverService;


/**
 *解锁事件的广播接收器，用于启动屏幕亮起广播接收器的注册服务，和停止百度语音识别过程
 * Created by zhengyangchen on 2015/10/31.
 */
public class UserPresentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //启动注册服务
        registerScreenActionReceiverService.onStartService(context.getApplicationContext());
        //停止百度语音识别
        VoiceRecognitionClient.getInstance(context).stopVoiceRecognition();
        Log.d(SpeechService.TAG, "解锁..启动注册服务");
    }

}
