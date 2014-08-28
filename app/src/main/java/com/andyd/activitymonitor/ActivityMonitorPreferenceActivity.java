package com.andyd.activitymonitor;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by AndrewA on 8/21/2014.
 */
public class ActivityMonitorPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ActivityMonitorPreferenceFragment())
                .commit();
    }

    /**
     * Fragment for displaying our preferences
     */
    public static class ActivityMonitorPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Setup the defaults appropriately
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.activity_monitor_list_activity_preferences, false);

            //Load the preferences from our xml
            addPreferencesFromResource(R.xml.activity_monitor_list_activity_preferences);
        }
    }
}
