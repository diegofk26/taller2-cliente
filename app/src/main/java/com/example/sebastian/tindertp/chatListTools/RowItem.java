package com.example.sebastian.tindertp.chatListTools;

public class RowItem {

    private String userName;
    private int profilePic;
    private String lastMessage;

    public RowItem(String userName, int profilePic, String message) {

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

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}