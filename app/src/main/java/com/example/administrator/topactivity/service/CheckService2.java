package com.example.administrator.topactivity.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.administrator.topactivity.utils.Utils;
import com.example.administrator.topactivity.utils.log.NgdsLog;

/**
 * Created by wangyt on 2015/11/30.
 * : 守护进程2
 */
public class CheckService2 extends Service {
    private static final String TAG = "CheckService2";

    private static final int MSG_HANDLE_CHECK = 1;

    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        NgdsLog.initFileLoger(this, TAG);
        NgdsLog.e(TAG, "onCreate");
        mHandler = new HandleRun();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NgdsLog.e(TAG, "onStartCommand");
        mHandler.sendEmptyMessage(MSG_HANDLE_CHECK);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_HANDLE_CHECK);
        NgdsLog.e(TAG, "onDestroy");
    }

    private void handleCheck() {
        if (!Utils.isServiceRunning(this, CheckService.class.getName())) {
            startService(new Intent(this, CheckService.class));
        }
        mHandler.sendEmptyMessageDelayed(MSG_HANDLE_CHECK, 1000);
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
}
