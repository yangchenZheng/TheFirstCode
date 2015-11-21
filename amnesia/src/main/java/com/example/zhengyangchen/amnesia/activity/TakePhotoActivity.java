package com.example.zhengyangchen.amnesia.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.adapter.TakePhotoAdapter;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.dao.MemoDB;
import com.example.zhengyangchen.amnesia.util.FileUtils;
import com.example.zhengyangchen.amnesia.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于显示添加图片事件的按钮
 * <p/>
 * 出现的问题：地址BitmapFactory.decodeFile()方法一直获取不到图片，
 * 最终是因为在拼接图片地址的时候将图片的格式字符遗忘。
 */
public class TakePhotoActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 2;
    private static final String ADD_PICTURE_ICON = "add_picture_icon";
    private static final int SELECT_PHOTO_PATHS = 1;
    /**
     * 描述信息的editText
     */
    private EditText mContentEdt;
    /**
     * 点击添加按钮显示的PopupWindow
     */
    private PopupWindow mPopupWindow;
    /**
     * popWindow设置动画的区域
     */
    private LinearLayout mLinearLayout;
    /**
     * 存放图片地址
     */
    private List<String> mPathList;
    /**
     * gridView的Adapter
     */
    private TakePhotoAdapter mTakePhotoAdapter;
    /**
     * 这个activity的View
     */
    private View mParentView;
    /**
     * LayoutInflater
     */
    private LayoutInflater mLayoutInflater;
    /**
     * 存储添加按钮的图片，防止多次存储
     */
    private SharedPreferences.Editor mPrefEditor;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        initData();
        //初始化view
        initView();
    }

    private void initData() {
        //初始化存储地址的list
        mPathList = new ArrayList<>();
        /*
      获取存在本地的简单数据
     */
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        //获取存储的地址
        String Path = mPref.getString(ADD_PICTURE_ICON, "");
        //判断获得的地址时候为空
        if (TextUtils.isEmpty(Path)) {
            //为空的话，将资源中的图片进行保存
            //添加按钮的图片
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
            //保存添加按钮图片并且获得他的保存地址
            Path = saveBitmapAndGetPath(mBitmap);
            mPrefEditor = mPref.edit();
            //将地址保存到本地
            mPrefEditor.putString(ADD_PICTURE_ICON, Path);
            mPrefEditor.apply();
            mPrefEditor.clear();
        }
        //将获得的地址加入到arrayList
        mPathList.add(Path);
    }


    private void initView() {
        mLayoutInflater = LayoutInflater.from(this);
        //获取该activity的View，用于
        mParentView = mLayoutInflater.inflate(R.layout.activity_take_photo, null);
        //初始化GridView
        initGridView();
        mContentEdt = (EditText) findViewById(R.id.take_photo_edt);
        //初始化popupWindow
        initPopupWindow();
    }

    private void initGridView() {
        /*
      用于显示要提交的照片
     */
        GridView mNoScrollGridView = (GridView) findViewById(R.id.noScrollGridView);
        mTakePhotoAdapter = new TakePhotoAdapter(this, R.layout.item_published_grida, mPathList);
        mNoScrollGridView.setAdapter(mTakePhotoAdapter);
        //设置GirdView的点击事件
        mNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPathList.size() >= 9) {
                    Util.showShortToast(TakePhotoActivity.this, "最多添加 8 张照片！");
                }else {
                    if (mPathList.size() > 0 && mPathList.size() - 1 == position) {
                        //将popupWindow从屏幕的底部弹出
                        mLinearLayout.startAnimation(AnimationUtils.loadAnimation(TakePhotoActivity.this, R.anim.activity_translate_in));
                        mPopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
                    }
                }
            }
        });

        mNoScrollGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mPathList.size() - 1) {
                    mPathList.remove(position);
                    mTakePhotoAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });


    }

    private void initPopupWindow() {

        mLayoutInflater = LayoutInflater.from(this);
        //获取该activity的View
        mParentView = mLayoutInflater.inflate(R.layout.activity_take_photo, null);
        mPopupWindow = new PopupWindow(TakePhotoActivity.this);
        //获取popWindow的View
        View view = LayoutInflater.from(TakePhotoActivity.this).inflate(R.layout.item_popupwindows, null);
        //获取动画区域
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_popup);

        //配置popupWindow的一些属性
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setContentView(view);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        //相机选项的点击区域
        LinearLayout cameraLinearLayout = (LinearLayout) view.findViewById(R.id.item_popupWindows_camera);
        //相册选项的点击区域
        LinearLayout albumLinearLayout = (LinearLayout) view.findViewById(R.id.item_popupWindows_Photo);
        //退出选项的点击区域
        LinearLayout cancelLinearLayout = (LinearLayout) view.findViewById(R.id.item_popupWindows_cancel);
        //单击取消收回popupWindow
        cancelLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                setPopupWindowOutAnimation();
            }
        });
        //照相机选项的点击事件
        cameraLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开系统相机
                openCameraSavePhoto();
                mPopupWindow.dismiss();
                mLinearLayout.clearAnimation();
            }
        });
        //相册的点击事件，弹出相册选择照片的界面
        albumLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TakePhotoActivity.this, SelectPhotoActivity.class);
                startActivityForResult(intent, SELECT_PHOTO_PATHS);
                mPopupWindow.dismiss();
                setPopupWindowOutAnimation();
            }
        });
    }

    /**
     * 打开系统照相机
     */
    private void openCameraSavePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //将时间作为文件名
        String fileName = String.valueOf(System.currentTimeMillis());
         mPath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName + ".png";
        Uri uri = Uri.fromFile(new File(mPath));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    /**
     * 给popupWindow设置消失的动画
     */
    private void setPopupWindowOutAnimation() {
        mLinearLayout.startAnimation(AnimationUtils.loadAnimation(TakePhotoActivity.this, R.anim.activity_translate_out));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mPathList.size() < 9 && resultCode == RESULT_OK) {
//                    //从相机返回的数据中获取照片
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    String path = saveBitmapAndGetPath(bitmap);
                    if (mPathList.size() > 0) {
                        mPathList.add(mPathList.size() - 1, mPath);
                        //将mPath置为空
                        mPath = null;
                        mTakePhotoAdapter.notifyDataSetChanged();
                    } else {
                        throw new IllegalArgumentException("mPathList 为空 它没有得到正确的初始化");
                    }


                }
                break;
            case SELECT_PHOTO_PATHS:
                if (resultCode == RESULT_OK) {
                        List<String> paths = (ArrayList<String>) data.getExtras().get("select_photo_paths");
                    if (paths != null) {
                        for (int i = 0; i < paths.size(); i++) {
                            String path = paths.get(i);
                            if (mPathList.size() > 0) {
                                mPathList.add(mPathList.size() - 1, path);
                                mTakePhotoAdapter.notifyDataSetChanged();
                            } else {
                                throw new IllegalArgumentException("mPathList 为空 它没有得到正确的初始化");
                            }
                        }
                    }

                }
                break;
        }
    }


    /**
     * 以系统时间保存图片并得到保存图片的地址
     *
     * @param bitmap 一张图片
     */
    private String saveBitmapAndGetPath(Bitmap bitmap) {
        //当前时间的作为文件名
        String fileName = String.valueOf(System.currentTimeMillis());
        //将图片保存并返回图片地址
        String path = FileUtils.saveBitmap(bitmap, fileName);
        //回收bitmap防止内存溢出
        if (bitmap != null) {
            bitmap.recycle();
        }
        return path;
    }

    /**
     * 用于启动这个activity
     *
     * @param context 环境变量
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TakePhotoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //将数据保存到数据库中
        saveDataToDatabase();
    }

    /**
     * 将数据保存到数据库中
     */
    private void saveDataToDatabase() {
        String paths = listToString(mPathList);
        String content = mContentEdt.getText().toString();
        Memo memo = new Memo();
        memo.setPhotoUrl(paths);
        memo.setMemoDesc(content);
        memo.setDateStr(new Date().getTime());

        //保存到数据库
        MemoDB.getInstance(TakePhotoActivity.this).saveMemoByContentProider(memo);
    }

    /**
     * 将gridView的中的图片地址集合，转换为字符串以便存储到数据库中
     * @param mPathList 图片地址集合
     * @return 已经转换的图片地址字符串
     */
    private String listToString(List<String> mPathList) {
        String paths = null;
        for (int i = 0; i < mPathList.size() - 1; i++) {
            if (i == 0) {
                paths = mPathList.get(i);
            } else {
                paths = paths + " " + mPathList.get(i);
            }
        }
        return paths;
    }
}
