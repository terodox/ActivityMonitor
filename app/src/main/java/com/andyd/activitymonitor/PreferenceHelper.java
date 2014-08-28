package com.andyd.activitymonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by AndrewA on 8/15/2014.
 * This class accomplishes two goals:
 * 1) Isolate the way we are storing preferences in case we want to change that in the future
 * 2) Create an easy path for manipulating preferences within the system
 */
public class PreferenceHelper {
    public static final String TAG = "PreferenceHelper";

    private static class Preferences {
        public static final String PREF_CURRENT_SESSION_ID = "currentSessionId";
        public static final String PREF_CURRENT_SESSION_START = "currentSessionStart";
        public static final String PREF_CURRENT_ACTIVITY_ID = "currentActivityId";
        public static final String PREF_CURRENT_POLLING_COUNT = "currentPollingCount";
        public static final String PREF_CURRENT_ALERT_TIMEOUT = "currentAlertTimeout";
    }

    public static int getPollingCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(PreferenceHelper.Preferences.PREF_CURRENT_POLLING_COUNT, 0);
    }

    public static int subtsractFromCurrentPollingCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int pollingCount = prefs.getInt(PreferenceHelper.Preferences.PREF_CURRENT_POLLING_COUNT, 0);
        pollingCount--;
        if(pollingCount < 0) {
            pollingCount = 0;
        }
        prefs.edit()
                .putInt(Preferences.PREF_CURRENT_POLLING_COUNT, pollingCount)
                .commit();
        return pollingCount;
    }
    public static int addToCurrentPollingCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int pollingCount = prefs.getInt(PreferenceHelper.Preferences.PREF_CURRENT_POLLING_COUNT, 0);
        pollingCount++;
        prefs.edit()
                .putInt(Preferences.PREF_CURRENT_POLLING_COUNT, pollingCount)
                .commit();
        return pollingCount;
    }

    public static SessionModel getCurrentSession(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SessionModel sessionModel = new SessionModel();
        sessionModel.setId(
                prefs.getLong(Preferences.PREF_CURRENT_SESSION_ID, 0));
        sessionModel.setActivityId(
                prefs.getLong(Preferences.PREF_CURRENT_ACTIVITY_ID, 0));
        sessionModel.setStart(
                prefs.getLong(Preferences.PREF_CURRENT_SESSION_START, 0));
        return sessionModel;
    }
    public static void updateCurrentSession(Context context, SessionModel sessionModel) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putLong(Preferences.PREF_CURRENT_SESSION_ID, sessionModel.getId())
                .putLong(Preferences.PREF_CURRENT_ACTIVITY_ID, sessionModel.getActivityId())
                .putLong(Preferences.PREF_CURRENT_SESSION_START, sessionModel.getStart())
                .commit();
    }

    public static int getSessionPauseMax(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //Using the preferences setup in activity_monitor_list_activity_preferences.xml
        // we will try to retrieve them from the shared prefs, but to be safe our default
        // will be the default from our resources.

        //Get the default
        int sessionPauseMaxInt = Integer.parseInt(context.getResources().getString(
                R.string.activity_monitor_list_activity_preference_session_pause_length_defaultValue));
        //Try to get the pref, but default appropriately.
        String sessionPauseMaxString = prefs.getString(context.getResources().getString(
                        R.string.activity_monitor_list_activity_preference_session_pause_length_key),
                Integer.toString(sessionPauseMaxInt));

        //If we got a numeric value, then use it.
        if(NumberUtils.IsNumeric(sessionPauseMaxString)) {
            sessionPauseMaxInt = Integer.parseInt(sessionPauseMaxString);
        }
        return sessionPauseMaxInt;
    }

    public static int getAlertTimeout(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //Set the default
        int alertTimeInt = 0;
        //Try to get the pref, but default appropriately.
        alertTimeInt = prefs.getInt(Preferences.PREF_CURRENT_ALERT_TIMEOUT, alertTimeInt);
        return alertTimeInt;
    }

    public static void setAlertTimeout(Context context, int alertTimeout) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putInt(Preferences.PREF_CURRENT_ALERT_TIMEOUT, alertTimeout)
                .commit();
    }
}
