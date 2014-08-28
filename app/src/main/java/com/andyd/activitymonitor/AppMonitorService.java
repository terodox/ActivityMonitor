package com.andyd.activitymonitor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AndrewA on 7/31/2014.
 * This service will also hang on to a broadcast receiver for screen_on
 *   This receiver must be declared dynamically because the screen_on broadcast is all kinds of
 *   weird.
 */
public class AppMonitorService extends IntentService {
    private static final String TAG = "AppMonitorService";

    private static final int POLLING_INTERVAL = 1000 * 5; //5 seconds

    public AppMonitorService() {
        super(TAG);
    }

    private SessionModel mSession;

    private BroadcastReceiver mScreenOnReceiver = null;

    @Override
    protected void onHandleIntent(Intent intent) {
        mSession = new SessionModel();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int pollingCount = PreferenceHelper.getPollingCount(this);
        mSession = PreferenceHelper.getCurrentSession(this);
        int alertTime = PreferenceHelper.getAlertTimeout(this);

        //If we move away from the app, this will be set.
        long currentTime = System.currentTimeMillis();

        String currentTopClass = "";

        //This is a quick shortcut to stop us from hitting sql unless we actually need to.
        if(pollingCount > 0) {
            ActivityController activityController =
                    ActivityController.get(getApplicationContext());
            //Is the screen evn on?
            PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
            //TODO: This is deprecated in API 20, but 20 is too new to just not use this.
            if(!powerManager.isScreenOn()) {
                if(mSession.getId() != 0) {
                    Log.i(TAG, "Screen has been turned off. Ending current session.");
                    //The screen was turned off. That means our session is definitively over!
                    mSession.setEnd(System.currentTimeMillis());
                    activityController.update(mSession);
                    mSession = new SessionModel();
                    //Clears out the currently running session
                    PreferenceHelper.updateCurrentSession(this, mSession);
                }
            } else {
                //Get a list of all activities currently polling.
                ArrayList<ActivityModel> pollingActivities = activityController.getActivitiesPolling();

                //Extra safety.
                if (pollingActivities.size() > 0) {
                    //Activity Manager is the android activity manager!
                    ActivityManager activityManager = (ActivityManager) getApplicationContext()
                            .getSystemService(Context.ACTIVITY_SERVICE);

                    //Get the task that is on the top of the back call stack
                    RunningTaskInfo taskInfo = activityManager.getRunningTasks(1).get(0);
                    currentTopClass = taskInfo.baseActivity.getPackageName();
                    Log.i(TAG, "Running Task: " + currentTopClass);
                    boolean classFound = false;
                    for (ActivityModel oneActivity : pollingActivities) {
                        if (currentTopClass.equals(oneActivity.getClassName())) {
                            classFound = true;
                            Log.d(TAG, "FOUND " + currentTopClass + " RUNNING!!!");
                            ActivityModel currentActivity =
                                    activityController.getActivityByClass(currentTopClass);
                            //Update the alert timeout.
                            PreferenceHelper
                                    .setAlertTimeout(this, currentActivity.getAlertTimeout());
                            if (currentActivity.getId() != mSession.getActivityId()) {
                                //If it's the same as the current session, then we don't do anything.
                                if (mSession.getId() != 0) {
                                    //If it's different, then we need to update the previous session with an end
                                    // time and create a new session
                                    mSession.setEnd(System.currentTimeMillis());
                                    //The reason this works without having the known start time is that,
                                    // the start time will not be updated.  Since it is set to 0 it will not
                                    // be passed along and therefore will not be updated.
                                    activityController.update(mSession);
                                }
                                mSession = new SessionModel(
                                        0, currentActivity.getId(), System.currentTimeMillis());
                                //This insert may yield a previous session if it was running within the last
                                // 5 minutes.
                                mSession = activityController.insert(mSession);
                            }
                        }
                    }
                    if (!classFound && mSession.getId() != 0) {
                        //We are currently in a monitoring session, but we are no longer in that app.
                        // Set the end for the current session, but reserve the ability to pull it back out
                        // if the app is resumed within 5 minutes.
                        mSession.setEnd(System.currentTimeMillis());
                        activityController.update(mSession);
                        mSession = new SessionModel();
                    }

                    //We are done with the logic of what is running and session management.
                    // Now let's update prefs and be done.
                    PreferenceHelper.updateCurrentSession(this, mSession);

                    //Handle Notification for lon running apps
                    if(mSession.getStart() != 0 && mSession.getEnd() == 0 &&
                            alertTime != 0 && //A zero here means, no alerts.
                            System.currentTimeMillis() - mSession.getStart() >
                                    (alertTime * 60 * 1000)) {
                        //If the app has been in use for more than alertTime minutes,
                        // throw a notification that they have been using it for a long time
                        sendNotification(this, mSession);
                    } else {
                        clearNotification(this);
                    }
                }
            }
        } //else case is handled when an activity model is updated to not poll anymore
    }

    private void sendNotification(Context context, SessionModel mSession) {
        long runTime = System.currentTimeMillis() - mSession.getStart();
        ActivityController activityController =
                ActivityController.get(getApplicationContext());

        ActivityModel currentActivity =
                activityController.getActivityById(mSession.getActivityId());

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ActivityMonitorListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                ActivityMonitorListActivity.REQUEST_NOTIFICATION, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(currentActivity.getName() + " Overuse!")
                .setContentText("Session Length: "
                        + NumberUtils.intToHourMinSec(runTime))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .build();

        notificationManager.notify(
                NotificationHelper.NotificationConstants.APP_MONITOR_SERVICE_NOTIFICATION_LONG_APP_USE_TIME,
                notification);
    }

    private void clearNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.cancel(
                NotificationHelper.NotificationConstants.APP_MONITOR_SERVICE_NOTIFICATION_LONG_APP_USE_TIME);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = new Intent(context, AppMonitorService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLLING_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
