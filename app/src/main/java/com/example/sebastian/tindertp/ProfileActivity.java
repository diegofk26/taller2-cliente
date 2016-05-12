package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.Common;

import java.io.File;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView txtView;/**< En caso de error.*/
    private ArrayList<String> messages;
    private ArrayList<String> users;
    private int notificationCount;

    private void setImgProfile(String imgFile){

        final File myImageFile = new File(imgFile);
        Bitmap myBitmap = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
        imgProfile.setImageBitmap(myBitmap);
    }

    private void getProfileImageIntoView(){
        if( getIntent().hasExtra(Common.PROFILE_IMG_KEY) ) {
            String profileImage = getIntent().getStringExtra(Common.PROFILE_IMG_KEY);
            setImgProfile(profileImage);
        } else{
            txtView.setText("Error al cargar la imagen de perfil");
        }
    }

    private void getNotificationCount() {
        if(getIntent().hasExtra(Common.MSSG_KEY)) {
            messages = getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            users = getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);
            notificationCount= messages.size();
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

        notificationCount=0;
        messages = new ArrayList<>();

        imgProfile = (ImageView)findViewById(R.id.imageView2);
        txtView = (TextView)findViewById(R.id.textView3);

        getNotificationCount();
        getProfileImageIntoView();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("PROFILE"));

    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String user = intent.getStringExtra("user");

            if(message!=null && user != null) {
                Log.i("asd", "actulizacon PROFILE desde app abierta");
                messages.add(message);
                users.add(user);
                notificationCount++;
            }

            invalidateOptionsMenu();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matching, menu);

        MenuItem item = menu.findItem(R.id.badge);

        MenuItemCompat.setActionView(item, R.layout.match_bar);
        if(notificationCount!=0) {
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
            TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
            tv.setText("+" + notificationCount);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinderTP.profileResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.profilePaused();
    }

    public void goToMesseges(View v) {
        Common.startActivity(this, ChatListActivity.class);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.optionSelectedItem(item, this) || super.onOptionsItemSelected(item);
    }
}
