package com.andyd.activitymonitor;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class SessionSummaryFragment extends Fragment {
    private static final String TAG = "SessionSummaryFragment";

    public static final String EXTRA_SESSION_SUMMARY_FRAGMENT_ACTIVITY_ID =
            "com.andyd.activitymonitor.sessionSummaryFragment.activityId";

    //Objects that are required to make this fragment function;
    private ActivityModel mCurrentActivity;
    private SessionSummaryModel mOverallSessionSummary;
    private SessionSummaryModel mLast24HrsSessionSummary;
    private SessionSummaryModel mLastWeekSessionSummary;
    private SessionSummaryModel mLast30DaysSessionSummary;

    //Items of use within the view
    ImageView mActivityIcon;
    TextView mActivityName;
    TextView mActivityClass;
    SessionSummaryView mSessionOverall;
    SessionSummaryView mSessionLast24Hrs;
    SessionSummaryView mSessionLastWeek;
    SessionSummaryView mSessionLast30Days;
    ToggleButton mCurrentlyPollingButton;

    //Setup our onclick listener
    private View.OnClickListener TogglePolling = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToggleButton pollingButton = (ToggleButton)v;
            if(pollingButton.isChecked()) {
                Log.d(TAG, "Enabling polling...");
                mCurrentActivity.setPolling(1);
            } else {
                Log.d(TAG, "Disabling polling...");
                mCurrentActivity.setPolling(0);
            }
            ActivityController.get(getActivity()).update(mCurrentActivity);
        }
    };

    public static SessionSummaryFragment newInstance(long activityId) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_SESSION_SUMMARY_FRAGMENT_ACTIVITY_ID, activityId);

        SessionSummaryFragment fragment = new SessionSummaryFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController activityController = ActivityController.get(getActivity());

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if(args != null) {
            long activityId = args.getLong(EXTRA_SESSION_SUMMARY_FRAGMENT_ACTIVITY_ID, 0);
            mCurrentActivity = activityController.getActivityById(activityId);
            mOverallSessionSummary = activityController.getActivitySessionSummary(activityId);
            long oneDay =          24 * 60 * 60 * 1000;
            long sevenDays =   7 * oneDay;
            long thirtyDays = 30 * oneDay;

            mLast24HrsSessionSummary = activityController
                    .getActivitySessionSummarySince(activityId,
                    System.currentTimeMillis() - oneDay);
            mLastWeekSessionSummary = activityController
                    .getActivitySessionSummarySince(activityId,
                    System.currentTimeMillis() - sevenDays);
            mLast30DaysSessionSummary = activityController
                    .getActivitySessionSummarySince(activityId,
                    System.currentTimeMillis() - thirtyDays);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_summary, container, false);

        //Get all the necessary items from the view.
        mActivityIcon = (ImageView)view.findViewById(R.id.fragment_session_summary_icon);
        mActivityName = (TextView)view.findViewById(R.id.fragment_session_summary_name);
        mActivityClass = (TextView)view.findViewById(R.id.fragment_session_summary_className);
        mSessionOverall =
                (SessionSummaryView)view.findViewById(R.id.fragment_session_summary_overall);
        mSessionLast24Hrs =
                (SessionSummaryView)view.findViewById(R.id.fragment_session_summary_last24Hrs);
        mSessionLastWeek =
                (SessionSummaryView)view.findViewById(R.id.fragment_session_summary_lastWeek);
        mSessionLast30Days =
                (SessionSummaryView)view.findViewById(R.id.fragment_session_summary_last30Days);
        mCurrentlyPollingButton = (ToggleButton)view.findViewById(
                R.id.fragment_session_summary_currentlyPolling);

        if(mCurrentActivity != null && mCurrentActivity.getId() != 0) {
            try {
                mActivityIcon.setImageDrawable(
                        getActivity()
                                .getPackageManager()
                                .getApplicationIcon(mCurrentActivity.getClassName())
                );
            } catch (PackageManager.NameNotFoundException nnf) {
                //Don't do anything for now
                //TODO: have this populate the icon with a missing icon image
            }
            mActivityName.setText(mCurrentActivity.getName());
            mActivityClass.setText(mCurrentActivity.getClassName());
            mCurrentlyPollingButton.setChecked(mCurrentActivity.getPolling() == 1);
            mCurrentlyPollingButton.setOnClickListener(TogglePolling);

            //Setup all the custom views
            mSessionOverall.setUpSummary(mOverallSessionSummary);
            mSessionLast24Hrs.setUpSummary(mLast24HrsSessionSummary);
            mSessionLastWeek.setUpSummary(mLastWeekSessionSummary);
            mSessionLast30Days.setUpSummary(mLast30DaysSessionSummary);
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getActivity(), ActivityMonitorListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }
}
