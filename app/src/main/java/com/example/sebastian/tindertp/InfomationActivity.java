package com.example.sebastian.tindertp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.ExpandedListAdapters.ExpandableAdpProfile;
import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;
import com.example.sebastian.tindertp.services.ReceiverOnMssgReaded;
import com.example.sebastian.tindertp.services.ReceiverOnNewMatch;
import com.example.sebastian.tindertp.services.ReceiverOnNewMessage;

import java.util.ArrayList;
import java.util.List;

public class InfomationActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private ArrayList<String> messages;
    private ArrayList<String> users;
    private ProfileInfo profile;
    private ReceiverOnNewMessage onNotice;
    private ReceiverOnMssgReaded onMssgReaded;
    private ReceiverOnNewMatch onMatch;
    private int count;

    private void setImgProfile(Bitmap myBitmap){
        imgProfile.setImageBitmap(myBitmap);
    }

    private void getProfileIntoView(MultiHashMap listDataChild, List<String> listDataParent){
        if( getIntent().hasExtra(Common.NAME_KEY) ) {

            SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
            String img64 =preferences.getString(Common.PHOTO_KEY, "");

            Bitmap bitmap = ImageBase64.decodeBase64(img64);

            setImgProfile(bitmap);

            count = getIntent().getIntExtra(Common.COUNT,0);

            for(int i = 0; i < count; i++) {
                String key = getIntent().getStringExtra(Common.MAP_KEY+i);
                listDataChild.putList(key, ArraySerialization.getPersistedArrayObject(this, key));
                listDataParent.add(key);
            }

            ExpandableAdpProfile profileAdp = new ExpandableAdpProfile(this,listDataChild,listDataParent);

            ExpandableListView expView = (ExpandableListView) findViewById(R.id.expandInfo);
            expView.setAdapter(profileAdp);


            for (int i = 0; i < count; i ++ ){
                expView.expandGroup(i);
            }

            profileAdp.addHeaders(listDataParent);
        } else{
            Common.showSnackbar(findViewById(R.id.relative_information), "Error al cargar la imagen.");
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(Common.PHOTO_KEY).apply();

        for(int i = 0; i < count; i++) {
            String key = getIntent().getStringExtra(Common.MAP_KEY+i);
            ArraySerialization.deleteArray(this, key);
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

        imgProfile = (ImageView)findViewById(R.id.infoImage);

        onNotice = new ReceiverOnNewMessage(this);
        onMssgReaded = new ReceiverOnMssgReaded(this, onNotice);
        onMatch = new ReceiverOnNewMatch(this);

        MultiHashMap listDataChild = new MultiHashMap();
        List<String> listDataParent = new ArrayList<>();

        getNotificationCount();
        getProfileIntoView(listDataChild, listDataParent);

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
        TextView name = (TextView) findViewById(R.id.nombreInfo);
        TextView alias = (TextView) findViewById(R.id.aliasInfo);
        TextView age = (TextView) findViewById(R.id.edadInfo);
        TextView sex = (TextView) findViewById(R.id.sexInfo);

        String nameS = getIntent().getStringExtra(Common.NAME_KEY);
        String aliasS = getIntent().getStringExtra(Common.ALIAS_KEY);
        int ageI = getIntent().getIntExtra(Common.AGE_KEY, 0);
        String sexS = getIntent().getStringExtra(Common.SEX_KEY);

        name.setText(Html.fromHtml("<b>" + name.getText().toString() + "</b> " + nameS));
        alias.setText(Html.fromHtml("<b>" + alias.getText().toString() + "</b> " + aliasS));
        age.setText(Html.fromHtml("<b>" + age.getText().toString() + "</b> " + ageI));
        sex.setText(Html.fromHtml("<b>" + sex.getText().toString() + "</b> " + sexS));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.information_menu, menu);

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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ActivityStarter.start(getApplicationContext(), UrlActivity.class);
            return true;
        } else if (id == R.id.action_logout ) {
            Common.clearLoginSaved(getApplicationContext());
            ActivityStarter.startClear(getApplicationContext(), SelectLoginOrRegistryActivity.class);
            finish();
            return true;
        }else if (id == R.id.badge) {
            ActivityStarter.start(getApplicationContext(), ChatListActivity.class);
            return true;
        }else if (id == R.id.my_profile) {
            String user = ((TinderTP) getApplication()).getUser();
            Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
            profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profile.putExtra(Common.EMAIL_KEY, user);
            startActivity(profile);
        }else if (id == R.id.edit_profile) {
            ActivityStarter.start(getApplicationContext(), EditProfileActivity.class);
        }else if (id == R.id.delete_profile) {
            ActivityStarter.start(getApplicationContext(),AreYouSureActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }
}
