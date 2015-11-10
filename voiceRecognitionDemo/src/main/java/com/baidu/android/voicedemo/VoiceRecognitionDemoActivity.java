
package com.baidu.android.voicedemo;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * 开发用例，含有一次语音识别的全过程。
 */
public class VoiceRecognitionDemoActivity extends Activity implements OnClickListener {
    private static final String TAG = "demoActivity";

    /**
     * 结果展示
     */
    private EditText mResult = null;

    private BaiduASRDigitalDialog mDialog = null;

    private DialogRecognitionListener mRecognitionListener;

    private int mCurrentTheme = Config.DIALOG_THEME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        mResult = (EditText) findViewById(R.id.recognition_text);
        mRecognitionListener = new DialogRecognitionListener() {

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                    mResult.setText(rs.get(0));
                }

            }
        };
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_diolog:
                mResult.setText(null);
//                if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
                    mCurrentTheme = Config.DIALOG_THEME;
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    Bundle params = new Bundle();
                    params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
                    params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
                    params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
                    mDialog = new BaiduASRDigitalDialog(this, params);
                    mDialog.setDialogRecognitionListener(mRecognitionListener);
//                }
                mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
                mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                        Config.getCurrentLanguage());
                Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
                mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
                mDialog.show();
                break;
            case R.id.setting:
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);
                break;
            case R.id.more:
                setting = new Intent(this, DemoListActivity.class);
                startActivity(setting);
                break;
            default:
                break;
        }

    }
}
