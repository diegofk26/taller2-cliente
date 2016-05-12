package com.example.sebastian.tindertp;

import android.app.Application;
import android.content.Context;

/**
 * Created by sebastian on 10/04/16.
 */
public class TinderTP extends Application {

    private String url;
    private static boolean active;
    private static boolean chatVisible;
    private static String chatName;
    private static boolean matchVisible;
    private static boolean chatListVisible;
    private static boolean fullScreenVisible;
    private static boolean profileVisible;

    public static void profileResumed() {
        profileVisible=true;
    }
    public static void profilePaused() {
        profileVisible=false;
    }

    public static void fullScreenResumed() {
        fullScreenVisible=true;
    }
    public static void fullScreenPaused() {
        fullScreenVisible=false;
    }

    public static void chatResumed() {
        chatVisible=true;
    }
    public static void chatPaused() {
        chatVisible=false;
    }

    public static void matchResumed() {
        matchVisible=true;
    }
    public static void matchPaused() {
        matchVisible=false;
    }

    public static void chatListResumed() {
        chatListVisible=true;
    }
    public static void chatListPaused() {
        chatListVisible=false;
    }

    public static boolean isFullScreenVisible() {
        return fullScreenVisible;
    }
    public static boolean isProfileVisible() {
        return profileVisible;
    }
    public static boolean isChatVisible() {
        return chatVisible;
    }
    public static boolean isMatchVisible() {
        return matchVisible;
    }
    public static boolean isChatListVisible() {
        return chatListVisible;
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
