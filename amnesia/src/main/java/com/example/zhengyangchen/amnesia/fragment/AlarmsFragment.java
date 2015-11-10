package com.example.zhengyangchen.amnesia.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.adapter.AlarmsCursorAdapter;
import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.contentProvider.AmnesiaProvider;
import com.example.zhengyangchen.amnesia.dao.AlarmsDB;
import com.example.zhengyangchen.amnesia.util.BaiduSpeech;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * 闹钟的fragment
 * Created by zhengyangchen on 2015/10/22.
 */
public class AlarmsFragment extends Fragment {
    public static final String TAG = "zyc";
    /**
     * 用于展示所有闹钟的listview
     */
    private ListView mListView;
    /**
     * listView的适配器
     */
    private AlarmsCursorAdapter mCursorAdapter;
    /**
     * loaderId
     */
    public static final int LOADER_ID = 2;
    /**
     * floatingButton
     */
    private FloatingActionsMenu mFlButtonMenu;
    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.alarms_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化loader
        initLoader();
        //初始化view
        initView();
        //处理各种事件
        initEvent();
    }

    private void initEvent() {
        //listView的长按监听事件，删除该条记录
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
                Log.d("zyc", "alarmsListView onLongClick");
                //获取view中的textView
                TextView textView = (TextView) view.findViewById(R.id.alarms_id);
                //获取id
                int alarmsId = Integer.valueOf(textView.getText().toString());
                //通过id删除对应的项
                AlarmsDB.deleteAlarmsById(alarmsId, getContext());
                return true;
            }
        });
        //设置添加按钮单机监听事件
        mFlButtonMenu.setOnMenuFloatingActionButtonLongClickListener(
                new FloatingActionsMenu.OnMenuFloatingActionButtonLongClickListener() {
            @Override
            public void onLongClick(View view) {
                //开始百度语音
                startBaiduSpeechAndSaveAlarm();
            }
        });
    }


    private void initView() {
        mFlButtonMenu = (FloatingActionsMenu) mView.findViewById(R.id.id_fab_add);
        mListView = (ListView) mView.findViewById(R.id.id_lv_alarms);
        mListView.setEmptyView(mView.findViewById(R.id.alarm_empty));
        mCursorAdapter = new AlarmsCursorAdapter(getActivity(), null, false);
        mListView.setAdapter(mCursorAdapter);
    }

    private void initLoader() {
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getActivity(), AmnesiaProvider.URI_ALARMS_ALL
                        , null, null, null, Alarms.COLUMN_ID + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (loader.getId() == LOADER_ID) {
                    mCursorAdapter.swapCursor(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                if (loader.getId() == LOADER_ID) {
                    mCursorAdapter.swapCursor(null);
                }
            }
        });
    }


    private void startBaiduSpeechAndSaveAlarm() {
        BaiduSpeech baiduSpeech = new BaiduSpeech(getActivity());
        BaiduASRDigitalDialog dialog = baiduSpeech.getBaiduASRDialogInstance();
        dialog.setDialogRecognitionListener(new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                    //将结果中的json_res部分提取出来
                    JSONArray result = BaiduSpeech.getJsonArrayResult(rs.get(0));
                    try {
                        //如果提取结果为空toast提示
                        if (result != null) {
                            //将jasonArray中的结果转换成alarms对象
                            Alarms alarms = AlarmsDB.getAlarmsInstance(result);
                            //将闹钟设置为启动状态
                            alarms.setIsAlarmsRun(Alarms.ALARMS_IS_RUNING);
                            //alarms实例保存到数据库
                            AlarmsDB.getInstance(getActivity()).saveAlarmsByContentProvider(alarms);
                        } else {
                            //如果空就提示
                            Toast.makeText(getActivity(), "请确保您的语义中有提醒您的意思，" +
                                    "例如”叫我“，”提醒我“，等类似词", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();
    }
}
