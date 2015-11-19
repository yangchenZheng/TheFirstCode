package com.example.zhengyangchen.amnesia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.util.ImageLoader;
import com.example.zhengyangchen.amnesia.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhengyangchen on 2015/11/19.
 */
public class SelectPhotoAdapter extends BaseAdapter {

    private static Set<String> mSelectPhotoPaths = new HashSet<>();
    private final Context mContext;
    private String mImageDir;
    private List<String> mImagePathList;
    private LayoutInflater mLayoutInflater;

    public SelectPhotoAdapter(Context context, String imageDir, List<String> mImagePathList) {
        this.mContext = context;
        this.mImageDir = imageDir;
        this.mImagePathList = mImagePathList;
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mImagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String path = mImageDir + "/" + mImagePathList.get(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.id_item_image);
            viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.id_item_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //初始化
        viewHolder.imageView.setImageResource(R.drawable.pictures_no);
        viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
        viewHolder.imageView.setColorFilter(null);

        //获取imageLoader实例
        ImageLoader imageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        imageLoader.loadImage(path, viewHolder.imageView);


        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经被选择 清除选中状态
                if (mSelectPhotoPaths.contains(path)) {
                    viewHolder.imageButton.setImageResource(R.drawable.picture_unselected);
                    viewHolder.imageView.setColorFilter(null);
                    mSelectPhotoPaths.remove(path);
                } else {//未被选择
                    if (mSelectPhotoPaths.size() > 8) {
                        Util.showShortToast(mContext, "您选择的图片已经超过9张了！");
                    } else {
                        viewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
                        viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
                        mSelectPhotoPaths.add(path);
                    }
                }
            }


        });

        if (mSelectPhotoPaths.contains(path)) {
            viewHolder.imageButton.setImageResource(R.drawable.pictures_selected);
            viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
        }

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        ImageButton imageButton;
    }

    /**
     * 将选中的照片地址抛出
     *
     * @return
     */
    public static Set<String> getSelectPhotoPaths() {
        return mSelectPhotoPaths;
    }
}
