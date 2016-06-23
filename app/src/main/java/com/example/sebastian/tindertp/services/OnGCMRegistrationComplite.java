package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.UrlActivity;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.internetTools.ConnectionTester;

import java.util.ArrayList;

public class OnGCMRegistrationComplite extends BroadcastReceiver{

    private Activity context;

    public OnGCMRegistrationComplite(Activity context) {
       this.context = context;
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (context.getIntent().hasExtra(Common.MSSG_KEY)){
            Log.i("asd","Obtengo los EXTRAS del serive, pendingIntent");
            ArrayList<String> messages = context.getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            ArrayList<String> users = context.getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);
            Log.i("asd","size in MAIN" + messages.size());
            DataThroughActivities.getInstance().setMessages(users,messages);
        }

        if (context.getIntent().hasExtra(Common.MATCH_KEY)){
            boolean haveMatch = context.getIntent().getBooleanExtra(Common.MATCH_KEY,false);
            DataThroughActivities.getInstance().setMatches(haveMatch);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                String url = preferences.getString("url", Common.FAIL);

                if (!url.equals(Common.FAIL)) {
                    Log.i("test", "hay url " + url);
                    ConnectionTester testConn = new ConnectionTester(context, url, Common.TEST);
                    testConn.runInBackground();
                } else {
                    Log.i("test", "no hay url");
                    ActivityStarter.start(context, UrlActivity.class);
                    ((Activity)context).finish();
                }
            }
        }, 500);

    }
}
