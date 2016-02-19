package com.example.administrator.topactivity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.topactivity.Const;
import com.example.administrator.topactivity.service.DaemonService;
import com.example.administrator.topactivity.utils.FileUtil;
import com.example.administrator.topactivity.utils.Utils;

/**
 * Created by wangyt on 2016/1/29.
 * :
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null == action) {
            return;
        }
        if (action.equals(Const.HEART_BEAT_ACTION)) {
            FileUtil.writeLogtoSdcard(TAG, "beat alarm");
            Intent startIntent = new Intent(context, DaemonService.class);
            context.startService(startIntent);
        } else if (action.equals(Const.NOTIFICATION_BEAT_ACTION)) {
            FileUtil.writeLogtoSdcard(TAG, "beat notification");
            Utils.addNotification(context);
        }
    }
}
