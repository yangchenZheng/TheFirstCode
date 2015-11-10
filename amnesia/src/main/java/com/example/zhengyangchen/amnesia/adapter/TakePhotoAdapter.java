package com.example.zhengyangchen.amnesia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.util.ImageLoader;

import java.util.List;

/**
 * 添加图片页面中的gridView的适配器
 * Created by zhengyangchen on 2015/11/6.
 */
public class TakePhotoAdapter extends ArrayAdapter<String> {

    /**
     * 获取布局
     */
    private LayoutInflater mLayoutInflater;
    /**
     * 图片地址
     */
    private List<String> mPathList;
    /**
     * 子布局id
     */
    private int resourceID;


    public TakePhotoAdapter(Context context, int resource, List<String> pathList) {
        super(context, resource, pathList);
        mLayoutInflater = LayoutInflater.from(context);
        this.mPathList = pathList;
        this.resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(resourceID, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //获取imageLoader实例
        ImageLoader imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        //通过loadImage加载图片
        imageLoader.loadImage(mPathList.get(position), viewHolder.imageView);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}



