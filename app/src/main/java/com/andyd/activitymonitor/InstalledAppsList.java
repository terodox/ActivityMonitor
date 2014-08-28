package com.andyd.activitymonitor;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class InstalledAppsList extends ListActivity {
    private static final String TAG = "AppSelectorFragment";

    //Extras
    public static final String EXTRA_INSTALLED_APP_NAME =
            "com.andrewdesmarais.applications.appmonitor.extra_installed_app_name";
    public static final String EXTRA_INSTALLED_APP_CLASS =
            "com.andrewdesmarais.applications.appmonitor.extra_installed_app_class";
    public static final String EXTRA_INSTALLED_APP_ICON =
            "com.andrewdesmarais.applications.appmonitor.extra_installed_app_icon";

    private ResolveInfo mAppPicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + apps.size() + " options");

        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                PackageManager comparePackageManager = getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.loadLabel(comparePackageManager).toString(),
                        rhs.loadLabel(comparePackageManager).toString()
                );
            }
        });

        InstalledAppsAdapter adapter = new InstalledAppsAdapter(this, apps);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mAppPicked = ((InstalledAppsAdapter)getListAdapter()).getItem(position);
        returnResult();
        super.onListItemClick(l, v, position, id);
    }

    public void returnResult() {
        PackageManager packageManager = getPackageManager();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_INSTALLED_APP_NAME, mAppPicked.loadLabel(packageManager));
        resultIntent.putExtra(EXTRA_INSTALLED_APP_CLASS,
                mAppPicked.activityInfo.packageName.toString());
        resultIntent.putExtra(EXTRA_INSTALLED_APP_ICON,
                mAppPicked.activityInfo.applicationInfo.icon);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
