package com.andyd.activitymonitor;

import java.util.Calendar;

/**
 * Created by Administrator on 8/28/2014.
 */
public class TimeUtils {
    public static final long oneDayMilliseconds = 24 * 60 * 60 * 1000;
    public static final long sevenDaysMilliseconds = 7 * oneDayMilliseconds;
    public static final long thirtyDaysMilliseconds = 30 * oneDayMilliseconds;

    public static long MillisecondsSinceMidnight() {
        Calendar rightNow = Calendar.getInstance();

        // Offset to add since we're not UTC
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long sinceMidnight = (rightNow.getTimeInMillis() + offset) % oneDayMilliseconds;

        return sinceMidnight;
    }
}
