package com.yubin.simpleapp.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.yubin.simpleapp.DownloadService.DownloadListener;
import com.yubin.simpleapp.DownloadService.DownloadUtil;
import com.yubin.simpleapp.R;
import com.yubin.simpleapp.utils.AppUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownLoadService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.joy.service.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.joy.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.joy.service.extra.PARAM2";

    public DownLoadService() {
        super("DownLoadService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownLoadService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    private Context mContext;

    private String param1;
    private String param2;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                param1 = intent.getStringExtra(EXTRA_PARAM1);
                param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        download(param1);
    }

    // todo 后面再修改和封装一下，启动service去下载
    private NotificationManager manager;
    public static boolean isDownload = false;

    private void download(String url) {
        if (isDownload) {
            Toast.makeText(mContext,"安装包正在下载...", Toast.LENGTH_SHORT);
            return;
        }

        if (!NotificationManagerCompat.from(mContext).areNotificationsEnabled()) {
            handler.post(() ->             Toast.makeText(mContext,"您未开启通知栏权限", Toast.LENGTH_SHORT));
        }
        isDownload = true;
        DownloadUtil.download(url, new DownloadListener() {
            @Override
            public void onStart() {
                handler.post(() -> {
                    if (manager == null) {
                        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("apk", "腾讯为村更新", NotificationManager.IMPORTANCE_DEFAULT);
                            channel.enableLights(true);//是否在桌面icon右上角展示小红点
                            channel.setLightColor(Color.GREEN);//小红点颜色
                            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
                            channel.setSound(null, null);
                            manager.createNotificationChannel(channel);
                        }
                    } else {
                        manager.notify(1, new NotificationCompat.Builder(mContext, "apk")
                                .setContentTitle("下载提示")
                                .setContentText("安装包即将开始下载")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .build());
                    }
                    Toast.makeText(mContext,"安装包正在下载...", Toast.LENGTH_SHORT);
                });
            }

            @Override
            public void onProgress(int progress) {
                handler.post(() -> {
                    if (manager == null) {
                        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    } else {
                        manager.notify(1, new NotificationCompat.Builder(mContext, "apk")
                                .setContentTitle("下载提示")
                                .setContentText("安装包正在下载" + progress + "%")
                                .setProgress(100, progress, false)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .build());
                    }
                });
            }

            @Override
            public void onFinish(String path) {
                if (manager == null) {
                    manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                } else if (isDownload) {
                    isDownload = false;
                    manager.cancel(1);
                    handler.postDelayed(() -> AppUtils.installApk(mContext, path), 1000);
                }
            }

            @Override
            public void onFail(String errorInfo) {
                handler.post(() -> {
                    if (manager == null) {
                        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    } else {
                        manager.notify(1, new NotificationCompat.Builder(mContext, "apk")
                                .setContentTitle("下载提示")
                                .setContentText("安装包下载失败")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .build());
                    }
                    isDownload = false;
                    Toast.makeText(mContext,"安装包下载失败", Toast.LENGTH_SHORT);
                });
            }
        });
    }
}