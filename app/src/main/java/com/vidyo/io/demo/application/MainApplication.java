package com.vidyo.io.demo.application;

import android.app.Application;


/**
 * Summary: MainApplication Component
 * Description: Calls when the application starts
 * @author RSI
 * @date 16.08.2018
 */
public class MainApplication extends Application {

    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }
}