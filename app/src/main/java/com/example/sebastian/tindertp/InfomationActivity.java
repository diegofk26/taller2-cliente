package com.example.sebastian.tindertp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;
import com.example.sebastian.tindertp.services.ReceiverOnMssgReaded;
import com.example.sebastian.tindertp.services.ReceiverOnNewMatch;
import com.example.sebastian.tindertp.services.ReceiverOnNewMessage;

import java.util.ArrayList;

public class InfomationActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView txtView;/**< En caso de error.*/
    private ArrayList<String> messages;
    private ArrayList<String> users;
    private ProfileInfo profile;
    private ReceiverOnNewMessage onNotice;
    private ReceiverOnMssgReaded onMssgReaded;
    private ReceiverOnNewMatch onMatch;

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
        setContentView(R.layout.activity_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarM);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        messages = new ArrayList<>();
        users = new ArrayList<>();

        imgProfile = (ImageView)findViewById(R.id.imageView2);
        txtView = (TextView)findViewById(R.id.textView3);

        onNotice = new ReceiverOnNewMessage(this);
        onMssgReaded = new ReceiverOnMssgReaded(this, onNotice);
        onMatch = new ReceiverOnNewMatch(this);

        getNotificationCount();
        getProfileImageIntoView();
        onNotice.setUsersAndMessages(users, messages);
        onMssgReaded.setUsersAndMessage(users, messages);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotice,
                new IntentFilter(Common.PROFILE_MSG_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onMssgReaded,
                new IntentFilter(Common.MSSG_READED_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onMatch,
                new IntentFilter(Common.PROFILE_MATCH_KEY));

        setProfileInfo();

    }

    private void setProfileInfo() {
        TextView name = (TextView) findViewById(R.id.textView4);
        TextView alias = (TextView) findViewById(R.id.textView5);
        TextView age = (TextView) findViewById(R.id.textView18);
        TextView sex = (TextView) findViewById(R.id.textView19);
        TextView music = (TextView) findViewById(R.id.textView20);
        TextView bands = (TextView) findViewById(R.id.textView21);
        TextView sports = (TextView) findViewById(R.id.textView22);
        TextView sex_interest = (TextView) findViewById(R.id.textView23);
        TextView outdoors = (TextView) findViewById(R.id.textView24);
        TextView travel = (TextView) findViewById(R.id.textView25);
        TextView food = (TextView) findViewById(R.id.textView26);

        name.setText(Html.fromHtml("<b>" + name.getText().toString() + "</b> " + profile.name));
        alias.setText(Html.fromHtml("<b>" + alias.getText().toString() + "</b> " + profile.alias));
        age.setText(Html.fromHtml("<b>" + age.getText().toString() + "</b> " + profile.age));
        sex.setText(Html.fromHtml("<b>" + sex.getText().toString() + "</b> " + profile.sex));

        setInterestsTextView(Common.MUSIC, music);
        setInterestsTextView(Common.MUSIC_BAND, bands);
        setInterestsTextView(Common.SPORT, sports);
        setInterestsTextView(Common.SEX, sex_interest);
        setInterestsTextView(Common.OUTDOORS, outdoors);
        setInterestsTextView(Common.TRAVEL, travel);
        setInterestsTextView(Common.FOOD, food);
    }

    private void setInterestsTextView(String category, TextView text){
        String categorySpanish = text.getText().toString();
        text.setText(Html.fromHtml("<b>"+categorySpanish+"</b> "+profile.getInterestMap(category)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.matching, menu);

        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.mssg_icon);

        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView icon = (ImageView)notifCount.findViewById(R.id.img);
        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        MenuItem matchItem = menu.findItem(R.id.match_fire);
        MenuItemCompat.setActionView(matchItem, R.layout.fire_icon);

        RelativeLayout matchRelative = (RelativeLayout) MenuItemCompat.getActionView(matchItem);
        ImageView matchIcon = (ImageView)matchRelative.findViewById(R.id.fire_item);

        if (onMatch.haveMatch()) {
            matchIcon.setImageResource(R.drawable.match_fire);
        } else {
            matchIcon.setImageResource(R.drawable.match_no_fire);
        }

        if(onNotice.getNotificationCount() != 0) {
            icon.setImageResource(R.drawable.new_msgg);
            tv.setText("+" + onNotice.getNotificationCount());
        } else {
            if (onMssgReaded.areMessagesReaded()) {
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
