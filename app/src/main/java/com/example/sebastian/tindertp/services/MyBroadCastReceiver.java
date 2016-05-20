package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.commonTools.Common;

import java.util.List;


public class MyBroadCastReceiver extends BroadcastReceiver {

    private List<String> messages;
    private List<String> users;
    private int notificationCount;
    private Activity fromActivity;

    public void setUsersAndMessages(List<String> users, List<String> messages) {
        this.users = users;
        this.messages = messages;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public void setLessNotificationCount(int less) {
        this.notificationCount -= less;
    }

    public MyBroadCastReceiver(Activity fromActivity) {
        this.fromActivity = fromActivity;
        notificationCount = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        String user = intent.getStringExtra("user");

        if(message != null && user != null) {
            messages.add(message);
            users.add(user);
            notificationCount++;
            fromActivity.invalidateOptionsMenu();
        }
    }
}
