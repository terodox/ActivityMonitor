package com.andyd.activitymonitor;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by AndrewA on 8/15/2014.
 */
public class SessionSummaryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long activityId = getIntent().getLongExtra(
                SessionSummaryFragment.EXTRA_SESSION_SUMMARY_FRAGMENT_ACTIVITY_ID, 0);

        setContentView(R.layout.single_fragment);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SessionSummaryFragment.newInstance(activityId))
                    .commit();
        }
    }
}
