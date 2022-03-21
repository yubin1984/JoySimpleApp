package com.yubin.simpleapp.base;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    private static Context mContext;
    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        mContext = this.getApplicationContext();
        super.onCreate();
    }
}
