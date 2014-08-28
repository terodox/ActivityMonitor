package com.andyd.activitymonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by AndrewA on 7/30/2014.
 */
public class InstalledAppsAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<ResolveInfo> mData;
    private static LayoutInflater mInflater = null;

    public InstalledAppsAdapter(Activity activity, List<ResolveInfo> data) {
        mActivity = activity;
        mData = data;
        mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mData.size();
    }

    public ResolveInfo getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = mInflater.inflate(R.layout.app_selector_list_item, null);

        TextView appName = (TextView)view.findViewById(R.id.app_selector_list_item_appName); // AppName
        ImageView appIcon =(ImageView)view
                .findViewById(R.id.app_selector_list_item_thumbnail); // App Icon

        // Setting all values in listview
        PackageManager packageManager = mActivity.getPackageManager();
        ResolveInfo resolveInfo = getItem(position);
        appName.setText(resolveInfo.loadLabel(packageManager).toString());
        appIcon.setImageDrawable(resolveInfo.loadIcon(packageManager));

        return view;
    }
}
