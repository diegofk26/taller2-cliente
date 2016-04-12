package com.example.sebastian.tindertp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.internetTools.TestConnectionClient;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "mypreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        String url = preferences.getString("url", "fail");
        if (!url.equals("fail")) {
            Log.i("test","hay url "+url);
            TestConnectionClient testConn = new TestConnectionClient(this,url,"/test");
            testConn.runInBackground();
        }
        else {
            Log.i("test","no hay url");
            Intent main = new Intent(this, UrlActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(main);
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
