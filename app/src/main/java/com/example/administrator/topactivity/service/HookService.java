package com.example.administrator.topactivity.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import com.example.administrator.topactivity.utils.log.NgdsLog;

/**
 * Created by wangyt on 2016/1/18.
 * : 获取topActivity实现
 */
public class HookService extends AccessibilityService {
    private static final String TAG = "HookService";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        NgdsLog.initFileLoger(this, TAG);
        NgdsLog.e(TAG, "onServiceConnected");
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16)
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        NgdsLog.e(TAG, "onAccessibilityEvent");

        if (event == null) {
            return;
        }
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(), event.getClassName().toString()
                );
                ActivityInfo activityInfo = tryGetActivity(componentName);
                if (activityInfo != null) {
                    NgdsLog.e(TAG, componentName.flattenToShortString());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onInterrupt() {
        NgdsLog.e(TAG, "onInterrupt");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NgdsLog.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NgdsLog.e(TAG, "onDestroy");
    }
}
