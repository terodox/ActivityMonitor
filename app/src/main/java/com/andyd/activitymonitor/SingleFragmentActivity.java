package com.andyd.activitymonitor;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by AndrewA on 8/15/2014.
 */
public abstract class SingleFragmentActivity extends Activity {
    protected abstract Fragment createFragment();

    protected int getLayoutResId() {
        return R.layout.single_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if(fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }
}
