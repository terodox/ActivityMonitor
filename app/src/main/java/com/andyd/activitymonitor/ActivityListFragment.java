package com.andyd.activitymonitor;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by AndrewA on 8/14/2014.
 */
public class ActivityListFragment extends ListFragment {
    private static final String TAG = "ActivityListFragment";

    private ActivityModelAdapter mAdapter;
    private Callbacks mCallbacks;

    /**
     * Create an interface for passing out data to the controlling activity.
     * This will allow us to handle which activity has been picked from the list and launch a new
     * summary fragment appropriately.
     */
    public interface Callbacks {
        void onActivitySelected(ActivityModel activityModel);
    }

    /**
     * This will setup the bootstrapping needed to launch summary activities appropriately.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    /**
     * Just a little cleanup to keep memory freed up
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new ActivityModelAdapter(getActivity());
        setListAdapter(mAdapter);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        //Register the context menu for this list
        ListView listView = (ListView)view.findViewById(android.R.id.list);
        registerForContextMenu(listView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.activity_monitor_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result = true;

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        ActivityModel activityModel = (ActivityModel)mAdapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.activity_monitor_list_item_context_delete:
                ActivityController.get(getActivity()).delete(activityModel);
                updateUI();
                break;
            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ActivityModel selectedActivity = (ActivityModel)mAdapter.getItem(position);
        mCallbacks.onActivitySelected(selectedActivity);
        //super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        ((ActivityModelAdapter)getListAdapter()).notifyDataSetChanged();
    }
}
