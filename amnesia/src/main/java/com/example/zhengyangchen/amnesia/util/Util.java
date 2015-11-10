package com.example.zhengyangchen.amnesia.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhengyangchen on 2015/10/28.
 */
public class Util {
    public static void showShortToast(Context context,String content) {
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(Context context,String content) {
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }
}
