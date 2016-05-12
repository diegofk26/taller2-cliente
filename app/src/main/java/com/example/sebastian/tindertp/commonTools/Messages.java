package com.example.sebastian.tindertp.commonTools;

import java.util.ArrayList;

public class Messages {
    private static Messages ourInstance = null;
    private ArrayList<String> messages;
    private ArrayList<String> users;

    public static Messages getInstance() {
        if(ourInstance == null)
        {
            ourInstance = new Messages();
        }
        return ourInstance;
    }

    private Messages() {
        messages = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void addMessage(String user,String mssg) {
        messages.add(mssg);
        users.add(user);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
    public ArrayList<String> getUsers() {
        return users;
    }
}
