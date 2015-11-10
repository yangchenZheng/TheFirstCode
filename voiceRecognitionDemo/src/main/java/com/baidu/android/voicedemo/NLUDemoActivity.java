
package com.baidu.android.voicedemo;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionClient.VoiceClientStatusChangeListener;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NLUDemoActivity extends FragmentActivity {
    private static final String TAG = "NLUDemo";

    private static final int RECOGNITION_DIALOG = 1;

    private DialogRecognitionListener mRecognitionListener;

    private ControlPanelFragment mControlPanel;

    private VoiceRecognitionClient mASREngine;

    /** 正在识别中 */
    private boolean isRecognition = false;

    /** 音量更新间隔 */
    private static final int POWER_UPDATE_INTERVAL = 100;

    /** 识别回调接口 */
    private MyVoiceRecogListener mListener = new MyVoiceRecogListener();

    /** 主线程Handler */
    private Handler mHandler;

    private CommandsAdapter mCommandsAdapter;

    private ListFragment mCommandsFragment;
    
    BaiduASRDigitalDialog mDialog;

    /**
     * 音量更新任务
     */
    private Runnable mUpdateVolume = new Runnable() {
        public void run() {
            if (isRecognition) {
                long vol = mASREngine.getCurrentDBLevelMeter();
                mControlPanel.volumeChange((int) vol);
                mHandler.removeCallbacks(mUpdateVolume);
                mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nlu_demo_activity);
        //获得识别BDVRClient单例对象
        mASREngine = VoiceRecognitionClient.getInstance(this);
        //设置key
        mASREngine.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
        mHandler = new Handler();
        mControlPanel = (ControlPanelFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.control_panel));
        //设置他的监听事件 开始执行相关操作
        mControlPanel.setOnEventListener(new ControlPanelFragment.OnEventListener() {

            @Override
            public boolean onStopListening() {
                mASREngine.speakFinish();
                return true;
            }

            @Override
            public boolean onStartListening() {
                VoiceRecognitionConfig config = new VoiceRecognitionConfig();
                int prop = Config.CURRENT_PROP;
                // 输入法暂不支持语义解析
                if (prop == VoiceRecognitionConfig.PROP_INPUT) {
                    prop = VoiceRecognitionConfig.PROP_SEARCH;
                }
                config.setProp(prop);
                config.setLanguage(Config.getCurrentLanguage());
                //开启语义解析
                config.enableNLU();
                config.enableVoicePower(Config.SHOW_VOL); // 音量反馈。
                if (Config.PLAY_START_SOUND) {
                    config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
                }
                if (Config.PLAY_END_SOUND) {
                    config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
                }
                config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率
                // 下面发起识别
                int code = mASREngine.startVoiceRecognition(mListener, config);
                if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
                    Toast.makeText(NLUDemoActivity.this, getString(R.string.error_start, code),
                            Toast.LENGTH_LONG).show();
                }

                return code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
            }

            @Override
            public boolean onCancel() {
                mASREngine.stopVoiceRecognition();
                return true;
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_diolog:
                Bundle params = new Bundle();
                params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
                mDialog = new BaiduASRDigitalDialog(this, params);
                mRecognitionListener = new DialogRecognitionListener() {

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> rs = results != null ? results
                                .getStringArrayList(RESULTS_RECOGNITION) : null;
                        if (rs != null && rs.size() > 0) {
                            showResourceViewer(rs.get(0));
                        }

                    }
                };
                mDialog.setDialogRecognitionListener(mRecognitionListener);
                int prop = Config.CURRENT_PROP;
                // 输入法暂不支持语义解析
                if (prop == VoiceRecognitionConfig.PROP_INPUT) {
                    prop = VoiceRecognitionConfig.PROP_SEARCH;
                }
                mDialog.getParams().put(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
                mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
                        Constants.SECRET_KEY);
                mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, prop);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
                mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                        Config.getCurrentLanguage());
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
                mDialog.show();
                break;
            default:
                break;
        }

    }

    /**
     * 将语义结果中的资源单独展示
     * 
     * @param result
     */
    private void showResourceViewer(String result) {
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
        }
        showListFragment(results);
    }

    private void showListFragment(JSONArray data) {
        if (mCommandsAdapter == null) {
            mCommandsAdapter = new CommandsAdapter(this);
            mCommandsFragment = new ListFragment();
            mCommandsFragment.setListAdapter(mCommandsAdapter);
        } else {
            mCommandsAdapter.clear();
        }
        mCommandsAdapter.setData(data);
        mCommandsAdapter.notifyDataSetChanged();
        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mCommandsFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        VoiceRecognitionClient.releaseInstance(); // 释放识别库
    }

    /**
     * 重写用于处理语音识别回调的监听器
     */
    class MyVoiceRecogListener implements VoiceClientStatusChangeListener {

        @Override
        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
            // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    isRecognition = true;
                    mHandler.removeCallbacks(mUpdateVolume);
                    mHandler.postDelayed(mUpdateVolume, POWER_UPDATE_INTERVAL);
                    mControlPanel.statusChange(ControlPanelFragment.STATUS_RECORDING_START);
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START: // 检测到语音起点
                    mControlPanel.statusChange(ControlPanelFragment.STATUS_SPEECH_START);
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    mControlPanel.statusChange(ControlPanelFragment.STATUS_SPEECH_END);
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    mControlPanel.statusChange(ControlPanelFragment.STATUS_FINISH);
                    isRecognition = false;
                    if (obj != null && obj instanceof List) {
                        List results = (List) obj;
                        if (results.size() > 0) {
                            String temp_str = results.get(0).toString();
                            showResourceViewer(temp_str);
                        }
                    }
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    mControlPanel.statusChange(ControlPanelFragment.STATUS_FINISH);
                    isRecognition = false;
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onError(int errorType, int errorCode) {
            isRecognition = false;
            mControlPanel.statusChange(ControlPanelFragment.STATUS_FINISH);
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }
    }
}
