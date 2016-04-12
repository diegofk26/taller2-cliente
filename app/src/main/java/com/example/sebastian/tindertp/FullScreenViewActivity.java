package com.example.sebastian.tindertp;


import com.example.sebastian.tindertp.animationTools.DepthPageTransformer;
import com.example.sebastian.tindertp.animationTools.FullScreenImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class FullScreenViewActivity extends Activity{

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    private int position;
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
        position = i.getIntExtra("position", 0);
        imgFiles = i.getStringArrayExtra("images");
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
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
