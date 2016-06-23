package com.example.sebastian.tindertp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.sebastian.tindertp.commonTools.Common;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            //InstanceID.getInstance(this).deleteInstanceID();
            InstanceID instanceID = InstanceID.getInstance(this);
            String authorizedEntity = "361567099505";

            //InstanceID.getInstance(this).deleteToken(authorizedEntity,GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            //instanceID.deleteInstanceID();
            String token = instanceID.getToken(authorizedEntity,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);

            Log.i(TAG, "GCM Registration Token: " + token);

            sharedPreferences.edit().putString(Common.TOKEN_GCM, token).apply();
            sharedPreferences.edit().putBoolean("sentTokenToServer", true).apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean("sentTokenToServer", false).apply();
        }

        Intent registrationComplete = new Intent(Common.REGIST_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
