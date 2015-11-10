/*
package com.example.zhengyangchen.amnesia.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.baidu.speech.VoiceRecognitionService;
import com.example.zhengyangchen.amnesia.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


*/
/**
 * Created by zhengyangchen on 2015/10/22.
 *//*

public class BaiduSpeechRecognizer implements RecognitionListener{

    private static final int REQUEST_UI = 1;
    private Context context;
    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    //状态表示符
    private int status = STATUS_None;
    private  SpeechRecognizer speechRecognizer;

    */
/**
     * 构造方法
     * @param context
     *//*

    public BaiduSpeechRecognizer(Context context){
        this.context = context;
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, new ComponentName(context, VoiceRecognitionService.class));
    }

    public String getResult() {
        Intent intent = new Intent();
        bindParams(intent);
        intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
        return null;
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public void bindParams(Intent intent) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
//        */
/*if (sp.contains(Constant.EXTRA_INFILE)) {
//            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
//            intent.putExtra(Constant.EXTRA_INFILE, tmp);
//        }*//*

//
//            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
//
////        if (sp.contains(Constant.EXTRA_SAMPLE)) {
////            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
////            if (null != tmp && !"".equals(tmp)) {
////                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
////            }
////        }
////        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
////            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
////            if (null != tmp && !"".equals(tmp)) {
////                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
////            }
////        }
////        if (sp.contains(Constant.EXTRA_NLU)) {
////            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
////            if (null != tmp && !"".equals(tmp)) {
////                intent.putExtra(Constant.EXTRA_NLU, tmp);
////            }
////        }
////
////        if (sp.contains(Constant.EXTRA_VAD)) {
////            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
////            if (null != tmp && !"".equals(tmp)) {
////                intent.putExtra(Constant.EXTRA_VAD, tmp);
////            }
////        }
////        String prop = null;
////        if (sp.contains(Constant.EXTRA_PROP)) {
////            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
////            if (null != tmp && !"".equals(tmp)) {
////                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
////                prop = tmp;
////            }
////        }
//
//        // offline asr
//        {
//            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
//            intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
////            if (null != prop) {
////                int propInt = Integer.parseInt(prop);
////                if (propInt == 10060) {
//                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
////                } else if (propInt == 20000) {
//                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
////                }
////            }
//            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
//        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }
}
*/
