package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**Recibe un aviso de un nuevo mensaje y actualiza la vista*/
public class ReceiverOnNewMessage extends BroadcastReceiver {

    private List<String> messages;
    private List<String> users;
    private int notificationCount;
    private Activity fromActivity;
    private static final String RECEIVER_TAG = "ReceiverOnNewMessage";

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

    public ReceiverOnNewMessage(Activity fromActivity) {
        this.fromActivity = fromActivity;
        notificationCount = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        String user = intent.getStringExtra("user");

        if(message != null && user != null) {
            Log.i(RECEIVER_TAG,"Recibe aviso de nuevo mensaje");
            messages.add(message);
            users.add(user);
            notificationCount++;
            fromActivity.invalidateOptionsMenu();
        }
    }
}
