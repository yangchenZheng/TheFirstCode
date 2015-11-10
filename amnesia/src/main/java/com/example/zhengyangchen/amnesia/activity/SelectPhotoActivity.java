package com.example.zhengyangchen.amnesia.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.ImageFolder;
import com.example.zhengyangchen.amnesia.util.Util;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SelectPhotoActivity extends AppCompatActivity {
    private static final int COMPLETE_SCANNING = 0x11;
    /**
     * 展示所有图片的gridView
     */
    private GridView mGridView;
    /**
     * gridView的数据适配器
     */
    private Adapter mAdapter;
    /**
     * 弹出PopupWindow的textView
     */
    private TextView mInPopupWindowTv;
    /**
     * 弹出预览界面的textView
     */
    private TextView mPreviewTv;
    /**
     * 存储所有文件夹的list
     */
    private List<ImageFolder> mImageFolderList;
    /**
     * 扫描图片时显示的对话框进度
     */
    private ProgressDialog mProgressDalog;
    /**
     * 临时的辅助类，用于保存图片的跟目录
     */
    private HashSet<String> mdDirPath = new HashSet<String>();
    /**
     * 图片最多的数量的文件夹
     */
    private File mMaxPictureDir;
    /**
     * 临时存储文件夹中数量最大值
     */
    private int mPictureCount;
    /**
     * 存储文件夹中图片的数量
     */
    private int totalCount = 0;
    /**
     * 所有图片的地址
     */
    private List<String> mImageList;

    private Handler mHandler = new Handler(){
        //在主线程中给adapter设置数据
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETE_SCANNING) {
                mProgressDalog.dismiss();
                //将数据绑定到view
                dataToView();
            }
        }
    };

    private void dataToView() {
        if (mMaxPictureDir == null) {
            Util.showShortToast(this, "没有扫描到图片");
            return;
        }

        mImageList = Arrays.asList(mMaxPictureDir.list());






    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        initView();
        initData();
        initEvent();
    }


    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_select_photo_gv);
        mInPopupWindowTv = (TextView) findViewById(R.id.id_select_photo_in_popup_window);
        mPreviewTv = (TextView) findViewById(R.id.id_select_photo_preview);
    }

    private void initData() {
        //获取图片信息
        getImages();


    }

    private void initEvent() {
    }

    private void getImages() {
        //检查外部存储状态，即是否有sd卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Util.showShortToast(this, "当前存储卡不存在或不可用");
            return;
        }

        //显示一个进度条
        mProgressDalog = ProgressDialog.show(this, null, "正在加载...");

        /**
         * 开启一个新的线程用于处理扫描耗时任务
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //用来存储第一张图片的地址
                String firstImagePath = null;
                /**
                 *获取系统图片的Uri
                 */
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = SelectPhotoActivity.this.getContentResolver();
                Cursor cursor = contentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                + MediaStore.Images.Media.MIME_TYPE + " = ?", new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (cursor.moveToNext()) {
                    //遍历出来一张图片的地址
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File fileParent = new File(imagePath).getParentFile();
                    if (firstImagePath == null) {
                        //将第一个图片地址进行保存
                        firstImagePath = imagePath;
                    }
                    if (fileParent == null)
                        //如果拿到的父目录为空的话就跳出，进行下次循环
                        continue;
                    String dirPath = fileParent.getAbsolutePath();
                    ImageFolder imageFolder = null;
                    if (mdDirPath.contains(dirPath)) {
                        continue;
                    } else {
                        mdDirPath.add(dirPath);
                        //初始化ImageFolder
                        imageFolder = new ImageFolder();
                        imageFolder.setDir(dirPath);
                        imageFolder.setFirstImagePath(firstImagePath);
                    }
                    //获取该文件夹内图片的数量
                    int pictureCount = fileParent.list(new FilenameFilter() {
                        //过滤文件夹内的文件
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    //将图片数量累计
                    totalCount += pictureCount;
                    //将当前文件夹中图片的数量保存到文件夹实例中去
                    imageFolder.setCount(pictureCount);
                    //将实例保存到list
                    mImageFolderList.add(imageFolder);

                    if (pictureCount > mPictureCount) {
                        mMaxPictureDir = fileParent;
                        mPictureCount = pictureCount;
                    }
                }

                //完成扫描
                cursor.close();
                mdDirPath = null;
                //通知handler完成扫描
                mHandler.sendEmptyMessage(COMPLETE_SCANNING);
            }
        });

    }

    private class MyAdapter extends BaseAdapter{
        private String mImageDir;
        private List<String> mImagePathList;

        public MyAdapter(Context context,String imageDir, List<String>  imagePaths) {
            this.mImageDir = imageDir;
            this.mImagePathList = imagePaths;
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}

