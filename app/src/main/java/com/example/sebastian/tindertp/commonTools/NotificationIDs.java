package com.example.sebastian.tindertp.commonTools;

import android.util.Log;

import java.util.ArrayList;

public class NotificationIDs {
    private ArrayList<Integer> notifications = null;
    private final static String NotificationIDs = "NotificationIDs";


    public static NotificationIDs getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void addNotification(int notification) {
        Log.i(NotificationIDs, "Agrega Id de notifcacion.");
        notifications.add(notification);
    }

    public boolean haveNotifications() {
        return notifications != null;
    }

    public int getNotification(int position) {
        return notifications.get(position);
    }

    public int getSize() {
        return notifications.size();
    }

    public void deleteNotifications() {
        Log.i(NotificationIDs,"Se eliminan todos las notificaciones");
        notifications = null;
    }

    private static class SingletonHelper{
        private static final NotificationIDs INSTANCE = new NotificationIDs();
    }

    private NotificationIDs() {
        notifications = new ArrayList<>();
    }
}

