package com.andyd.activitymonitor;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class SessionModel {
    private static final String TAG = "SessionModel";

    private long mId = 0;
    private long mActivityId = 0;
    private long mStart = 0;
    private long mEnd = 0;
    private long mCreated = 0;
    private long mModified = 0;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public long getActivityId() {
        return mActivityId;
    }

    public void setActivityId(long mActivityId) {
        this.mActivityId = mActivityId;
    }

    public long getStart() {
        return mStart;
    }

    public void setStart(long mStart) {
        this.mStart = mStart;
    }

    public long getEnd() {
        return mEnd;
    }

    public void setEnd(long mEnd) {
        this.mEnd = mEnd;
    }

    public long getCreated() {
        return mCreated;
    }

    public long getModified() {
        return mModified;
    }

    public SessionModel() {}
    public SessionModel(long id, long activityId, long start) {
        mId = id;
        mActivityId = activityId;
        mStart = start;
    }
    public SessionModel(long id, long activityId, long start, long end, long created, long modified) {
        mId = id;
        mActivityId = activityId;
        mStart = start;
        mEnd = end;
        mCreated = created;
        mModified = modified;
    }

    public long getSessionLength() {
        return mEnd - mStart;
    }
}
