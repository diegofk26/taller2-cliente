package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.commonTools.Common;

import java.util.Collections;
import java.util.List;


public class PriorActivitiesUpdater extends BroadcastReceiver {

    MyBroadCastReceiver onNotice;
    private Activity fromActivity;
    List<String> users;
    List<String> message;
    private boolean areMessagesReaded;

    public void setUsersAndMessage(List<String> users, List<String> mssg) {
        this.users = users;
        message = mssg;
    }

    public boolean areMessagesReaded() {
        return areMessagesReaded;
    }

    public void setAreMessagesReaded(boolean areMessagesReaded) {
        this.areMessagesReaded = areMessagesReaded;
    }

    public PriorActivitiesUpdater(Activity fromActivity, MyBroadCastReceiver onNotice) {
        this.fromActivity = fromActivity;
        this.onNotice = onNotice;
        this.areMessagesReaded = false;
    }

    private void deleteMessages(String user) {
        for (int i = users.size() - 1; i >= 0; i--) {
            if (users.get(i).equals(user)) {
                users.remove(i);
                message.remove(i);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String user = intent.getStringExtra("user");

        if(user != null) {
            int mssgReaded = Collections.frequency(users, user);
            deleteMessages(user);
            onNotice.setLessNotificationCount(mssgReaded);
            areMessagesReaded = true;
            fromActivity.invalidateOptionsMenu();
        }
    }

}
