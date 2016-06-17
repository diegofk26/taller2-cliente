package com.example.sebastian.tindertp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;

import java.util.List;


public class ReceiverOnInfoIncome extends BroadcastReceiver{

    private CustomAdapter adp;
    private List<RowItem> rowItems;
    private List<String> usersEmail;

    public ReceiverOnInfoIncome(CustomAdapter adapter, List<RowItem> rowItems, List<String> usersEmail) {
        adp = adapter;
        this.rowItems = rowItems;
        this.usersEmail = usersEmail;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String json = intent.getStringExtra("json");
        Log.i("BROAD", "entra broad");
        if(json != null) {
            ProfileInfo profile = new ProfileInfo(json);

            int index = usersEmail.indexOf(profile.email);

            if (index >= 0) {
                rowItems.get(index).setProfilePic(profile.bitmap);
            } else {
                RowItem newItem = new RowItem(profile.name,profile.bitmap,"");
                usersEmail.add(profile.email);
                rowItems.add(newItem);
            }
            adp.addRowItem(rowItems);

        }
    }

}

