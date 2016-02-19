package com.example.administrator.topactivity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.topactivity.service.DaemonService;
import com.example.administrator.topactivity.utils.FileUtil;

/**
 * Created by wangyt on 2016/1/29.
 * :
 */
public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null == action) {
            FileUtil.writeLogtoSdcard(TAG, "action is null");
            return;
        }
        FileUtil.writeLogtoSdcard(TAG, "Package change");
        Intent startIntent = new Intent(context, DaemonService.class);
        context.startService(startIntent);
    }
}
