package com.example.sebastian.tindertp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;

import java.util.List;


public class ReceiverOnInfoIncome extends BroadcastReceiver{

    private CustomAdapter adp;
    private List<RowItem> rowItems;
    private List<String> usersEmail;
    private List<Bitmap> profilePics;
    private List<String> messages;
    private static final String RECEIVER_TAG = "OnInfoIncome";

    public ReceiverOnInfoIncome(CustomAdapter adapter, List<RowItem> rowItems,
                                List<String> usersEmail, List<Bitmap> profilePics, List<String> messages) {
        adp = adapter;
        this.rowItems = rowItems;
        this.usersEmail = usersEmail;
        this.messages = messages;
        this.profilePics = profilePics;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String json = intent.getStringExtra("json");
        if(json != null) {
            Log.i(RECEIVER_TAG,"Recibo datos Json");
            ProfileInfo profile = new ProfileInfo(json);

            int index = usersEmail.indexOf(profile.email);

            if (index >= 0) {//ya estaba en la base del usuario
                Log.i(RECEIVER_TAG, "Es un usuario viejo");
                rowItems.get(index).setProfilePic(profile.bitmap);
                profilePics.set(index,profile.bitmap);
            } else {//es un usuario nuevo que llego por notificacion de GCM
                Log.i(RECEIVER_TAG, "Es un usuario nuevo");
                RowItem newItem = new RowItem(profile.name,profile.email,profile.bitmap,"");
                messages.add("");
                usersEmail.add(profile.email);
                profilePics.add(profile.bitmap);
                rowItems.add(newItem);
            }
            adp.addRowItem(rowItems);
        }
    }

}

