package com.example.sebastian.tindertp;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;
import com.example.sebastian.tindertp.services.ReceiverOnProfileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userEmail = getIntent().getStringExtra(Common.EMAIL_KEY);

        MultiHashMap listDataChild = new MultiHashMap();
        List<String> listDataParent = new ArrayList<>();

        ExpandableAdpProfile profileAdp = new ExpandableAdpProfile(this,listDataChild,listDataParent);

        ExpandableListView expView = (ExpandableListView) findViewById(R.id.expandableListView);
        expView.setAdapter(profileAdp);

        ReceiverOnProfileInfo profileBuilder = new ReceiverOnProfileInfo(this,listDataChild,listDataParent,profileAdp);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(profileBuilder,
                new IntentFilter(Common.SPECIFIC_USER_PROF));

        String user = ((TinderTP) getApplication()).getUser();
        String url = ((TinderTP) getApplication()).getUrl();
        String token = ((TinderTP) getApplication()).getToken();

        ConnectionStruct conn = new ConnectionStruct(Common.INFO,Common.GET,url);
        Map<String,String> values = HeaderBuilder.forUserInfo(user, token, userEmail);
        NewUserDownloaderClient client = new NewUserDownloaderClient(this, findViewById(R.id.relative_profile),
                Common.SPECIFIC_USER_PROF, conn, values);
        client.runInBackground();


    }

}
