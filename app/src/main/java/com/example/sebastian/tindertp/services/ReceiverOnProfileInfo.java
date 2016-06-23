package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.ExpandedListAdapters.ExpandableAdpProfile;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;

import java.util.List;


public class ReceiverOnProfileInfo  extends BroadcastReceiver {

    private Context context;
    private MultiHashMap listDataChild;
    private List<String> listDataParent;
    private ExpandableAdpProfile adpProfile;

    public ReceiverOnProfileInfo(Context ctx, MultiHashMap listDataChild, List<String> listDataParent,
                                 ExpandableAdpProfile adpProfile) {
        context = ctx;
        this.listDataChild = listDataChild;
        this.listDataParent = listDataParent;
        this.adpProfile = adpProfile;
    }

    private void settingText(TextView text, String value){
        String header = text.getText().toString();
        text.setText(Html.fromHtml("<b>" + header + "</b> " + value));
    }

    @Override
    public void onReceive(Context cxt, Intent intent) {

        String json = intent.getStringExtra("json");

        if (json != null) {

            ProfileInfo profile = new ProfileInfo(json,listDataChild);

            ImageView imgProfile = (ImageView) ((Activity)context).findViewById(R.id.profImage);
            TextView name = (TextView) ((Activity)context).findViewById(R.id.nombreProf);
            TextView alias = (TextView) ((Activity)context).findViewById(R.id.aliasProf);
            TextView age = (TextView) ((Activity)context).findViewById(R.id.edadProf);
            TextView sex = (TextView) ((Activity)context).findViewById(R.id.sexProf);

            imgProfile.setImageBitmap(profile.bitmap);

            settingText(alias, profile.alias);
            settingText(name, profile.name);
            settingText(age, profile.age + "");
            settingText(sex, profile.sex);

            for (String key : listDataChild.getKeys()) {
                listDataParent.add(key);
            }

            adpProfile.addHeaders(listDataParent);
        }

    }
}
