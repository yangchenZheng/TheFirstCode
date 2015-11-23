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
import com.example.zhengyangchen.amnesia.util.Util;

import java.util.ArrayList;

/**
 * 添加备忘信息的界面
 * Created by zhengyangchen on 2015/10/23.
 */
public class AddMemoActivity extends Activity {

    private ClearEditText clearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_memo_activity);

        //有清除，语音按钮的editText
        clearEditText = (ClearEditText) findViewById(R.id.username);
        //添加点击语音按钮的监听器
        clearEditText.setOnSpeakIconClickListener(new ClearEditText.onSpeakIconClickListener() {
            @Override
            public void onClick() {
                //弹出toast
                Util.showShortToast(getBaseContext(), "开始语音识别");
                //以对话框的形式开始百度语音
                baiduASRDialogShow();
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

    /**
     * 以对话框的形式开始百度语音
     */
    private void baiduASRDialogShow() {
        //配置百度语音的参数
        Bundle params = new Bundle();
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, "g39DHotSDo6SgMuf3n1uS2WE");
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, "e0d79ac15958302f5483c94b2d0b40ea");
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
}
