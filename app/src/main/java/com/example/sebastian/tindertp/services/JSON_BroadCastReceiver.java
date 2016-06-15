package com.example.sebastian.tindertp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON_BroadCastReceiver extends BroadcastReceiver {

    private MatchingActivity matchingActivity;

    public JSON_BroadCastReceiver(MatchingActivity matching) {
        matchingActivity = matching;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("json");
        Log.i("BROAD", "entra broad");
        if(json != null) {

            ProfileInfo profile = new ProfileInfo(json);

            matchingActivity.saveEmailPossibleMatch(profile.email);
            matchingActivity.setTitle(profile.name + ", " + profile.age + ".");
            matchingActivity.setImage(profile.bitmap, profile.photo);
            matchingActivity.storeToProfile(json);
        }

    }
}
