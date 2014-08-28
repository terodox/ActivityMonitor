package com.andyd.activitymonitor;

import android.text.TextUtils;

/**
 * Created by AndrewA on 8/13/2014.
 */
public class NumberUtils {
    public static boolean IsNumeric(String value) {
        try {
            Double.parseDouble(value);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public static String intToHourMinSec(long timing) {
        String finalString = "";
        finalString = intToHourMin(timing);
        //Subtract any possible hours.
        timing -= (int)Math.floor(timing / (60 * 60 * 1000)) * (60 * 60 * 1000);
        //Subtract any possible minutes
        timing -= (int)Math.floor(timing / (60 * 1000)) * (60 * 1000);
        finalString += intToSec(timing);
        return finalString;
    }
    public static String intToHourMin(long timing) {
        String finalString = "";
        if(timing - (60 * 60 * 1000) > 0) {
            //We have been running for more than an hour. Calculate hours:
            int hours = (int)Math.floor(timing / (60 * 60 * 1000));
            finalString += Integer.toString(hours) + " Hr ";
            timing -= (60 * 60 * 1000) * hours;
        }
        if(timing - (60 * 1000) > 0) {
            //We have been running for more than a minute. Calculate minutes:
            int minutes = (int)Math.floor(timing / (60 * 1000));
            finalString += Integer.toString(minutes) + " Min ";
            timing -= (60 * 1000) * minutes;
        } else if(!TextUtils.isEmpty(finalString)) {
            finalString += "0 Min";
        }
        return finalString;
    }
    public static String intToSec(long timing) {
        String finalString = "";
        if(timing - 1000 > 0) {
            //We have been running for more than a second. Calculate seconds:
            int seconds = (int)Math.floor(timing / 1000);
            finalString += Integer.toString(seconds) + " Sec ";
        } else if(!TextUtils.isEmpty(finalString)) {
            finalString += "0 Sec ";
        }
        return finalString;
    }
}

