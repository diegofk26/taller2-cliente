package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverOnNewMatch extends BroadcastReceiver {

    private boolean haveMatch = false;
    private Activity activity;

    public ReceiverOnNewMatch(Activity fromActivity) {
        activity = fromActivity;
    }

    public boolean haveMatch(){
        return haveMatch;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String user = intent.getStringExtra("user");

        if( user != null) {
            haveMatch = true;
            activity.invalidateOptionsMenu();
        }
    }

    public void setHaveMatch(boolean haveMatch) {
        this.haveMatch = haveMatch;
    }
}
