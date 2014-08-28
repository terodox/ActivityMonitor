package com.andyd.activitymonitor;

/**
 * Created by AndrewA on 8/13/2014.
 * This class provides a model for Activities.
 */
public class ActivityModel {
    private long mId = 0;
    private String mName = "";
    private String mClassName = "";
    private int mIcon = 0;
    private int mPolling = 0;
    private long mCreated = 0;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClass(String className) {
        this.mClassName = className;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public int getPolling() {
        return mPolling;
    }

    public void setPolling(int mPolling) {
        this.mPolling = mPolling;
    }

    public long getCreated() {
        return mCreated;
    }

    public ActivityModel() {}

    public ActivityModel(String name, String className, int icon) {
        mName = name;
        mClassName = className;
        mIcon = icon;
    }
    public ActivityModel(long id, String name, String className, int icon, int polling, long created) {
        mId = id;
        mName = name;
        mClassName = className;
        mIcon = icon;
        mPolling = polling;
        mCreated = created;
    }

    @Override
    public String toString() {
        return mName;
    }
}
