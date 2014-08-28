package com.andyd.activitymonitor;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class SessionSummaryModel {
    private static final String TAG = "SessionSummaryModel";

    private long mActivityId;
    private long mTotalLength;
    private long mAverageSessionLength;
    private int mAverageSessionCount;

    public long getActivityId() {
        return mActivityId;
    }

    public void setActivityId(long mActivityId) {
        this.mActivityId = mActivityId;
    }

    public long getAverageSessionLength() {
        return mAverageSessionLength;
    }

    public long getTotalLength() {
        return mTotalLength;
    }

    public void setTotalLength(long mTotalLength) {
        this.mTotalLength = mTotalLength;
    }

    public void setAverageSessionLength(int mAverageSessionLength) {
        this.mAverageSessionLength = mAverageSessionLength;
    }

    public int getAverageSessionCount() {
        return mAverageSessionCount;
    }

    public void setAverageSessionCount(int mAverageSessionCount) {
        this.mAverageSessionCount = mAverageSessionCount;
    }

    public SessionSummaryModel() {}
    public SessionSummaryModel(long activityId, int totalLength,
                               int averageSessionLength, int averageSessionCount) {
        mActivityId = activityId;
        mTotalLength = totalLength;
        mAverageSessionLength = averageSessionLength;
        mAverageSessionCount = averageSessionCount;
    }
}
