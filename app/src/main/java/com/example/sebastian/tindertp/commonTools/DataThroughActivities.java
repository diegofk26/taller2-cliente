package com.example.sebastian.tindertp.commonTools;

import android.util.Log;

import java.util.ArrayList;

public class DataThroughActivities {
    private ArrayList<String> messages = null;
    private ArrayList<String> users = null;
    private boolean matches = false;
    private final static String DataThrough_TAG = "DataThrough";


    public static DataThroughActivities getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void setMessages(ArrayList<String> users, ArrayList<String> messages) {
        Log.i(DataThrough_TAG,"Se setean los mensajes y usuarios.");
        this.messages = messages;
        this.users = users;
    }

    public void setMatches(boolean haveMatch) {
        Log.i(DataThrough_TAG,"Se setean el match.");
        matches = haveMatch;
    }

    public void deleteMssg() {
        Log.i(DataThrough_TAG,"Se eliminan todos los mensajes");
        messages = null;
        users = null;
    }

    public void deleteMssg(String userName) {
        Log.i(DataThrough_TAG,"Se eliminan los mensajes de " + userName);
        for (int i = users.size() - 1; i >= 0; i--) {
            if (users.get(i).equals(userName)){
                users.remove(i);
                messages.remove(i);
            }
        }
    }


    public ArrayList<String> getMessages() {
        return messages;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public boolean hasMessages() {
        return messages != null;
    }

    public boolean hasMatches() {
        return matches;
    }

    public boolean areTwoDifferntUsers() {
        int i = 0;
        int j = 1;
        boolean differents = false;
        while( !differents && j < messages.size() && i < messages.size()) {

            if ( !users.get(i).equals(users.get(j))) {
                differents = true;
            }
            i++;j++;
        }

        return differents;
    }

    private static class SingletonHelper{
        private static final DataThroughActivities INSTANCE = new DataThroughActivities();
    }

    private DataThroughActivities() {}
}
