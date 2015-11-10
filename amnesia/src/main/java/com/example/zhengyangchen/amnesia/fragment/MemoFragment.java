package com.example.zhengyangchen.amnesia.fragment;


import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.activity.AddMemoActivity;
import com.example.zhengyangchen.amnesia.activity.TakePhotoActivity;
import com.example.zhengyangchen.amnesia.adapter.MemoCursorAdapter;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.contentProvider.AmnesiaProvider;
import com.example.zhengyangchen.amnesia.dao.MemoDB;
import com.example.zhengyangchen.amnesia.util.ArcMenu;

/**
 * 备忘fragment
 * Created by zhengyangchen on 2015/10/22.
 */
public class MemoFragment extends Fragment {
    /**
     * 这个碎片的布局view
     */
    private View mView;
    /**
     * 卫星菜单按钮
     */
    private ArcMenu mArcMenu;
    /**
     * listView
     */
    private ListView mListView;
    /**
     * listView的数据适配器
     */
    private CursorAdapter cursorAdapter;
    /**
     * loader的ID
     */
    private static final int LOADER_ID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.memo_fragment, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化loader
        initLoader();
        //初始化view
        initView();
        //事件处理
        initEvent();


    }

    /**
     * loader 用于当数据改变时通知adapter改变
     * <p/>
     * 遇到问题：在contentprovider中添加了新表，然后把MEMO_ALL查询的语句补全了，就出现了listView数据不会时时更新
     * 解决：在memo_alll查询语句结束后添加数据改变通知
     * 原因：？？？？？研究下loader原理
     */
    private void initLoader() {
        /**
         * 初始化loader，
         * 第一个参数：代表这个loader的id 一个activity或者fragment只有一个loaderManager，可以有多个
         */
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                /**
                 *  //将会查询数据库 注意：当出现和数据库有关系的问题时一定要把程序卸载了，以便将以前的数据库一起卸载了
                 *  创建loader 开始异步加载数据
                 */
                return new CursorLoader(getActivity(), AmnesiaProvider.URI_MEMO_ALL, null, null, null, Memo.COLUMN_ID + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (loader.getId() == LOADER_ID) {
                    //得到异步加载的数据，更新adapter
                    cursorAdapter.swapCursor(data);
                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                //移除adapter使用的loader，系统会释放不再使用的loader
                cursorAdapter.swapCursor(null);
            }
        });
    }


    private void initEvent() {
        //给微信菜单按钮添加监听事件
        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                //根据位置实现对应的逻辑
                switch (pos) {
                    case 1:
                        TakePhotoActivity.actionStart(getContext());
                        break;
                    case 3:
                        AddMemoActivity.actionStart(getContext());
                        break;


                }
            }
        });
        //卫星菜单的长按事件
        mArcMenu.setOnMenuLongClickListener(new ArcMenu.onMenuLongClickListener() {
            @Override
            public void onLongClick(View view) {
                //弹出快速添加的对话框
                addMemoDialogFragment addMemoDialogFragment = new addMemoDialogFragment();
                addMemoDialogFragment.show(getFragmentManager(), "addMemoDialog");
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取点击的view中的textView
                TextView textView = (TextView) view.findViewById(R.id.memo_id);
                //获取textView中存的id
                int memoId = Integer.valueOf(textView.getText().toString());
                //通过id将该条数据从数据库中删除
                MemoDB.deleteMemoById(memoId, getContext());
                return true;
            }
        });
    }

    private void initView() {
        //获取卫星菜单实例
        mArcMenu = (ArcMenu) mView.findViewById(R.id.id_menu);
        //获取listView实例
        mListView = (ListView) mView.findViewById(R.id.id_listview);
        //为listView空数据时的布局
        mListView.setEmptyView(mView.findViewById(R.id.empty));
        //创建MemoCursorAdapter
        cursorAdapter = new MemoCursorAdapter(getActivity(), null, false);
        //为listView设置adapter
        mListView.setAdapter(cursorAdapter);

    }


}
