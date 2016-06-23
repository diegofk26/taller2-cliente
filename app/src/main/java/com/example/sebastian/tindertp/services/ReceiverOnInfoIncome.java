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

    public ReceiverOnInfoIncome(CustomAdapter adapter, List<RowItem> rowItems,
                                List<String> usersEmail, List<Bitmap> profilePics) {
        adp = adapter;
        this.rowItems = rowItems;
        this.usersEmail = usersEmail;
        this.profilePics = profilePics;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("OnInfoIncome","Recibo datos Json");

        String json = intent.getStringExtra("json");
        if(json != null) {
            ProfileInfo profile = new ProfileInfo(json);

            int index = usersEmail.indexOf(profile.email);

            if (index >= 0) {//ya estaba en la base del usuario
                Log.i("bitttt", "ya estaba el usuario");
                rowItems.get(index).setProfilePic(profile.bitmap);
                profilePics.set(index,profile.bitmap);
            } else {//es un usuario nuevo que llego por notificacion de GCM
                RowItem newItem = new RowItem(profile.name,profile.email,profile.bitmap,"");
                usersEmail.add(profile.email);
                profilePics.add(profile.bitmap);
                rowItems.add(newItem);
            }
            adp.addRowItem(rowItems);
        }
    }

}

