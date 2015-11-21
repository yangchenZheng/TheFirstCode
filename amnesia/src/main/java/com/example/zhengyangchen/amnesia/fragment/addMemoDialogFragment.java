package com.example.zhengyangchen.amnesia.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.dao.MemoDB;
import com.example.zhengyangchen.amnesia.util.ClearEditText;
import com.example.zhengyangchen.amnesia.util.OnNotifyDataSetChangedListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zhengyangchen on 2015/10/26.
 */
public class addMemoDialogFragment extends DialogFragment {
    private View view;
    private ClearEditText clearEditText;
    private Button cancelButton;
    private OnNotifyDataSetChangedListener mListener ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_memo_dialog_fragment, container, false);
        //初始化view
        initView();
        //事件处理
        initEvent();
        return view;
    }

    private void initEvent() {
        clearEditText.setOnSpeakIconClickListener(new ClearEditText.onSpeakIconClickListener() {
            @Override
            public void onClick() {
                //触发语音转文字，将结果显示到edittext上
                setContentInEditTextByDiaLog(getContext(), clearEditText);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        //给这个dialog设置按键监听
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                //按下back键
                if (i == KeyEvent.KEYCODE_BACK) {
                    //将数据保存到数据库
                    ToDatebase();
                    //将dialog取消
                    getDialog().dismiss();
                }
                return false;
            }
        });

    }

    private void ToDatebase() {
        Memo memo = new Memo();
        String content = clearEditText.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            memo.setMemoDesc(content);
            memo.setDateStr(new Date().getTime());
            //像数据库中存入数据
            MemoDB.getInstance(getContext()).saveMemoByContentProider(memo);
            mListener.notifyDataChangeListener(memo);
        } else {
            Toast.makeText(getActivity(),"不能保存一条空记录！",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void initView() {
        //设置标题
        getDialog().setTitle("快速记事");
        //获取各个组件的实例
        clearEditText = (ClearEditText) view.findViewById(R.id.id_dialog_edittext);
        cancelButton = (Button) view.findViewById(R.id.id_dialog_cancle);
    }

    /**
     * 打开语音转文字，并将文字显示到clearEditText上
     *
     * @param context
     * @param editText
     */
    public static synchronized void setContentInEditTextByDiaLog(Context context, final EditText editText) {
        final String[] result = new String[1];
        //创建用于设置参数的bundle
        Bundle params = new Bundle();
        //百度开发的key
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, "g39DHotSDo6SgMuf3n1uS2WE");
        //secret_key
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, "e0d79ac15958302f5483c94b2d0b40ea");
        //获取语音对话框实例
        BaiduASRDigitalDialog baiduASRDigitalDialog = new BaiduASRDigitalDialog(context, params);
        //设置监听事件，在回调中获得结果
        baiduASRDigitalDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                    //将结果显示到界面上
                    editText.setText(rs.get(0));
                    //用于将光标显示到正确的位置，而不是还在第一位
                    editText.setSelection(rs.get(0).length());
                }
            }
        });
        //将对话框显示出来
        baiduASRDigitalDialog.show();
    }


    public void setOnNotifyDataSetChangedListener(OnNotifyDataSetChangedListener onNotifyDataSetChangedListener) {
        this.mListener = onNotifyDataSetChangedListener;
    }



}
