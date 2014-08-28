package com.andyd.activitymonitor;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AndrewA on 8/13/2014.
 */
public class ActivityProviderMetaData {
    public static final String AUTHORITY =
            "com.andyd.ActivityMonitor.provider.ActivityProvider";

    public static final String DATABASE_NAME = "activity.db";
    public static final int DATABASE_VERSION = 2;

    private ActivityProviderMetaData() {}

    public static final class ActivityTableMetaData implements BaseColumns {
        private ActivityTableMetaData() {}

        public static final String TABLE_NAME = "activity";


        //URI and MIME Type definitions

        //URI and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/activities");
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.andyd.ActivityMonitor.activity";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.andyd.ActivityMonitor.activity";
        public static final String DEFAULT_SORT_ORDER = "name ASC";

        //Additional column definitions
        //TYPE: String
        public static final String ACTIVITY_NAME = "name";
        //TYPE: String
        public static final String ACTIVITY_CLASS = "class";
        //TYPE: int - Resource id for app icon
        public static final String ACTIVITY_ICON = "icon";
        //TYPE: int - 1 is active, 0 is inactive
        public static final String ACTIVITY_POLLING = "polling";
        //TYPE: int from System.currentTimeMillis()
        public static final String ACTIVITY_CREATED = "created";

        //Indexes!
        public static final String ACTIVITY_UDX_CLASS = "uidx_" + ACTIVITY_CLASS;
    }

    public static final class SessionTableMetaData implements BaseColumns {
        private SessionTableMetaData() {}

        public static final String TABLE_NAME = "session";


        //URI and MIME Type definitions

        //URI and MIME type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sessions");
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.andyd.ActivityMonitor.session";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.andyd.ActivityMonitor.session";
        public static final String DEFAULT_SORT_ORDER = "start DESC";

        //Additional column definitions
        //TYPE: int - id of associated activity
        public static final String ACTIVITY_ID = "activityId";
        //TYPE: int from System.currentTimeMillis()
        public static final String SESSION_START = "start";
        //TYPE: int from System.currentTimeMillis()
        public static final String SESSION_END = "end";
        //TYPE: int from System.currentTimeMillis()
        public static final String SESSION_CREATED = "created";
        //TYPE: int from System.currentTimeMillis()
        public static final String SESSION_MODIFIED = "modified";
    }

    public static final class SessionSummaryMetaData {
        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/sessionSummary");
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.andyd.ActivityMonitor.sessionSummary";

        //Columns definitions.  This will be an abstract table since the data is a summary and
        // not actual columns of a table.
        public static final String ACTIVITY_ID = SessionTableMetaData.ACTIVITY_ID;
        //TYPE: int - Total use length in millis.
        public static final String SESSION_SUMMARY_TOTAL_LENGTH = "totalLength";
        //TYPE: int - Average use length in millis.
        public static final String SESSION_SUMMARY_AVG_LENGTH = "averageLength";
        //TYPE: int - Average number of sessions
        public static final String SESSION_SUMMARY_AVG_COUNT = "averageCount";
    }
}