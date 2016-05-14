package com.example.sebastian.tindertp.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by sebastian on 10/04/16.
 */
public class TinderTP extends Application {

    private String url;
    private static boolean chatVisible;
    private static String chatName;



    public static void chatResumed() {
        chatVisible=true;
    }
    public static void chatPaused() {
        chatVisible=false;
    }



    public static void updateChatName(String chName) {
        chatName = chName;
    }

    public static boolean isTheSameChat(String chName) {
        if (chatVisible) {
            return chName.equals(chatName);
        } else {
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String newUrl) {
        this.url = newUrl;
    }

}
