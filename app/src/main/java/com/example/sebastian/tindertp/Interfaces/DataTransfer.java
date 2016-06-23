package com.example.sebastian.tindertp.Interfaces;


import android.content.Context;
import android.view.View;

public interface DataTransfer {
    String getUser();
    String getURL();
    String getToken();
    String getChatName();
    String getChatEmail();
    View findView(int id);
    Context getContext();
}
