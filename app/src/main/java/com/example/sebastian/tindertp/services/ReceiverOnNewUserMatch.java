package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.Interfaces.DataTransfer;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;

import java.util.Map;

public class ReceiverOnNewUserMatch extends BroadcastReceiver{

    private View view;
    private DataTransfer context;
    private static final String RECEIVER_TAG = "OnNewUserMatch";

    public ReceiverOnNewUserMatch(DataTransfer ctx, View v ) {
        context = ctx;
        view = v;
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String userMatch = intent.getStringExtra("user");

        if( userMatch != null) {

            Log.i(RECEIVER_TAG,"Recibo un nuevo match");

            String token =  context.getToken();
            String user = context.getUser();
            String url = context.getURL();

            ConnectionStruct conn = new ConnectionStruct(Common.INFO,Common.GET,url);
            Map<String,String> values = HeaderBuilder.forUserInfo(user, token, userMatch);
            NewUserDownloaderClient client = new NewUserDownloaderClient((Context)context, view,
                    Common.SPECIFIC_USER_KEY, conn, values);
            client.runInBackground();
        }

    }
}
