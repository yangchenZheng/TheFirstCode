package com.example.zhengyangchen.amnesia.util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 主界面中的按钮
 * Created by zhengyangchen on 2015/11/18.
 */
public class MainMenu extends ViewGroup {
    /**
     * 菜单的状态
     */
    private Status mStatus = Status.CLOSE;

    private FloatingActionButton mMainMenuFLAB;

    public enum Status {
        CLOSE,OPEN;
    }

    public MainMenu(Context context) {
        this(context,null);
    }

    public MainMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MainMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
