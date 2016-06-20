package com.example.sebastian.tindertp.application;

import android.app.Application;


public class TinderTP extends Application {

    private String url;
    private String token;
    private String user;
    private static boolean chatVisible;
    private static String chatName;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String tk) {
        token = tk;
    }

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
