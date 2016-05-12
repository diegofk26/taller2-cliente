package com.example.sebastian.tindertp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ChatActivity extends AppCompatActivity {

    private String chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatName = "FromUser";
        TinderTP.updateChatName(chatName);


    }

    @Override
    protected void onResume() {
        super.onResume();
        TinderTP.chatResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.chatPaused();
    }

}
