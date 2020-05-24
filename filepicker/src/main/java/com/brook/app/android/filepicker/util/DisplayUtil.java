package com.brook.app.android.filepicker.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Brook
 * @time 2020-03-14 17:25
 */
public class DisplayUtil {


    private DisplayUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static float dp2px(Context context, float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }


    /**
     * 获取View在屏幕中X轴位置
     *
     * @param view
     * @return
     */
    public static int getScreenX(View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        return xy[0];
    }


    /**
     * 获取View在屏幕中Y轴位置
     *
     * @param view
     * @return
     */
    public static int getScreenY(View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        return xy[1];
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}