package com.example.sebastian.tindertp.commonTools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataThroughActivities {
    private ArrayList<String> messages = null;
    private ArrayList<String> users = null;


    public static DataThroughActivities getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void setMessages(ArrayList<String> users, ArrayList<String> messages) {
        this.messages = messages;
        this.users = users;
    }

    public void deleteMssg() {
        messages = null;
        users = null;
    }

    public void deleteMssg(String userName) {
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
