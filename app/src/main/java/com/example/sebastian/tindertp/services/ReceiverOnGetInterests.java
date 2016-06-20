package com.example.sebastian.tindertp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sebastian.tindertp.CategoryUpdater;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.MultiHashIntStr;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReceiverOnGetInterests extends BroadcastReceiver {


    private List<String> catList = new ArrayList<>();
    private MultiHashIntStr categoryValues = new MultiHashIntStr();
    private CategoryUpdater updater;

    public ReceiverOnGetInterests(CategoryUpdater updater) {
        this.updater = updater;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("json");
        Log.i("RECEIVER","llega el json");
        if (json != null) {

            Log.i("ERROR -", "Entra en prueba");
            categoryValues.put(0, "Rock");
            categoryValues.put(0,"Cumbia");
            categoryValues.put(0,"Electronica");
            categoryValues.put(1,"una banda");
            categoryValues.put(1,"una pala");
            categoryValues.put(1,"una cosa");
            categoryValues.put(1,"un gato");
            categoryValues.put(1,"unalata");
            categoryValues.put(2,"chocolate");
            categoryValues.put(2,"pollo");
            categoryValues.put(3,"Yoga");
            categoryValues.put(4,"Brasil");

            catList.add(Common.MUSIC);
            catList.add(Common.MUSIC_BAND);
            catList.add(Common.FOOD);
            catList.add(Common.OUTDOORS);
            catList.add(Common.TRAVEL);
            catList.add(Common.SPORT);
            catList.add(Common.SEX);
            updater.update(catList, categoryValues);

/*
            try {
                JSONObject jsonO = new JSONObject(json);
                JSONArray interests = jsonO.getJSONArray(Common.INTERESTS_KEY);
                for (int i = 0; i < interests.length(); i++) {
                    JSONObject catObj = (JSONObject)interests.get(i);
                    String category = catObj.getString(Common.CATEGORY_KEY);
                    catList.add(category);
                    categoryValues.put(i,catObj.getString(Common.VALUE_KEY));
                }
                updater.update(catList, categoryValues);
            }catch (JSONException e) {}

            */
        }else {

        }

    }
}
