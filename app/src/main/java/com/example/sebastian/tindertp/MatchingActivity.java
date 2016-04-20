package com.example.sebastian.tindertp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.internetTools.ImageDownloaderClient;
import com.example.sebastian.tindertp.gestureTools.OnSwipeTapTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MatchingActivity extends AppCompatActivity {

    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;

    private List<Bitmap> bitmaps;
    private ImageView imgView;
    private boolean firstTime;
    private List<String> imgFiles;
    private int imgPosition;
    private ImageDownloaderClient imageDownloader;

    private void initalize(){
        bitmaps = new ArrayList<Bitmap>();
        imgFiles = new ArrayList<String>();
        firstTime = true;
        imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageResource(RES_PLACEHOLDER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mText = (TextView)findViewById(R.id.textView2);

        initalize();

        imageDownloader =  new ImageDownloaderClient(this,mText);
        imageDownloader.runInBackground();

        //listen gesture fling or tap
        imgView.setOnTouchListener(new OnSwipeTapTouchListener(this));

    }

    public void goToProfile(View v) {
        if (imgFiles.size()!= 0){
            Intent profileAct = new Intent(this, ProfileActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profileAct.putExtra("profileFile", imgFiles.get(0));
            this.startActivity(profileAct);
        }
    }


    public ImageView getImgView(){
        return imgView;
    }

    public List<String> getImgFiles(){
        return imgFiles;
    }

    public List<Bitmap> getBitmaps(){
        return bitmaps;
    }

    public void onBackgroundTaskDataObtained(Bitmap bitmap,String file) {
        this.imgFiles.add(file);
        this.bitmaps.add(bitmap);
        if(firstTime) {
            firstTime = false;
            imgView.setImageBitmap(this.bitmaps.get(0));
            Log.i("Bitmap saved", "success");
            imgView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        }
    }

    public void downloadNextImg(){
        imageDownloader.runInBackground();
    }

    public boolean downloadComplete() {
        return imageDownloader.downloadComplete();
    }

    public void setImagePosition(int newPosition){
        imgPosition = newPosition;
    }

    public int getImagePosition(){
        return imgPosition ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void starActivity(Class<?> newActivity) {
        Intent activity = new Intent(this, newActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(activity);
    }

    private void clearLoginSaved(){
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(Common.USER_KEY).apply();
        preferences.edit().remove(Common.PASS_KEY).apply();
        Log.i("Clear","Delete login preferences.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //settings (URL for now) is started
        int id = item.getItemId();
        if (id == R.id.action_settings) {
           starActivity(UrlActivity.class);
            return true;
        } else if (id == R.id.action_logout ) {
            clearLoginSaved();
            starActivity(LoginActivity.class);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
