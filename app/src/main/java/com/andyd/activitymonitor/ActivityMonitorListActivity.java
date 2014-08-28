package com.andyd.activitymonitor;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;



public class ActivityMonitorListActivity extends Activity
        implements ActivityListFragment.Callbacks {
    public static final String TAG = "ActivityMonitorListActivity";

    public static final int REQUEST_INSTALLED_APP = 0;
    public static final int REQUEST_NOTIFICATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fragment);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ActivityListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_monitor_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean result = false;
        switch (id){
            case R.id.activity_monitor_list_menu_add:
                Log.d(TAG, "Add Menu Button Clicked!");
                Intent intent = new Intent(this, InstalledAppsList.class);
                startActivityForResult(intent, REQUEST_INSTALLED_APP);
                result = true;
                break;
            case R.id.activity_monitor_list_menu_settings:
                Log.d(TAG, "Settings Menu Button Clicked!");
                Intent preferenceIntent = new Intent(this, ActivityMonitorPreferenceActivity.class);
                startActivity(preferenceIntent);
                result = true;
                break;
        }
        if(!result) {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean resultHandled = false;
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_INSTALLED_APP) {
                String appName = data.getStringExtra(InstalledAppsList.EXTRA_INSTALLED_APP_NAME);
                String appClass = data.getStringExtra(InstalledAppsList.EXTRA_INSTALLED_APP_CLASS);
                int appIcon = data.getIntExtra(InstalledAppsList.EXTRA_INSTALLED_APP_ICON, 0);
                if(appName != null && appClass != null) {
                    //We've chosen an app to monitor.  Insert it to the db.
                    ActivityModel newActivity = new ActivityModel(appName, appClass, appIcon);
                    newActivity.setPolling(1); //Default the polling to be on
                    ActivityController.get(this).insert(newActivity);

                }
                resultHandled = true;
            }
        }
        if(!resultHandled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onActivitySelected(ActivityModel activityModel) {
        Intent intent = new Intent(this, SessionSummaryActivity.class);
        intent.putExtra(
                SessionSummaryFragment.EXTRA_SESSION_SUMMARY_FRAGMENT_ACTIVITY_ID,
                activityModel.getId());
        startActivity(intent);
    }

    /**
     * Set aside for now - future implementation of Master detail will need this.

    @Override
    public void onActivityUpdated(ActivityModel activityModel) {
        FragmentManager fragmentManager = getFragmentManager();
        ActivityListFragment listFragment =
                (ActivityListFragment)fragmentManager.findFragmentById(R.id.container);
        listFragment.updateUI();
    }
    */
}
