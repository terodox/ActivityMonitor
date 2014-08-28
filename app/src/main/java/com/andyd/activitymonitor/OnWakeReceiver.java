package com.andyd.activitymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by AndrewA on 8/15/2014.
 * This will be called in response to the following broadcasts:
 *          - android.intent.action.BOOT_COMPLETED
 */
public class OnWakeReceiver extends BroadcastReceiver {
    public static final String TAG = "OnWakeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Waking up!" + intent.getAction());
        if(PreferenceHelper.getPollingCount(context) > 0) {
            //The service needs to be running! Get it going.
            AppMonitorService.setServiceAlarm(context, true);
        }
    }
}
