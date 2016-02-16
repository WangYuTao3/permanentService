package com.example.administrator.topactivity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.topactivity.service.DaemonService;
import com.example.administrator.topactivity.utils.log.NgdsLog;

/**
 * Created by wangyt on 2016/1/29.
 * :
 */
public class ConnectReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NgdsLog.initFileLoger(context, TAG);
        String action = intent.getAction();
        if (null == action) {
            NgdsLog.e(TAG, "action is null");
            return;
        }
        NgdsLog.e(TAG, "connectivity change");
        Intent startIntent = new Intent(context, DaemonService.class);
        context.startService(startIntent);
    }
}
