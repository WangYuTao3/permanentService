package com.example.administrator.topactivity.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import com.example.administrator.topactivity.utils.FileUtil;
import com.example.administrator.topactivity.utils.Utils;

/**
 * Created by wangyt on 2015/11/30.
 * :
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";

    private static final int MSG_HANDLE_CHECK = 1;

    private static Handler mHandler;

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtil.writeLogtoSdcard(TAG, "onCreate",true);
        mHandler = new HandleRun();
        mHandler.sendEmptyMessage(MSG_HANDLE_CHECK);

        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "daemon service");
        mWakeLock.setReferenceCounted(false);
        Utils.addNotification(this);
//        Utils.startAndGetServiceAlarm(this, 1000 * 60);
        Utils.startAndGetBroadcastAlarm(this, 1000 * 60);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FileUtil.writeLogtoSdcard(TAG, "onStartCommand",true);
        Utils.addNotification(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //fix leak
        mWakeLock.release();
        mHandler.removeMessages(MSG_HANDLE_CHECK);
        FileUtil.writeLogtoSdcard(TAG, "onDestroy",true);
        startService(new Intent(this, DaemonService.class));
    }

    class HandleRun extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_CHECK:
                    handleCheck();
                    break;
            }
        }
    }

    private void handleCheck() {
        FileUtil.writeLogtoSdcard(TAG, "check",true);
        mWakeLock.acquire(1000 * 60);
        if (!Utils.isServiceRunning(this, MyService.class.getName()))
            startService(new Intent(this, MyService.class));
        mWakeLock.release();

        mHandler.sendEmptyMessageDelayed(MSG_HANDLE_CHECK, 1000 * 20);
    }

}
