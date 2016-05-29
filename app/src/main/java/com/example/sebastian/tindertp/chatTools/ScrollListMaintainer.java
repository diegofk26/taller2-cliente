package com.example.sebastian.tindertp.chatTools;

import android.view.ViewTreeObserver;
import android.widget.ListView;

public class ScrollListMaintainer {

    public static void maintainScrollPosition(final ListView list, final int positionToPreserve) {

        list.post(new Runnable() {
            @Override
            public void run() {
                list.setSelection(positionToPreserve);
            }
        });

        list.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (list.getFirstVisiblePosition() == positionToPreserve) {
                    list.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

}
