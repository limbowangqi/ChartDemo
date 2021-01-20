package com.limbo.chartdemo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.limbo.chartdemo.MyApp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangqi on 2018/8/2.
 */
public class MyUtils {

    public static int dp2px(float dpValue) {
        final float scale = MyApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static float sp2px(float spValue) {
        final float scale = MyApp.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    /**
     *  性别转换  man -->男  woman-->女
     * @param sex
     */
    public static String setSex(String sex){
        String s= "不明";
        if (TextUtils.isEmpty(sex))
            return s;
        if ("man".equals(sex))
            return "男";
        if ("woman".equals(sex))
            return "女";
        return s;
    }

    /**
     *
     * @param activity
     * @param bgAlpha
     *            屏幕透明度0.0-1.0
     *            1表示完全不透明
     */
    public static void setBackgroundAlpha(Activity activity , float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }

    /**
     *  格式化小数位
     * @param d
     * @param point
     * @return
     */
    public static String formatDouble(Double d, int point) {
        if(point < 0) {
            throw new IllegalStateException("小数点位数不合法:" + point);
        } else {
            String format = "0";
            if(point > 0) {
                format = format + ".";
            }

            for(int i = 0; i < point; ++i) {
                format = format + "0";
            }

            DecimalFormat df = new DecimalFormat(format);
            return df.format(d);
        }
    }

    public static String formatTime(long time, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(long2Date(time));
    }

    public static Date long2Date(long time){
        Date date = new Date();
        date.setTime(time);
        return date;
    }

    public static int getWeekOfDate(Date dt) {
        int[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }


}
