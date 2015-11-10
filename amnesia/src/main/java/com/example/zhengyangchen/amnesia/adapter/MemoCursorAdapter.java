package com.example.zhengyangchen.amnesia.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Memo;

/**
 * 备忘listView的适配器
 * Created by zhengyangchen on 2015/10/27.
 */
public class MemoCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;
    public MemoCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.memo_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTime = (TextView) view.findViewById(R.id.id_time);
        TextView tvContent = (TextView) view.findViewById(R.id.id_content);
        TextView tvId = (TextView) view.findViewById(R.id.memo_id);
        long time = cursor.getLong(cursor.getColumnIndex(Memo.COLUMN_DATE_STR));
        tvTime.setText(MemoAdapter.dateLongToString(time));
        tvContent.setText(cursor.getString(cursor.getColumnIndex(Memo.COLUMN_MEMO_DESC)));
        int id = cursor.getInt(cursor.getColumnIndex(Memo.COLUMN_ID));
        tvId.setText(String.valueOf(id));
    }
}
