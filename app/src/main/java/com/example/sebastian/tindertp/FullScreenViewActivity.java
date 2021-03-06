package com.example.sebastian.tindertp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.sebastian.tindertp.animationTools.FullScreenImageAdapter;
import com.example.sebastian.tindertp.commonTools.Common;

public class FullScreenViewActivity extends Activity{

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    private String picBase64;

    /**Oculta la barra de estados en fullscreen.*/
    private void hideClockBateryBar(){
        View decorView = getWindow().getDecorView();
        // oculta la barra de estado.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**Obtiene datos pasados por Intent.*/
    private void getDataFromMatchingActivity(){
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        picBase64 = preferences.getString(Common.PHOTO_KEY,"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        hideClockBateryBar();

        viewPager = (ViewPager) findViewById(R.id.pager);

        getDataFromMatchingActivity();

        if ( !picBase64.isEmpty()) {

            adapter = new FullScreenImageAdapter(getApplicationContext(), picBase64);

            viewPager.setAdapter(adapter);

            // muestra la imagen seleccionada
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(Common.PHOTO_KEY).apply();
    }
}
