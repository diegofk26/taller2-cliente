package com.example.sebastian.tindertp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import com.example.sebastian.tindertp.EditProfileActivity;
import com.example.sebastian.tindertp.ExpandedListAdapters.ExpandableListAdapter;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;

import java.util.Map;

public class ReceiverOnProfileEdit extends BroadcastReceiver {

    private EditProfileActivity profileActivity;
    private ExpandableListAdapter adpProfile;
    private  Map<Integer,String> mapperID;

    public ReceiverOnProfileEdit(EditProfileActivity ctx,ExpandableListAdapter adpProfile, Map<Integer,String> mapperID) {
        this.mapperID = mapperID;
        this.adpProfile = adpProfile;
        profileActivity = ctx;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String json = intent.getStringExtra("json");
        if(json != null) {

            Log.i("ass", json);

            MultiHashMap dataChild = new MultiHashMap();
            ProfileInfo myProfile = new ProfileInfo(json,dataChild);
            adpProfile.setAllEditText(mapperID,dataChild);

            EditText name = (EditText)profileActivity.findViewById(R.id.editText5);
            EditText alias = (EditText)profileActivity.findViewById(R.id.editText6);

            name.setText(myProfile.name);
            alias.setText((myProfile.alias));

            profileActivity.saveProfile(myProfile);

        }
    }
}
