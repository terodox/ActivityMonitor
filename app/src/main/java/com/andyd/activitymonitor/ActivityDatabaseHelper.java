package com.andyd.activitymonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Setup/Create Database
 * This class helps to open, create and upgrade the database file
 */
public class ActivityDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ActivityDatabaseHelper";
    ActivityDatabaseHelper(Context context) {
        super(context,
                ActivityProviderMetaData.DATABASE_NAME,
                null,
                ActivityProviderMetaData.DATABASE_VERSION);
    }

    private static class SQLiteUpgrades {
        public static final String V3_ADD_ACTIVITY_ALERT_TIME_COLUMN =
                "ALTER TABLE " + ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME
                        + " ADD COLUMN "
                        + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ALERT_TIMEOUT
                        + " INTEGER DEFAULT 5";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "inner onCreate called");
        db.execSQL("CREATE TABLE " + ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME
                + " (" + ActivityProviderMetaData.ActivityTableMetaData._ID
                + " INTEGER PRIMARY KEY,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME + " TEXT,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS + " TEXT,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON + " INTEGER,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING + " INTEGER,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED + " INTEGER,"
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ALERT_TIMEOUT + " INTEGER"
                + ");");
        /**
         * This unique index is a safety.
         * This is enforced by a business rule in the ActivityController's insert method.
         */
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_UDX_CLASS + " ON "
                + ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME + " ("
                + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS
                + ")");

        db.execSQL("CREATE TABLE " + ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME
                + " (" + ActivityProviderMetaData.SessionTableMetaData._ID
                + " INTEGER PRIMARY KEY,"
                + ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID + " INTEGER,"
                + ActivityProviderMetaData.SessionTableMetaData.SESSION_START + " INTEGER,"
                + ActivityProviderMetaData.SessionTableMetaData.SESSION_END + " INTEGER,"
                + ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED + " INTEGER,"
                + ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        int upgradeTo = oldVersion + 1;
        //Why loop here?  Let's say someone skipped a few updates.  This loop will execute the
        // updates incrementally, allowing the db to come up to speed even over several versions
        // of difference
        while(upgradeTo <= newVersion) {
            Log.v(TAG, "Doing updates for version " + upgradeTo);
            switch(upgradeTo) {
                case 3:
                    db.execSQL(SQLiteUpgrades.V3_ADD_ACTIVITY_ALERT_TIME_COLUMN);
                    break;
            }
            upgradeTo++;
        }
        Log.v(TAG, "DATABASE UPDATE COMPLETE");
    }
}