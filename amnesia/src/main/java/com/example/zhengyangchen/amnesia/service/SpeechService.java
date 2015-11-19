package com.example.zhengyangchen.amnesia.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.bean.SemanticParsingObject;
import com.example.zhengyangchen.amnesia.dao.AlarmsDB;
import com.example.zhengyangchen.amnesia.dao.MemoDB;
import com.example.zhengyangchen.amnesia.util.BaiduSpeech;
import com.example.zhengyangchen.amnesia.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * 用于开始语音识别，并保存到数据库 ，发送添加闹钟的广播
 * Created by zhengyangchen on 2015/10/29.
 */
public class SpeechService extends Service {

    public static final String TAG = "zyc";
    private BaiduSpeech baiduSpeech;

//    public class MyBinder extends Binder {
//
//    }
    @Override
    public void onCreate() {
        //初始化百度speech
        if (baiduSpeech == null) {
            baiduSpeech = new BaiduSpeech(this);
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            //开始百度语音并保存数据到数据库
            startBaiduSpeech();
            return START_STICKY;
        }

        return START_STICKY;
    }

    /**
     * 开始百度语音并将返回的数据保存到数据库
     */
    private void startBaiduSpeech() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //通过api的方式启动百度语音
                baiduSpeech.startVoiceRecognitionByApi();
                //通过回调获得各种语音数据
                baiduSpeech.setVoiceClientStatusChangeListener(new BaiduSpeech.VoiceClientStatusChangeListener() {
                    @Override
                    public void onClientStatusChange(int status, Object obj) {
                        switch (status) {
                            case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                                // 检测到语音起点
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_AUDIO_DATA:
                                // 有音频数据输出
                                //if (obj != null && obj instanceof byte[]) {
                                    // 处理数据
                                //}
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                                // 已经检测到语音终点，等待网络返回
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                                Log.d(TAG, "语音识别结束");
                                // 语音识别完成
                                if (obj != null && obj instanceof List) {
                                    List results = (List) obj;
                                    if (results.size() > 0) {
                                        String temp_str = results.get(0).toString();
                                        SemanticParsingObject semanticParsingObject = BaiduSpeech.getSemanticParsingObject(temp_str);
                                        List<Object> objectList = BaiduSpeech.getDaoInstanceList(getApplicationContext(),
                                                semanticParsingObject);
                                        saveDataToDatabase(objectList);
//                                        //将json中的“json_res”数据提取出
//                                        JSONArray result_jsonArray = BaiduSpeech.getJsonArrayResult(temp_str);
//                                        if (result_jsonArray != null) {
//                                            try {
//                                                //提取出alarms对象
//                                                Alarms alarms = AlarmsDB.getAlarmsInstance(result_jsonArray);
//                                                if (alarms != null) {
//                                                    //将闹钟状态设置为响铃状态
//                                                    alarms.setIsAlarmsRun(Alarms.ALARMS_IS_RUNING);
//                                                    //将alarms对象保存进数据库
//                                                    AlarmsDB.getInstance(getBaseContext()).saveAlarmsByContentProvider(alarms);
//                                                }
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else {
////                                                //如果空就提示
////                                                Toast.makeText(getBaseContext(), "请确保您的语义中有提醒您的意思，" +
////                                                        "例如”叫我“，”提醒我“，等类似词", Toast.LENGTH_LONG).show();
//                                            //开启震动
//                                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                            vibrator.vibrate(500);
//
//                                        }

                                    }
                                }
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                                //多句模式会有部分结果（一个分句）返回
                                break;
                            case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                                //通知用户已取消
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void onError(int errorCode, int errorCode1) {

                    }
                });
            }
        }).start();
    }

    /**
     * 将数据存入数据库
     * @param objectList
     */
    private void saveDataToDatabase(List<Object> objectList) {
        if (!objectList.isEmpty()) {
            for (int i = 0; i < objectList.size(); i++) {
                Object object = objectList.get(i);
                if (object instanceof Alarms) {
                    Alarms alarms = (Alarms) object;
                    AlarmsDB.getInstance(getApplicationContext()).saveAlarmsByContentProvider(alarms);
                }
                if (object instanceof Memo) {
                    Memo memo = (Memo) object;
                    MemoDB.getInstance(getApplicationContext()).saveMemoByContentProider(memo);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
