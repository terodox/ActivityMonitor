package com.andyd.activitymonitor;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class ActivityModelAdapter extends BaseAdapter {
    private static final String TAG = "ActivityModelAdapter";

    private ArrayList<ActivityModel> mActivities;
    private Activity mActivity;
    private static LayoutInflater sInflater = null;

    public ActivityModelAdapter(Activity activity) {
        mActivity = activity;
        mActivities = ActivityController.get(mActivity).getActivities();
        sInflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        //This must always be a value greater than zero to avoid an app ending crash
        return (mActivities.size() > 0)? mActivities.size() : 1;
    }

    @Override
    public Object getItem(int position) {
        if(mActivities.size() > 0) {
            return mActivities.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(mActivities.size() > 0) {
            return mActivities.get(position).getId();
        } else {
            return 0;
        }
    }

    private static class ViewParts {
        public ImageView icon;
        public TextView headerText;
        public TextView subText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewParts viewParts;

        if(convertView == null) {
            //We don't have a view yet.  Inflate one to use.
            view = sInflater.inflate(R.layout.listfragment_activity_list_item, null);
            viewParts = new ViewParts();
            viewParts.icon =
                    (ImageView)view.findViewById(R.id.listfragment_activity_list_item_icon);
            viewParts.headerText =
                    (TextView)view.findViewById(R.id.listfragment_activity_list_item_name);
            viewParts.subText =
                    (TextView)view.findViewById(R.id.listfragment_activity_list_item_className);
            //This will allow us to reuse the view parts if we switch back to this view
            view.setTag(viewParts);
        } else {
            viewParts = (ViewParts)view.getTag();
        }

        //Setup our empty list item
        if(mActivities.size() == 0) {
            viewParts.icon.setVisibility(View.GONE);
            viewParts.headerText.setText("Click the plus to monitor an app");
            viewParts.subText.setText("");
        } else {
            viewParts.icon.setVisibility(View.VISIBLE);
            ActivityModel currentActivity = mActivities.get(position);
            Drawable icon = null;
            try {
                icon =
                        mActivity.getPackageManager().getApplicationIcon(currentActivity.getClassName());
            } catch(PackageManager.NameNotFoundException nnf) {
                //Silently cry in the weeds
            }
            if(icon != null) {
                viewParts.icon.setImageDrawable(icon);
            }
            viewParts.headerText.setText(currentActivity.getName());
            viewParts.subText.setText(currentActivity.getClassName());

            //TODO: setup the onclick listener
        }

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        mActivities = ActivityController.get(mActivity).getActivities();
        super.notifyDataSetChanged();

    }
}
