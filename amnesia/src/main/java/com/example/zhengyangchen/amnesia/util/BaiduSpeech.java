package com.example.zhengyangchen.amnesia.util;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.bean.SemanticParsingObject;
import com.example.zhengyangchen.amnesia.dao.AlarmsDB;
import com.example.zhengyangchen.amnesia.dao.MemoDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 对百度语音的api进行了简单的封装
 * Created by zhengyangchen on 2015/10/26.
 */
public class BaiduSpeech {

    /**
     * 百度apiKey
     */
    public static final String API_KEY = "g39DHotSDo6SgMuf3n1uS2WE";
    /**
     * 百度secretKey
     */
    public static final String SECRET_KEY = "e0d79ac15958302f5483c94b2d0b40ea";
    private static final String TAG = "zyc";
    /**
     * 识别回调接口
     */
    private MyVoiceRecogListener mMyVoiceRecogListener = new MyVoiceRecogListener();
    private Context mContext;
    //百度语音
    private VoiceRecognitionClient mASREngine;
    //用于回调的接口
    private VoiceClientStatusChangeListener mListener;
    private BaiduASRDigitalDialog mDialog;

    public BaiduSpeech(Context context) {
        this.mContext = context;
    }


    public BaiduASRDigitalDialog getBaiduASRDialogInstance() {
        Bundle params = new Bundle();
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
        mDialog = new BaiduASRDigitalDialog(mContext, params);
        mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, BaiduASRDigitalDialog.SPEECH_MODE_SEARCH);
        //设定key
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_API_KEY, BaiduSpeech.API_KEY);
        //设定secretKey
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, BaiduSpeech.SECRET_KEY);
        //开启语义解析
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
        //开启语音识别开始提示
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, true);
        //开启语音识别结束提示音
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_CONTACTS_ENABLE, true);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, true);
        return mDialog;
    }

    /**
     * 开始语音识别
     */
    public void startVoiceRecognitionByApi() {
        //获取单利实例
        mASREngine = VoiceRecognitionClient.getInstance(mContext);
        //设置apiKey
        mASREngine.setTokenApis(API_KEY, SECRET_KEY);
        VoiceRecognitionConfig config = new VoiceRecognitionConfig();
        //设置识别开始提示音
        config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start);
        //设置识别结束提示音
        config.enableEndSoundEffect(R.raw.bdspeech_speech_end);
        //设置识别语言
        config.setLanguage(VoiceRecognitionConfig.LANGUAGE_CHINESE);
        //设置采样率
        config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K);
        //设置垂直领域，默认为输入，输入下不支持语义解析所以换为搜索
        config.setProp(VoiceRecognitionConfig.PROP_SEARCH);
        //开启通讯录支持，识别人名更加高效
        config.enableContacts();
        //开启语义解析
        config.enableNLU();
        //关闭语音能量监听节省资源
        config.enableVoicePower(false);
        //启动百度语音
        int code = mASREngine.startVoiceRecognition(mMyVoiceRecogListener, config);
        if (code == VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
            //启动成功
            Log.d(TAG, "VoiceRecognitionClient run");
        } else {
            //启动失败
            Log.d(TAG, "VoiceRecognitionClient error");
        }

    }

    class MyVoiceRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {
        @Override
        public void onClientStatusChange(int status, Object obj) {
            mListener.onClientStatusChange(status, obj);
            switch (status) {
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                    // 检测到语音起点
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_AUDIO_DATA:
                    // 有音频数据输出
//                    if (obj != null && obj instanceof byte[]) {
//                        // 处理数据
//                    }
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    // 已经检测到语音终点，等待网络返回
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    // 语音识别完成
                    /*if(currentVoiceType == VOICE_TYPE_SEARCH){
                        // obj是一个ArrayList<String>，里面有多个候选词
                    }else if(currentVoiceType== VOICE_TYPE_INPUT){
                        // obj是一个List<List<Candidate>>，里面有多个候选词
                    }*/
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    //多句模式会有部分结果（一个分句）返回
                   /* if(currentVoiceType == VOICE_TYPE_SEARCH){
                        //obj是一个ArrayList<String>，里面有多个候选词
                    } else if(currentVoiceType == VOICE_TYPE_INPUT){
                        //obj是一个List<List<Candidate>>，里面有多个候选词*/
                    //List<List<Candidate>> result = (List<List<Candidate>>) obj;
                    //}
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    //通知用户已取消
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int errorType, int errorCode) {
            //errorType为错误类型，errorCode为错误码。此回调先于onClientStatusChange的CLIENT_STATUS_ERROR
            mListener.onError(errorCode, errorCode);
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }
    }

    /**
     * 将语义结果中的资源单独展示
     *
     * @param result
     */
    public static JSONArray getJsonArrayResult(String result) {
        JSONArray results = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject temp_json = new JSONObject(result);
                String temp_str = temp_json.optString("json_res");
                if (!TextUtils.isEmpty(temp_str)) {
                    temp_json = new JSONObject(temp_str);
                    if (temp_json != null) {
                        // 获取语义结果
                        results = temp_json.optJSONArray("results");
                        JSONArray commands = temp_json.optJSONArray("commandlist");
                        // 如果语义结果为空获取资源结果
                        if (results == null || results.length() == 0) {
                            results = commands;
                        } else if (commands != null && commands.length() > 0) {
                            for (int i = 0; i < commands.length(); i++) {
                                results.put(commands.opt(i));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.w(TAG, e);
            }
        } else {

        }

        return results;
    }


    /**
     * 将百度语音语义解析结果解释为一个对象，这个对象中包含了所有关键信息
     *
     * @param result 传入的原始结果
     * @return 解析实例后的对象
     */
    public static SemanticParsingObject getSemanticParsingObject(String result) {
        SemanticParsingObject semanticParsingObject = new SemanticParsingObject();
        //@SemanticParsingObject
        semanticParsingObject.setOriginalResultJson(result);
        JSONArray jsonArrayResults;
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject temp_json = new JSONObject(result);

                String temp_str = temp_json.optString("json_res");
                if (!TextUtils.isEmpty(temp_str)) {
                    temp_json = new JSONObject(temp_str);
                    //用户原始输入文本
                    String rawText = temp_json.optString("raw_text");
                    //@@
                    semanticParsingObject.setRawText(rawText);
                    //原始输入文本 被分词 矫正 的文本
                    String parsedText = temp_json.optString("parsed_text");
                    //@@
                    semanticParsingObject.setParsedText(parsedText);
                    //意图表示素组的json文本
                    String results = temp_json.optString("results");
                    //@@
                    semanticParsingObject.setResults(results);
                    List<SemanticParsingObject.ResultObject> resultObjectList = new ArrayList<>();
                    if (!TextUtils.isEmpty(results)) {
                        jsonArrayResults = temp_json.optJSONArray("results");

                        for (int i = 0; i < jsonArrayResults.length(); i++) {
                            JSONObject jsonObject = jsonArrayResults.getJSONObject(0);

                            String domain = jsonObject.getString("domain");
                            String intent = jsonObject.getString("intent");
                            double score = jsonObject.getDouble("score");
                            JSONObject object = jsonObject.getJSONObject("object");

                            SemanticParsingObject.ResultObject resultObject = semanticParsingObject.getResultObjectInstance();
                            //@@@
                            resultObject.setDomain(domain);
                            //@@@
                            resultObject.setIntent(intent);
                            //@@@
                            resultObject.setScore(score);
                            //@@@
                            resultObject.setObject(object);
                            resultObjectList.add(resultObject);
                        }

                        semanticParsingObject.setObjects(resultObjectList);
                    }
                }
            } catch (JSONException e) {
                Log.w(TAG, e);
            }
        }
        return semanticParsingObject;
    }

    /**
     * 将语义解析对象进行处理
     * @param context 上下文
     * @param semanticParsingObject 语义解析对象
     * @return 处理结果 list
     */
    public static List<Object> getDaoInstanceList(Context context, SemanticParsingObject semanticParsingObject) {

        List<Object> objectList = new ArrayList<>();
        try {
            //判断语义解析对象是否为空
            if (semanticParsingObject != null) {
                //不为空
                //判断原始数据结果是否为空
                if (semanticParsingObject.getOriginalResultJson() != null) {
                    //不为空
                    // 判断语义解析对象中的意图表示数组的json文本是否为空
                    if (semanticParsingObject.getResults() != null) {
                        //不为空
                        //判断存储意图数组的list的长度
                        SemanticParsingObject.ResultObject resultObject = null;
                        if (semanticParsingObject.getObjects().size() > 0) {
                            for (int i = 0; i < semanticParsingObject.getObjects().size(); i++) {
                                resultObject = semanticParsingObject.getObjects().get(i);
                                //获取领域取值
                                String domain = resultObject.getDomain();
                                switch (domain) {
                                    //领域取值为 提醒 将数据以alarm
                                    case "alarm":
                                        JSONObject jsonObject = resultObject.getObject();
                                        Alarms alarms = new Alarms();
                                        alarms.setAlarmsDesc(jsonObject.getString("event"));
                                        //拼接日期时间
                                        String dateAndTime = jsonObject.getString("date") + " " + jsonObject.getString("time");
                                        long date = DateUtil.parseDate(dateAndTime).getTime();
                                        alarms.setAlarmsRunTime(date);
                                        objectList.add(alarms);
                                        break;
                                    default:
                                        createMemoAndAddToList(semanticParsingObject,objectList);
                                }
                            }
                        } else {
                            createMemoAndAddToList(semanticParsingObject, objectList);
                        }
                    } else {//语义解析对象中的意图表示数组的json文本为空

                        createMemoAndAddToList(semanticParsingObject, objectList);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return objectList;
    }

    private static void createMemoAndAddToList(SemanticParsingObject semanticParsingObject, List<Object> objectList) {
        String rawText = semanticParsingObject.getRawText();
        //数据产生的时间
        long dateStr = new Date().getTime();
        Memo memo = new Memo();
        memo.setMemoDesc(rawText);
        memo.setDateStr(dateStr);
        objectList.add(memo);
    }

    /**
     * 创建接口
     */
    public interface VoiceClientStatusChangeListener {
        void onClientStatusChange(int status, Object obj);

        void onError(int errorCode, int errorCode1);

    }

    /**
     * 接口对完提供
     */
    public VoiceClientStatusChangeListener setVoiceClientStatusChangeListener(VoiceClientStatusChangeListener listener) {
        this.mListener = listener;
        return mListener;
    }

    public void stopBaiduSpeech() {
        mASREngine.stopVoiceRecognition();
    }
}
