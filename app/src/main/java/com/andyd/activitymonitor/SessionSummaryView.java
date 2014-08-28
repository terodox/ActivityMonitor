package com.andyd.activitymonitor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by AndrewA on 8/21/2014.
 */
public class SessionSummaryView extends TableLayout {
    private TextView mSessionSummaryHeader;
    private TextView mSessionLengthName;
    private TextView mSessionLengthValue;
    private TextView mSessionAverageName;
    private TextView mSessionAverageValue;
    private TextView mSessionCountName;
    private TextView mSessionCountValue;

    public SessionSummaryView (Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        //Adjust any parameters of the our root element.
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if(layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        setLayoutParams(layoutParams);

        //Inflate our desired layout and append it to us.
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.session_summary_view, this, true);

        //Get all of our view components
        mSessionSummaryHeader =
                (TextView)view.findViewById(R.id.session_summary_view_timingHeader);
        mSessionLengthName =
                (TextView)view.findViewById(R.id.session_summary_view_summaryLengthName);
        mSessionLengthValue =
                (TextView)view.findViewById(R.id.session_summary_view_summaryLengthValue);
        mSessionAverageName =
                (TextView)view.findViewById(R.id.session_summary_view_summaryAverageName);
        mSessionAverageValue =
                (TextView)view.findViewById(R.id.session_summary_view_summaryAverageValue);
        mSessionCountName =
                (TextView)view.findViewById(R.id.session_summary_view_sessionCountName);
        mSessionCountValue =
                (TextView)view.findViewById(R.id.session_summary_view_sessionCountValue);

        //Lookup all possible strings that could have been passed in from the xml ui
        TypedArray styledInformation =
                context.obtainStyledAttributes(attributeSet, R.styleable.SessionSummaryView);
        String sessionSummaryHeader =
                styledInformation.getString(R.styleable.SessionSummaryView_summaryHeader);
        String sessionLengthName =
                styledInformation.getString(R.styleable.SessionSummaryView_summaryLengthName);
        String sessionAverageName =
                styledInformation.getString(R.styleable.SessionSummaryView_summaryAverageName);
        String sessionCountName =
                styledInformation.getString(R.styleable.SessionSummaryView_sessionCountName);
        int backgroundColor =
                styledInformation.getColor(R.styleable.SessionSummaryView_backgroundColor,
                        android.R.color.background_light);

        //If the data was set, then apply it to the views
        if(!TextUtils.isEmpty(sessionSummaryHeader)) {
            mSessionSummaryHeader.setText(sessionSummaryHeader);
        }
        if(!TextUtils.isEmpty(sessionLengthName)) {
            mSessionLengthName.setText(sessionLengthName);
        }
        if(!TextUtils.isEmpty(sessionAverageName)) {
            mSessionAverageName.setText(sessionAverageName);
        }
        if(!TextUtils.isEmpty(sessionCountName)) {
            mSessionCountName.setText(sessionCountName);
        }
        setBackgroundColor(backgroundColor);

    }

    public SessionSummaryView(Context context) {
        this(context, null);
    }

    public void setUpSummary(SessionSummaryModel sessionSummary) {
        if(sessionSummary != null) {
            long totalTiming = sessionSummary.getTotalLength();
            String totalTimingString = NumberUtils.intToHourMinSec(totalTiming);
            if(TextUtils.isEmpty(totalTimingString)) {
                totalTimingString = NumberUtils.intToSec(totalTiming);
            }
            long averageTiming = sessionSummary.getAverageSessionLength();
            String averageTimingString = NumberUtils.intToHourMinSec(averageTiming);
            if(TextUtils.isEmpty(averageTimingString)) {
                averageTimingString = NumberUtils.intToSec(averageTiming);
            }
            mSessionLengthValue.setText(totalTimingString);
            mSessionAverageValue.setText(averageTimingString);
            mSessionCountValue.setText(Long.toString(sessionSummary.getAverageSessionCount()));
        } else {
            mSessionLengthValue.setText("0 sec");
            mSessionAverageValue.setText("0 sec");
            mSessionCountValue.setText("0");
        }
    }
}
