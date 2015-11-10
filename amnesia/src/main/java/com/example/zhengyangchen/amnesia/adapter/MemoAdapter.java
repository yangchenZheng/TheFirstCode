package com.example.zhengyangchen.amnesia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Memo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by zhengyangchen on 2015/10/26.
 */
public class MemoAdapter extends ArrayAdapter<Memo> {
    private int resourceId;

    public MemoAdapter(Context context, int resource, List<Memo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Memo memo = getItem(position);
        viewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new viewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder.time = (TextView) convertView.findViewById(R.id.id_time);
            viewHolder.content = (TextView) convertView.findViewById(R.id.id_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MemoAdapter.viewHolder) convertView.getTag();
        }
        viewHolder.time.setText(dateLongToString(memo.getDateStr()));
        viewHolder.content.setText(memo.getMemoDesc());
        return convertView;
    }

    public static String dateLongToString(long dateStr) {
        Date date = new Date(dateStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
        String str = dateFormat.format(date);
        return str;
    }

    class viewHolder {
        TextView content;
        TextView time;
    }
}
