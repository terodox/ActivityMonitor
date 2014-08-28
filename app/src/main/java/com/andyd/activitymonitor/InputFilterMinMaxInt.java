package com.andyd.activitymonitor;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Administrator on 8/28/2014.
 */
public class InputFilterMinMaxInt implements InputFilter {

    private int min, max;

    public InputFilterMinMaxInt(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMaxInt(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int min, int max, int input) {
        return max > min ? input >= min && input <= max : input >= max && input <= min;
    }
}
