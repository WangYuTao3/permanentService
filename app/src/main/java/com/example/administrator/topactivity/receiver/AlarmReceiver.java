package com.example.administrator.topactivity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.topactivity.Const;
import com.example.administrator.topactivity.service.CheckService;
import com.example.administrator.topactivity.service.CheckService2;
import com.example.administrator.topactivity.utils.Utils;
import com.example.administrator.topactivity.utils.log.NgdsLog;

/**
 * Created by wangyt on 2016/1/29.
 * : 闹钟以及notification移除广播接收器
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NgdsLog.initFileLoger(context, TAG);
        String action = intent.getAction();
        if (null == action) {
            NgdsLog.e(TAG, "action is null");
            return;
        }
        if (action.equals(Const.HEART_BEAT_ACTION)) {
            NgdsLog.e(TAG, "beat alarm");
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
            Intent startIntent2 = new Intent(context, CheckService2.class);
            context.startService(startIntent2);
        } else if (action.equals(Const.NOTIFICATION_BEAT_ACTION)) {
            NgdsLog.e(TAG, "beat notification");
            Utils.addNotification(context);
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
            Intent startIntent2 = new Intent(context, CheckService2.class);
            context.startService(startIntent2);
        }
    }
}
