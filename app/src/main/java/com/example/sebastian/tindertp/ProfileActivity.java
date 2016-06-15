package com.example.sebastian.tindertp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;
import com.example.sebastian.tindertp.services.MyBroadCastReceiver;
import com.example.sebastian.tindertp.services.PriorActivitiesUpdater;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView txtView;/**< En caso de error.*/
    private ArrayList<String> messages;
    private ArrayList<String> users;
    private ProfileInfo profile;
    private MyBroadCastReceiver onNotice;
    private PriorActivitiesUpdater onPriorCall;

    private void setImgProfile(Bitmap myBitmap){
        imgProfile.setImageBitmap(myBitmap);
    }

    private void getProfileImageIntoView(){
        if( getIntent().hasExtra(Common.PROFILE_JSON) ) {
            String profileJson = getIntent().getStringExtra(Common.PROFILE_JSON);
            profile = new ProfileInfo(profileJson);
            setImgProfile(profile.bitmap);
        } else{
            txtView.setText(R.string.error_image_profile);
        }
    }

    private void getNotificationCount() {
        if(getIntent().hasExtra(Common.MSSG_KEY)) {
            messages = getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            users = getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);
            onNotice.setNotificationCount(messages.size());
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarM);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        messages = new ArrayList<>();
        users = new ArrayList<>();

        imgProfile = (ImageView)findViewById(R.id.imageView2);
        txtView = (TextView)findViewById(R.id.textView3);

        onNotice = new MyBroadCastReceiver(this);
        onPriorCall = new PriorActivitiesUpdater(this, onNotice);

        getNotificationCount();
        getProfileImageIntoView();
        onNotice.setUsersAndMessages(users, messages);
        onPriorCall.setUsersAndMessage(users, messages);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("PROFILE"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onPriorCall, new IntentFilter("PRIOR"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.matching, menu);

        MenuItem item = menu.findItem(R.id.badge);

        MenuItemCompat.setActionView(item, R.layout.match_icon);

        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView icon = (ImageView)notifCount.findViewById(R.id.img);
        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        if(onNotice.getNotificationCount() != 0) {
            icon.setImageResource(R.drawable.new_msgg);
            tv.setText("+" + onNotice.getNotificationCount());
        } else {
            if (onPriorCall.areMessagesReaded()) {
                icon.setImageResource(R.drawable.empty_msg);
                tv.setText("");
            }
        }

        return true;
    }

    public void goToMesseges(View v) {
        Intent chatAct = new Intent(this, ChatListActivity.class);
        chatAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (onNotice.getNotificationCount() != 0) {
            chatAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
            chatAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
        }

        this.startActivity(chatAct);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.optionSelectedItem(item, this) || super.onOptionsItemSelected(item);
    }
}
