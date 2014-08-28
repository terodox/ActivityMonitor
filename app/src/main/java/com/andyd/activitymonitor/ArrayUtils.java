package com.andyd.activitymonitor;

import java.io.InvalidClassException;
import java.util.Arrays;

/**
 * Created by AndrewA on 8/21/2014.
 */
public class ArrayUtils {
    public static String[] arrayAppend(String[] array1, String[] array2) {
        String[] finalArray = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, finalArray, 0, array1.length);
        System.arraycopy(array2, 0, finalArray, array1.length, array2.length);
        return finalArray;
    }
}
