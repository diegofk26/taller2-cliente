package com.example.sebastian.tindertp;


import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

public interface DataTransfer {
    String getUser();
    String getURL();
    String getToken();
    String getChatName();
    ConnectivityManager getConectivityManager();
    View findView(int id);
    Context getContext();
}
