package com.andyd.activitymonitor;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by AndrewA on 8/13/2014.
 */
public class ActivityProvider extends ContentProvider {
    public static final String TAG = "ActivityProvider";

    //Setup the projection for activities and sessions
    public static HashMap<String, String> sActivitiesProjection;
    public static HashMap<String, String> sSessionsProjection;
    public static HashMap<String, String> sSessionSummaryProjection;
    static {
        sActivitiesProjection = new HashMap<String, String>();
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData._ID,
                ActivityProviderMetaData.ActivityTableMetaData._ID);
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME);
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS);
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON);
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING);
        sActivitiesProjection.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED);

        sSessionsProjection = new HashMap<String, String>();
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData._ID,
                ActivityProviderMetaData.SessionTableMetaData._ID);
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID,
                ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID);
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_START,
                ActivityProviderMetaData.SessionTableMetaData.SESSION_START);
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_END,
                ActivityProviderMetaData.SessionTableMetaData.SESSION_END);
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED,
                ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED);
        sSessionsProjection.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED,
                ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED);

        sSessionSummaryProjection = new HashMap<String, String>();
        sSessionSummaryProjection.put(ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID,
                ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID);
        sSessionSummaryProjection.put(
                ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_LENGTH,
                ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_LENGTH);
        sSessionSummaryProjection.put(
                ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_COUNT,
                ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_COUNT);
    }



    /**
     * Setup URIs
     * Provide a mechanism to identify all the incoming uri patterns
     */
    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_ACTIVITY_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_ACTIVITY_URI_INDICATOR = 2;
    private static final int INCOMING_SESSION_COLLECTION_URI_INDICATOR = 3;
    private static final int INCOMING_SINGLE_SESSION_URI_INDICATOR = 4;
    private static final int INCOMING_SESSION_LATEST_URI_INDICATOR = 6;
    private static final int INCOMING_SESSION_SUMMARY_URI_INDICATOR = 5;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities",
                INCOMING_ACTIVITY_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities/#",
                INCOMING_SINGLE_ACTIVITY_URI_INDICATOR);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities/#/sessions",
                INCOMING_SESSION_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities/#/sessions/#",
                INCOMING_SINGLE_SESSION_URI_INDICATOR);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities/#/sessions/latest",
                INCOMING_SESSION_LATEST_URI_INDICATOR);
        sUriMatcher.addURI(ActivityProviderMetaData.AUTHORITY, "activities/#/sessionSummary",
                INCOMING_SESSION_SUMMARY_URI_INDICATOR);
    }

    /**
     * Setup/Create Database
     * This class helps to open, create and upgrade the database file
     */
    public static class ActivityDatabaseHelper extends SQLiteOpenHelper {
        ActivityDatabaseHelper(Context context) {
            super(context,
                    ActivityProviderMetaData.DATABASE_NAME,
                    null,
                    ActivityProviderMetaData.DATABASE_VERSION);
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
                    + ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED + " INTEGER"
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
            Log.d(TAG, "inner onUpgrade called");
            Log.w(TAG, "Upgrading database from " + oldVersion + " to " + newVersion
                    + " which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " +
                    ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +
                    ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME);
            onCreate(db);
        }
    }

    private ActivityDatabaseHelper mOpenHelper;

    //Now all of the guts for the actual content provider

    @Override
    public boolean onCreate() {
        Log.d(TAG, "main onCreate called");
        mOpenHelper = new ActivityDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String orderBy = "";
        //Every URI case statement must have the handler for if the sortOrder passed in is empty.
        if(!TextUtils.isEmpty(sortOrder)) {
            orderBy = sortOrder;
        }
        boolean usingQueryBuilder = true;
        String rawQuery = "";
        String[] rawQueryParams = null;
        String limitRowCount = null;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_ACTIVITY_COLLECTION_URI_INDICATOR:
                queryBuilder.setTables(ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sActivitiesProjection);
                if(TextUtils.isEmpty(sortOrder)) {
                    orderBy = ActivityProviderMetaData.ActivityTableMetaData.DEFAULT_SORT_ORDER;
                }
                break;
            case INCOMING_SINGLE_ACTIVITY_URI_INDICATOR:
                queryBuilder.setTables(ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sActivitiesProjection);
                queryBuilder.appendWhere(ActivityProviderMetaData.ActivityTableMetaData._ID
                        + " = " + uri.getPathSegments().get(1));
                if(TextUtils.isEmpty(sortOrder)) {
                    orderBy = ActivityProviderMetaData.ActivityTableMetaData.DEFAULT_SORT_ORDER;
                }
                break;
            case INCOMING_SESSION_COLLECTION_URI_INDICATOR:
                queryBuilder.setTables(ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sSessionsProjection);
                queryBuilder.appendWhere(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                        + " = " + uri.getPathSegments().get(1));
                if(TextUtils.isEmpty(sortOrder)) {
                    orderBy = ActivityProviderMetaData.SessionTableMetaData.DEFAULT_SORT_ORDER;
                }
                break;
            case INCOMING_SINGLE_SESSION_URI_INDICATOR:
                queryBuilder.setTables(ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sSessionsProjection);
                queryBuilder.appendWhere(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                        + " = " + uri.getPathSegments().get(1)
                        + " AND " + ActivityProviderMetaData.SessionTableMetaData._ID
                        + " = " + uri.getPathSegments().get(3));
                if(TextUtils.isEmpty(sortOrder)) {
                    orderBy = ActivityProviderMetaData.SessionTableMetaData.DEFAULT_SORT_ORDER;
                }
                break;
            case INCOMING_SESSION_LATEST_URI_INDICATOR:
                queryBuilder.setTables(ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sSessionsProjection);
                queryBuilder.appendWhere(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                        + " = " + uri.getPathSegments().get(1));
                //This is the critical difference for this request.  We only want the latest one.
                limitRowCount = Integer.toString(1);
                //We're going to ignore any passed sortOrder and force it to be by start.
                orderBy = ActivityProviderMetaData.SessionTableMetaData.SESSION_START + " DESC";
                break;
            case INCOMING_SESSION_SUMMARY_URI_INDICATOR:
                usingQueryBuilder = false;
                rawQuery = "SELECT COALESCE("
                        + ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID
                        + ", 0) AS " + ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID
                        + ", COALESCE(SUM(" + ActivityProviderMetaData.SessionTableMetaData.SESSION_END
                        + " - " + ActivityProviderMetaData.SessionTableMetaData.SESSION_START
                        + "), 0) AS "
                        + ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_TOTAL_LENGTH
                        + ", COALESCE(AVG(" + ActivityProviderMetaData.SessionTableMetaData.SESSION_END
                        + " - " + ActivityProviderMetaData.SessionTableMetaData.SESSION_START
                        + "), 0) AS "
                        + ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_LENGTH
                        + ", COALESCE(COUNT(" + ActivityProviderMetaData.SessionTableMetaData._ID
                        + "), 0) AS "
                        + ActivityProviderMetaData.SessionSummaryMetaData.SESSION_SUMMARY_AVG_COUNT
                        + " FROM " + ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME
                        + " WHERE "
                        + ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID + "=? AND "
                        + ActivityProviderMetaData.SessionTableMetaData.SESSION_END + " <> 0";
                if(!TextUtils.isEmpty(selection)) {
                    rawQuery += " AND (" + selection + ") ";
                }
                rawQuery +=
                        " GROUP BY " + ActivityProviderMetaData.SessionSummaryMetaData.ACTIVITY_ID
                        + "";

                Log.d(TAG, rawQuery);
                if(selectionArgs != null) {
                    for (String oneArg : selectionArgs) {
                        Log.d(TAG, oneArg);
                    }
                }
                Log.d(TAG, Long.toString(System.currentTimeMillis()));


                /**
                 * The above query should look something like:
                 * SELECT activityId,
                 *      COALESCE(SUM(end - start), 0) AS totalLength,
                 *      COALESCE(AVG(end - start), 0) AS averageLength,
                 *      COALESCE(COUNT(id), 0) AS averageCount
                 *      --TODO: Yes, this is wrong and needs to be refactored
                 * FROM session
                 * WHERE activityId = ?
                 * GROUP BY activityId
                 */
                rawQueryParams = new String[] {
                        uri.getPathSegments().get(1) //Activity ID from uri
                };
                if(selectionArgs != null && selectionArgs.length > 0) {
                    //Append all the additional selectionArgs to the rawQueryParams array
                    rawQueryParams = ArrayUtils.arrayAppend(rawQueryParams, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }

        //We now have the query built.  Get the db and run it!
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        if(usingQueryBuilder) {
            cursor = queryBuilder.query(database, projection, selection, selectionArgs, null,
                    null, orderBy, limitRowCount);
        } else {
            cursor = database.rawQuery(rawQuery, rawQueryParams);
        }

        //Tell the cursor which uri to watch, so it knows when its source data changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_ACTIVITY_COLLECTION_URI_INDICATOR:
                mimeType = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_TYPE;
                break;
            case INCOMING_SINGLE_ACTIVITY_URI_INDICATOR:
                mimeType = ActivityProviderMetaData.ActivityTableMetaData.CONTENT_ITEM_TYPE;
                break;
            case INCOMING_SESSION_COLLECTION_URI_INDICATOR:
                mimeType = ActivityProviderMetaData.SessionTableMetaData.CONTENT_TYPE;
                break;
            case INCOMING_SINGLE_SESSION_URI_INDICATOR:
                mimeType = ActivityProviderMetaData.SessionTableMetaData.CONTENT_ITEM_TYPE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri = null;
        ContentValues insertValues;

        //Handle the case where we aren't passed any values.
        //This will allow for easier data validation in the insert methods.
        if(values != null) {
            insertValues = new ContentValues(values);
        } else {
            insertValues = new ContentValues();
        }

        /**
         * I've chosen to break out these two inserts because of the logic involved with processing
         * their inserts.  The other function (query, update, delete) are all much more straight
         * forward.
         */
        switch (sUriMatcher.match(uri)) {
            case INCOMING_ACTIVITY_COLLECTION_URI_INDICATOR:
                insertedUri = insertActivity(values);
                break;
            case INCOMING_SESSION_COLLECTION_URI_INDICATOR:
                String activityId = uri.getPathSegments().get(1);
                insertedUri = insertSession(activityId, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI for insert " + uri);
        }

        if(insertedUri != null) {
            getContext().getContentResolver().notifyChange(insertedUri, null);
        } else {
            throw new SecurityException("Failed to insert row into " + uri);
        }
        return insertedUri;
    }

    private Uri insertActivity(ContentValues values) {
        Uri insertedUri = null;

        //Verify we have all the necessary valid data
        if(values.containsKey(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CLASS)
                == false) {
            //This column cannot be empty!  Throw an error.
            throw new IllegalArgumentException("Inserting an activity requires a class!");
        }
        if(values.containsKey(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME)
                == false) {
            //This column cannot be empty!  Throw an error.
            throw new IllegalArgumentException("Inserting an activity requires a name!");
        }
        if(values.containsKey(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON)
                == false) {
            //We will default this column to a 0 meaning no icon.
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_ICON, 0);
        }
        if(values.containsKey(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING)
                == false) {
            //We will default this column to a 1 meaning it will be polling by default.
            values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_POLLING, 1);
        }
        //Setup the created date
        values.put(ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_CREATED,
                System.currentTimeMillis());

        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        long rowId = database.insert(ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME,
                ActivityProviderMetaData.ActivityTableMetaData.ACTIVITY_NAME, values);
        if(rowId > 0) {
            //Successfully inserted row!
            insertedUri = ContentUris.withAppendedId(
                    ActivityProviderMetaData.ActivityTableMetaData.CONTENT_URI, rowId);
        }
        return insertedUri;
    }

    private Uri insertSession(String activityId, ContentValues values) {
        Uri insertedUri = null;

        //This insert requires no data from the outside world.
        //We will, however, verify that our activityId is numeric.
        if(!NumberUtils.IsNumeric(activityId)) {
            //If it isn't numeric, throw an error!
            throw new IllegalArgumentException("Activity ID in the uri must be numeric! Received: "
                    + activityId);
        }

        //Everything else is defaulted.
        values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_START,
                System.currentTimeMillis());
        values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_END, 0);
        values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_CREATED,
                System.currentTimeMillis());
        values.put(ActivityProviderMetaData.SessionTableMetaData.SESSION_MODIFIED,
                System.currentTimeMillis());
        values.put(ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID,
                Long.valueOf(activityId));

        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        long rowId = database.insert(ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME,
                ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID, values);
        if(rowId > 0) {
            //Successfully inserted row!
            insertedUri = ContentUris.withAppendedId(
                    ActivityProviderMetaData.SessionTableMetaData.CONTENT_URI, rowId);
        }
        return insertedUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int count = 0;
        if(TextUtils.isEmpty(selection)) {
            selection = "";
        } else {
            selection = " AND (" + selection + ")";
        }
        //TODO: Add a validator for the ids being passed to be numeric
        switch (sUriMatcher.match(uri)) {
            case INCOMING_SINGLE_ACTIVITY_URI_INDICATOR:
                count = database.update(
                        ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME,
                        values,
                        ActivityProviderMetaData.ActivityTableMetaData._ID
                                + "=" + uri.getPathSegments().get(1)
                                + selection,
                        selectionArgs);
                break;
            case INCOMING_SINGLE_SESSION_URI_INDICATOR:
                count = database.update(
                        ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME,
                        values,
                        ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                                + "=" + uri.getPathSegments().get(1)
                                + " AND "
                                + ActivityProviderMetaData.SessionTableMetaData._ID
                                + "=" + uri.getPathSegments().get(3)
                                + selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid uri for update " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int count = 0;
        if(TextUtils.isEmpty(selection)) {
            selection = "";
        } else {
            selection = " AND (" + selection + ")";
        }
        //TODO: Add a validator for the ids being passed to be numeric
        switch (sUriMatcher.match(uri)) {
            case INCOMING_SINGLE_ACTIVITY_URI_INDICATOR:
                count = database.delete(
                        ActivityProviderMetaData.ActivityTableMetaData.TABLE_NAME,
                        ActivityProviderMetaData.ActivityTableMetaData._ID
                                + "=" + uri.getPathSegments().get(1)
                                + selection,
                        selectionArgs
                );
                break;
            case INCOMING_SINGLE_SESSION_URI_INDICATOR:
                count = database.delete(
                        ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME,
                        ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                                + "=" + uri.getPathSegments().get(1)
                                + " AND "
                                + ActivityProviderMetaData.SessionTableMetaData._ID
                                + "=" + uri.getPathSegments().get(3)
                                + selection,
                        selectionArgs
                );
                break;
            case INCOMING_SESSION_COLLECTION_URI_INDICATOR:
                count = database.delete(
                        ActivityProviderMetaData.SessionTableMetaData.TABLE_NAME,
                        ActivityProviderMetaData.SessionTableMetaData.ACTIVITY_ID
                                + "=" + uri.getPathSegments().get(1)
                                + selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid uri for update " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}