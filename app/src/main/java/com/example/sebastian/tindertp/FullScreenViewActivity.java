package com.example.sebastian.tindertp;


import com.example.sebastian.tindertp.animationTools.DepthPageTransformer;
import com.example.sebastian.tindertp.animationTools.FullScreenImageAdapter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ImagesPosition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class FullScreenViewActivity extends Activity{

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    private int firtPosition;
    private String[] imgFiles;

    private void hideClockBateryBar(){
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void getDataFromMatchingActivity(){
        //Get the variables of MatchingActivity
        Intent i = getIntent();
        firtPosition = i.getIntExtra(Common.IMG_POS_KEY, 0);
        imgFiles = i.getStringArrayExtra(Common.IMG_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        hideClockBateryBar();

        //Set the page and the animator depthPageTransformer
        viewPager = (ViewPager) findViewById(R.id.pager);
        //viewPager.setPageTransformer(true,new DepthPageTransformer());

        getDataFromMatchingActivity();

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,imgFiles);

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        // displaying selected image first
        viewPager.setCurrentItem(firtPosition);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TinderTP.fullScreenResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.fullScreenPaused();
    }

    /**Al volver a la activitidad que la llamo, setea la nueva posicion de la imagen si esta cambió.*/
    @Override
    public void onBackPressed() {
        if (firtPosition != viewPager.getCurrentItem()) {
            ImagesPosition.getInstance().setPosition(viewPager.getCurrentItem());
            ImagesPosition.getInstance().setPositionChanged(true);
        }
        super.onBackPressed();
        finish();
    }
}
