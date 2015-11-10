package com.example.zhengyangchen.amnesia.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.util.ClearEditText;

import java.util.ArrayList;

/**
 * Created by zhengyangchen on 2015/10/23.
 */
public class AddMemoActivity extends Activity {

    private ClearEditText clearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_memo_activity);


        clearEditText = (ClearEditText) findViewById(R.id.username);

        clearEditText.setOnSpeakIconClickListener(new ClearEditText.onSpeakIconClickListener() {
            @Override
            public void onClick() {
                /*InputMethodManager inputMethodManager = (InputMethodManager) clearEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(clearEditText.getWindowToken(), 0);
                clearEditText.clearFocus();*/
                Toast.makeText(getApplicationContext(), "开始语音识别", Toast.LENGTH_SHORT).show();
                Bundle params = new Bundle();
                params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, "g39DHotSDo6SgMuf3n1uS2WE");
                params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, "e0d79ac15958302f5483c94b2d0b40ea");
                /*params.putInt(BaiduASRDigitalDialog.PARAM_PROP, VoiceRecognitionConfig.PROP_INPUT);
//设置语种类型：中文普通话，中文粤语，英文，可选。默认为中文普通话
                params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, VoiceRecognitionConfig.LANGUAGE_CHINESE);
//如果需要语义解析，设置下方参数。领域为输入不支持
                params.putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
// 设置对话框主题，可选。BaiduASRDigitalDialog 提供了蓝、暗、红、绿、橙四中颜色，每种颜色又分亮、暗两种色调。共 8 种主题，开发者可以按需选择，取值参考 BaiduASRDigitalDialog 中前缀为 THEME_的常量。默认为亮蓝色
                params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_RED_DEEPBG);*/
                BaiduASRDigitalDialog baiduASRDigitalDialog = new BaiduASRDigitalDialog(AddMemoActivity.this, params);
                baiduASRDigitalDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> rs = results != null ? results
                                .getStringArrayList(RESULTS_RECOGNITION) : null;
                        if (rs != null && rs.size() > 0) {
                            //将识别的结果给clearEditText
                            clearEditText.setText(rs.get(0));
                            //edittext光标的位置
                            clearEditText.setSelection(rs.get(0).length());
                        }
                    }
                });
                baiduASRDigitalDialog.show();
            }
        });
    }

    /**
     * 用于启动这个activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddMemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
