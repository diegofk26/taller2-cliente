package com.example.sebastian.tindertp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.sebastian.tindertp.internetTools.TestConnectionClient;

public class MainActivity extends Activity {

    public static final String PREF_FILE_NAME = "mypreferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        String url = preferences.getString("url", "fail");
        if (!url.equals("fail")) {
            TestConnectionClient testConn = new TestConnectionClient(this,url);
            testConn.runInBackground();
        }
        else {
            Intent main = new Intent(this, UrlActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(main);
            this.finish();
        }
        super.onCreate(savedInstanceState);
    }
}
