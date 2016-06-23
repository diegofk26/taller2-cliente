package com.example.sebastian.tindertp.ExpandedListAdapters;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;

public class MyScrollListener implements AbsListView.OnScrollListener {

    public MyScrollListener(Activity activity) {
        this.activity = activity;
    }

    Activity activity;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
        }
    }

}