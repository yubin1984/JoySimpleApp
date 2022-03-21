package com.yubin.simpleapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;

public class AppUtils {
    /**
     * get this App install package name
     *
     * @return com.cake.page
     */
    public static String getAppPackageName() {
        return SmallUtils.getApp().getApplicationInfo().packageName;
    }

    /**
     * get string app version
     */
    public static String getVersionName() {
        String versionName = "";
        try {
            PackageManager pm = SmallUtils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(SmallUtils.getApp().getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * get int app version
     */
    public static int getVersionCode() {
        int versioncode = 0;
        try {
            PackageManager pm = SmallUtils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(SmallUtils.getApp().getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    /**
     * install APK
     * <p>
     * 6.0++ need Dynamic permissions,you need add this part permissions into your AndroidManifest.xml
     * <p>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <p>
     * 7.0++ need FileProvider to make Uri ,don't care for it ,I have already do it!
     * more info about FileProvider [https://developer.android.google.cn/reference/android/support/v4/content/FileProvider.html]
     * <p>
     * <p>
     * <p>
     * TODO 1:create xml folder in [res] and create file_paths.xml in this xml
     * <p>
     * <?xml version="1.0" encoding="utf-8"?>
     * <resources>
     * <paths xmlns:android="http://schemas.android.com/apk/res/android">
     * <root-path path="" name="camera_photos" />
     * <root-path path="." name="download" />
     * </paths>
     * </resources>
     * <p>
     * TODO 2: take under code into <application></application>
     * <p>
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="${applicationId}.fileprovider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_paths"/>
     * </provider>
     */
    public static void installApk(Context context, String downloadApk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(downloadApk);
        Log.i("installApk", "安装路径==" + downloadApk);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }

    /**
     * get main module BuildConfig values
     *
     * @param context
     * @param fieldName
     * @return
     */
    public static Object getBuildConfigValue(Context context, String fieldName) {
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void goIntentSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", SmallUtils.getApp().getPackageName(), null);
        intent.setData(uri);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
