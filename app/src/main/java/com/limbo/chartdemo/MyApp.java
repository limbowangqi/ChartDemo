package com.limbo.chartdemo;

import android.app.Application;

/**
 * Created by wangqi on 2018/8/2.
 */
public class MyApp extends Application {

    public static final String BASE_URL = "http://192.168.10.86/";

    private static MyApp INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static MyApp getInstance() {
        return INSTANCE;
    }
}
