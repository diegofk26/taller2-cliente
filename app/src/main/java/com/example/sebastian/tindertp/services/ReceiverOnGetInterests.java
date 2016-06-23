package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sebastian.tindertp.CategoryUpdater;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.MultiHashIntStr;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiverOnGetInterests extends BroadcastReceiver {

    private MultiHashMap categoryValues = new MultiHashMap();
    private CategoryUpdater updater;
    private boolean lookForProfileInfo = false;

    public ReceiverOnGetInterests(CategoryUpdater updater, boolean look) {
        this.updater = updater;
        lookForProfileInfo = look;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("json");
        Log.i("RECEIVER","llega el json");
        if (json != null) {
            try {
                JSONObject jsonO = new JSONObject(json);
                JSONArray interests = jsonO.getJSONArray(Common.INTERESTS_KEY);
                for (int i = 0; i < interests.length(); i++) {
                    JSONObject catObj = (JSONObject)interests.get(i);
                    String category = catObj.getString(Common.CATEGORY_KEY);
                    categoryValues.put(category,catObj.getString(Common.VALUE_KEY));
                }
                updater.update(categoryValues);

                if (lookForProfileInfo) {
                    String url = ((TinderTP)((Activity)updater).getApplication()).getUrl();
                    String user = ((TinderTP)((Activity)updater).getApplication()).getUser();
                    String token = ((TinderTP)((Activity)updater).getApplication()).getToken();
                    ConnectionStruct conn = new ConnectionStruct(Common.INFO,Common.GET, url);
                    Map<String,String> values = HeaderBuilder.forUserInfo(user, token, user);
                    NewUserDownloaderClient client = new NewUserDownloaderClient((Context)updater,
                            ((Activity)updater).findViewById(R.id.edit_relative), Common.MY_PROFILE, conn, values);
                    client.runInBackground();
                }
            }catch (JSONException e) {}


        }else {

        }

    }
}
