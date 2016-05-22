package com.example.sebastian.tindertp;


import com.example.sebastian.tindertp.animationTools.DepthPageTransformer;
import com.example.sebastian.tindertp.animationTools.FullScreenImageAdapter;
import com.example.sebastian.tindertp.application.TinderTP;
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
    /**Oculta la barra de estados en fullscreen.*/
    private void hideClockBateryBar(){
        View decorView = getWindow().getDecorView();
        // oculta la barra de estado.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**Obtiene datos pasados por Intent.*/
    private void getDataFromMatchingActivity(){
        Intent i = getIntent();
        firtPosition = i.getIntExtra(Common.IMG_POS_KEY, 0);
        imgFiles = i.getStringArrayExtra(Common.IMG_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        hideClockBateryBar();


        viewPager = (ViewPager) findViewById(R.id.pager);

        getDataFromMatchingActivity();

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,imgFiles);

        viewPager.setAdapter(adapter);
        //setea el animador de pagina.
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        // muestra la imagen seleccionada
        viewPager.setCurrentItem(firtPosition);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
