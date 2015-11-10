package com.example.zhengyangchen.amnesia.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Alarms;
import com.example.zhengyangchen.amnesia.bean.Date;
import com.example.zhengyangchen.amnesia.dao.AlarmsDB;
import com.example.zhengyangchen.amnesia.util.DateUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 闹钟listView的适配器
 * Created by zhengyangchen on 2015/10/29.
 */
public class AlarmsCursorAdapter extends CursorAdapter {


    private LayoutInflater layoutInflater;

    private Context mContext;

    public AlarmsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.alarms_listview_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        TextView tvTime = (TextView) view.findViewById(R.id.id_alarms_time);
        TextView tvContent = (TextView) view.findViewById(R.id.id_alarms_desc);
        final TextView tvId = (TextView) view.findViewById(R.id.alarms_id);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.id_alarms_item_status);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.id_alarms_item_rl);

        final int alarmStatus = cursor.getInt(cursor.getColumnIndex(Alarms.COLUMN_IS_ALARMS_RUN));
        //根据数据库中闹钟状态设置不同的图标
        if (alarmStatus == Alarms.ALARMS_IS_RUNING) {
            imageButton.setImageResource(R.drawable.ic_perm_group_device_alarms);
        } else {
            imageButton.setImageResource(R.drawable.ic_perm_group_system_clock);
        }


        long time = cursor.getLong(cursor.getColumnIndex(Alarms.COLUMN_ALARMS_RUN_TIME));
        //获取日期实例 里面包含了年 月 日 等
        Date date = getDateInstance(time);
        tvTime.setText(date.getMonthAndTime());
        tvContent.setText(cursor.getString(cursor.getColumnIndex(Alarms.COLUMN_ALARMS_DESC)));
        final int id = cursor.getInt(cursor.getColumnIndex(Alarms.COLUMN_ID));
        tvId.setText(String.valueOf(id));

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlarmsDB.deleteAlarmsById(id, mContext);
                return true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("zyc", "imageButton run");
                if (alarmStatus == Alarms.ALARMS_IS_RUNING) {
                    //imageButton.setBackgroundResource(R.drawable.ic_perm_group_system_clock);
                    AlarmsDB.updateAlarmStatus(id,Alarms.ALARMS_IS_NOT_RUNING, mContext);
                } else if (alarmStatus == Alarms.ALARMS_IS_NOT_RUNING){
                   // imageButton.setBackgroundResource(R.drawable.ic_perm_group_device_alarms);
                    AlarmsDB.updateAlarmStatus(id, Alarms.ALARMS_IS_RUNING, mContext);
                }

            }
        });

    }



    private Date getDateInstance(long time) {
        String stringTime = MemoAdapter.dateLongToString(time);
        String monthAndTime = DateUtil.getMonthAndTimeFromDateString(stringTime);
        int month = DateUtil.getMonthFromDateString(stringTime);
        Date date = new Date();
        date.setMonth(month);
        date.setMonthAndTime(monthAndTime);

        return date;
    }
}
