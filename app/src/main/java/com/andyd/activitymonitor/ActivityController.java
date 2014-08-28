package com.andyd.activitymonitor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by AndrewA on 8/13/2014.
 * This singleton class will be used for the management of activities throughout the users
 * interaction.
 */
public class ActivityController {
    private static final String TAG = "ActivityController";
    private Context mContext;

    private static ActivityController mInstance;

    private static String[] mActivityProjection = new String[] {
            ActivityProviderMetaData.ActivityTableMetaData._ID,
            ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME,
            ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS,
            ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON,
            ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING,
            ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED
    };

    private static String[] mSessionProjection = new String[] {
            ActivityProviderMetaData.SessionTableMetaData._ID,
            ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID,
            ActivityProviderMetaData.SessionTableMetaData.SESSION_START,
            ActivityProviderMetaData.SessionTableMetaData.SESSION_END,
            ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED,
            ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED
    };

    private static String[] mSessionSummaryProjection = new String[] {
            ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID,
            ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_TOTAL_LENGTH,
            ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_LENGTH,
            ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_COUNT
    };

    private ActivityController(Context context) {
        mContext = context;
    }

    public static ActivityController get(Context context) {
        if(mInstance == null) {
            mInstance = new ActivityController(context);
        }
        return mInstance;
    }

    public ArrayList<ActivityModel> getActivities() {
        ArrayList<ActivityModel> activities = new ArrayList<ActivityModel>();

        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(
                baseUri,    //Uri
                mActivityProjection,       //Projection
                null,       //Selection
                null,       //SelectionArgs
                null);      //sortOrder

        ActivityModelCursorHelper activityCursorHelper = new ActivityModelCursorHelper(cursor);
        if(cursor.getCount() > 0) {
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                activities.add(activityCursorHelper.createModelFromCursor(cursor));
            }
        }
        cursor.close();
        return activities;
    }

    public ArrayList<ActivityModel> getActivitiesPolling() {
        ArrayList<ActivityModel> activities = new ArrayList<ActivityModel>();

        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(
                baseUri,    //Uri
                mActivityProjection,       //Projection
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING
                        + "=1",       //Selection
                null,       //SelectionArgs
                null);      //sortOrder

        ActivityModelCursorHelper activityCursorHelper = new ActivityModelCursorHelper(cursor);
        if(cursor.getCount() > 0) {
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                activities.add(activityCursorHelper.createModelFromCursor(cursor));
            }
        }
        cursor.close();
        return activities;
    }

    public ActivityModel getActivityByClass(String className) {
        ActivityModel selectedActivity = null;
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(
                baseUri,//Uri
                mActivityProjection,       //Projection
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS + "=?",//Selection
                new String[] {
                        className
                },       //SelectionArgs
                null);      //sortOrder
        //Now we need to get all of our column locations
        ActivityModelCursorHelper activityCursorHelper = new ActivityModelCursorHelper(cursor);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            selectedActivity = activityCursorHelper.createModelFromCursor(cursor);
        }
        cursor.close();
        return selectedActivity;
    }

    public ActivityModel getActivityById(long activityId) {
        ActivityModel selectedActivity = null;
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri selectorUri = Uri.withAppendedPath(baseUri, Long.toString(activityId));
        Cursor cursor = mContext.getContentResolver().query(
                selectorUri,//Uri
                mActivityProjection,       //Projection
                null,       //Selection
                null,       //SelectionArgs
                null);      //sortOrder
        //Now we need to get all of our column locations
        ActivityModelCursorHelper activityCursorHelper = new ActivityModelCursorHelper(cursor);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            selectedActivity = activityCursorHelper.createModelFromCursor(cursor);
        }
        cursor.close();
        return selectedActivity;
    }

    public SessionModel getLatestActivitySession(long activityId) {
        SessionModel sessionModel = null;
        ContentResolver resolver = mContext.getContentResolver();
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri sessionLatestUri = Uri.parse(baseUri.toString() + "/" + Long.toString(activityId)
                + "/sessions/latest");
        Cursor cursor =
                resolver.query(sessionLatestUri, mSessionProjection, null, null, null);
        SessionModelCursorHelper cursorHelper = new SessionModelCursorHelper(cursor);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            sessionModel = cursorHelper.createModelFromCursor(cursor);
        }
        return sessionModel;
    }

    public SessionSummaryModel getActivitySessionSummary(long activityId) {
        SessionSummaryModel summaryInfo = null;
        ContentResolver resolver = mContext.getContentResolver();
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri sessionSummaryUri = Uri.parse(baseUri.toString() + "/" + Long.toString(activityId)
                + "/sessionSummary");
        Cursor cursor =
                resolver.query(sessionSummaryUri, mSessionSummaryProjection, null, null, null);
        SessionSummaryCursorHelper cursorHelper = new SessionSummaryCursorHelper(cursor);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            summaryInfo = cursorHelper.createModelFromCursor(cursor);
        }
        return summaryInfo;
    }

    public SessionSummaryModel getActivitySessionSummarySince(long activityId, long sinceTime) {
        SessionSummaryModel summaryInfo = null;
        ContentResolver resolver = mContext.getContentResolver();
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri sessionSummaryUri = Uri.parse(baseUri.toString() + "/" + Long.toString(activityId)
                + "/sessionSummary");
        String selection = "start > ?";
        String[] selectionArgs = new String[] {
                Long.toString(sinceTime)
        };
        Cursor cursor =
                resolver.query(sessionSummaryUri,
                        mSessionSummaryProjection,
                        selection,
                        selectionArgs,
                        null);
        SessionSummaryCursorHelper cursorHelper = new SessionSummaryCursorHelper(cursor);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            summaryInfo = cursorHelper.createModelFromCursor(cursor);
        }
        return summaryInfo;
    }

    public ActivityModel insert(ActivityModel activityToInsert) {
        ActivityModel existingActivity = getActivityByClass(activityToInsert.getClassName());
        if(existingActivity != null) {
            //Pass out the one that already exists if they try to insert a duplicate.
            activityToInsert = existingActivity;
        } else {
            ContentResolver resolver = mContext.getContentResolver();
            Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
            Uri insertedUri = resolver.insert(baseUri, activityModelToContentValues(activityToInsert));
            activityToInsert.setId(Long.valueOf(insertedUri.getPathSegments().get(1)));
            Log.d(TAG, "Inserted activity: " + insertedUri);
        }
        //If they added an app, then the monitor service needs to be enabled
        AppMonitorService.setServiceAlarm(mContext, true);
        return activityToInsert;
    }

    public SessionModel insert(SessionModel sessionToInsert) {
        SessionModel latestSession = getLatestActivitySession(sessionToInsert.getActivityId());
        int sessionPauseMax = PreferenceHelper.getSessionPauseMax(mContext);
        long currentTime = System.currentTimeMillis();
        if(latestSession  != null &&
                (currentTime - latestSession.getEnd()) < (sessionPauseMax * 60 * 1000)) {
            //This activity has a session that occurred less than sessionPauseMax minutes ago!
            // return the previous session to continue working with it.
            // The value of sessionPauseMax can be 0.  This still works here, but should be noted
            // for any future updates
            latestSession.setEnd(0);
            //Reset the end time to 0 because it is still currently running.
            update(latestSession);
            sessionToInsert = latestSession;
        } else {
            ContentResolver resolver = mContext.getContentResolver();
            Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
            Uri selectorUri = Uri.parse(baseUri.toString() + "/"
                            + Long.toString(sessionToInsert.getActivityId())
                            + "/sessions"
            );
            Uri insertedUri =
                    resolver.insert(selectorUri, sessionModelToContentValues(sessionToInsert));
            sessionToInsert.setId(Long.valueOf(insertedUri.getPathSegments().get(1)));
            Log.d(TAG, "Inserted session: " + insertedUri);
        }
        return sessionToInsert;
    }

    public boolean update(ActivityModel activityModel) {
        boolean success = false;
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri selectorUri = Uri.withAppendedPath(baseUri, Long.toString(activityModel.getId()));
        ContentResolver resolver = mContext.getContentResolver();
        int affectedRowCount = resolver.update(
                selectorUri,
                activityModelToContentValues(activityModel),
                null,
                null);
        if(affectedRowCount > 0) {
            success = true;
        }
        //Once we've updated the activity, if it is no longer polling, then we need to make sure to
        // end it's current session.
        int pollingCount = 0;
        if(activityModel.getPolling() == 0) {
            //We're done polling this for now. Make sure to clean up any open polling sessions
            SessionModel latestSession = getLatestActivitySession(activityModel.getId());
            if(latestSession != null) {
                latestSession.setEnd(System.currentTimeMillis());
                update(latestSession);
            }
            pollingCount = PreferenceHelper.subtsractFromCurrentPollingCount(mContext);
        } else {
            pollingCount = PreferenceHelper.addToCurrentPollingCount(mContext);
        }
        //Turn on our service depending on whether it's needed or not
        if(pollingCount == 0) {
            AppMonitorService.setServiceAlarm(mContext, false);
        } else {
            AppMonitorService.setServiceAlarm(mContext, true);
        }
        return success;
    }

    public boolean update(SessionModel sessionModel) {
        boolean success = false;
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri selectorUri = Uri.parse(baseUri.toString() + "/"
                        + Long.toString(sessionModel.getActivityId())
                        + "/sessions/" + Long.toString(sessionModel.getId())
        );
        ContentResolver resolver = mContext.getContentResolver();
        int affectedRowCount = resolver.update(
                selectorUri,
                sessionModelToContentValues(sessionModel),
                null,
                null);
        if(affectedRowCount > 0) {
            success = true;
        }
        return success;
    }

    public boolean delete(ActivityModel activityToDelete) {
        boolean success = false;
        //First we need to remove all associated sessions, only if that succeeds should we continue.
        if(deleteAllActivitySessions(activityToDelete.getId())) {
            int startingCount = getCount();
            ContentResolver resolver = mContext.getContentResolver();
            Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
            Uri selectorUri = Uri.withAppendedPath(baseUri, Long.toString(activityToDelete.getId()));
            resolver.delete(
                    selectorUri,//Uri
                    null,   //Selector
                    null    //SelectorArgs
            );
            int finalCount = getCount();
            Log.d(TAG, "Deleted activity. New Count " + finalCount);
            success = startingCount > finalCount;
        }
        return success;
    }

    public boolean deleteAllActivitySessions(long activityId) {
        int startCount = getSessionCount(activityId);
        boolean success = false;
        if(startCount > 0) {
            Uri baseUri = ActivityProviderMetaData.SessionSummaryMetaData.CONTENT_URI;
            Uri selectorUri = Uri.parse(baseUri.toString() + "/"
                            + Long.toString(activityId)
                            + "/sessions"
            );
            ContentResolver resolver = mContext.getContentResolver();
            resolver.delete(
                    selectorUri,
                    null,
                    null
            );
            int finalCount = getSessionCount(activityId);
            success = startCount > finalCount;
        } else {
            success = true;
        }
        return success;
    }

    public int getCount() {
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Activity activity = (Activity)mContext;
        Cursor cursor = activity.getContentResolver().query(
                baseUri,//Uri
                null,       //Projection
                null,       //Selection
                null,       //SelectionArgs
                null);      //sortOrder
        int activityCount = cursor.getCount();
        cursor.close();
        return activityCount;
    }

    public int getSessionCount(long activityId) {
        Uri baseUri = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI;
        Uri selectorUri = Uri.parse(baseUri.toString() + "/"
                        + Long.toString(activityId)
                        + "/sessions"
        );
        Activity activity = (Activity)mContext;
        Cursor cursor = activity.getContentResolver().query(
                selectorUri,//Uri
                null,       //Projection
                null,       //Selection
                null,       //SelectionArgs
                null);      //sortOrder
        int sessionCount = cursor.getCount();
        cursor.close();
        return sessionCount;
    }

    private ContentValues activityModelToContentValues(ActivityModel currentActivity) {
        ContentValues values = new ContentValues();
        if(currentActivity.getId() != 0) {
            values.put(ActivityProviderMetaData.ActivityTableMetaData._ID, currentActivity.getId());
        }
        if(!TextUtils.isEmpty(currentActivity.getName())) {
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME,
                    currentActivity.getName());
        }
        if(!TextUtils.isEmpty(currentActivity.getClassName())) {
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS,
                    currentActivity.getClassName());
        }
        values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING,
                    currentActivity.getPolling());
        if(currentActivity.getIcon() != 0) {
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON,
                    currentActivity.getIcon());
        }
        if(currentActivity.getCreated() != 0) {
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED,
                    currentActivity.getCreated());
        }
        return values;
    }

    private class ActivityModelCursorHelper {
        public int iIdColumn;
        public int iNameColumn;
        public int iClassNameColumn;
        public int iIconColumn;
        public int iPollingColumn;
        public int iCreatedColumn;

        public ActivityModelCursorHelper(Cursor cursor) {
            iIdColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData._ID);
            iNameColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME);
            iClassNameColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS);
            iIconColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON);
            iPollingColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING);
            iCreatedColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED);
        }

        public ActivityModel createModelFromCursor(Cursor cursor) {
            return new ActivityModel(
                    cursor.getLong(iIdColumn),
                    cursor.getString(iNameColumn),
                    cursor.getString(iClassNameColumn),
                    cursor.getInt(iIconColumn),
                    cursor.getInt(iPollingColumn),
                    cursor.getLong(iCreatedColumn)
            );
        }
    }

    private ContentValues sessionModelToContentValues(SessionModel sessionModel) {
        ContentValues values = new ContentValues();
        if(sessionModel.getId() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData._ID, sessionModel.getId());
        }
        if(sessionModel.getActivityId() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID,
                    sessionModel.getActivityId());
        }
        if(sessionModel.getStart() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_START,
                    sessionModel.getStart());
        }
        if(sessionModel.getEnd() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_END,
                    sessionModel.getEnd());
        }
        if(sessionModel.getCreated() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED,
                    sessionModel.getCreated());
        }
        if(sessionModel.getModified() != 0) {
            values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED,
                    sessionModel.getModified());
        }
        return values;
    }

    private class SessionModelCursorHelper {
        public int iIdColumn;
        public int iActivityIdColumn;
        public int iStartColumn;
        public int iEndColumn;
        public int iCreatedColumn;
        public int iModifiedColumn;
        public SessionModelCursorHelper(Cursor cursor) {
            iIdColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData._ID);
            iActivityIdColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID);
            iStartColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData.SESSION_START);
            iEndColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData.SESSION_END);
            iCreatedColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED);
            iModifiedColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED);
        }
        public SessionModel createModelFromCursor(Cursor cursor) {
            return new SessionModel(
                    cursor.getLong(iIdColumn),
                    cursor.getLong(iActivityIdColumn),
                    cursor.getLong(iStartColumn),
                    cursor.getLong(iEndColumn),
                    cursor.getLong(iCreatedColumn),
                    cursor.getLong(iModifiedColumn)
            );
        }
    }

    private class SessionSummaryCursorHelper {
        public int iActivityIdColumn;
        public int iTotalLengthColumn;
        public int iAverageLengthColumn;
        public int iAverageCountColumn;
        public SessionSummaryCursorHelper(Cursor cursor) {
            iActivityIdColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID);
            iTotalLengthColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_TOTAL_LENGTH);
            iAverageLengthColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_LENGTH);
            iAverageCountColumn = cursor.getColumnIndex(
                    ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_COUNT);
        }
        public SessionSummaryModel createModelFromCursor(Cursor cursor) {
            return new SessionSummaryModel(
                    cursor.getInt(iActivityIdColumn),
                    cursor.getInt(iTotalLengthColumn),
                    cursor.getInt(iAverageLengthColumn),
                    cursor.getInt(iAverageCountColumn)
            );
        }
    }
}