package com.example.sebastian.tindertp.chatListTools;

import android.graphics.Bitmap;

public class RowItem {

    private String userName;
    private Bitmap profilePic;
    private String lastMessage;

    public RowItem(String userName, Bitmap profilePic, String message) {

        this.userName = userName;
        this.profilePic = profilePic;
        this.lastMessage = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}