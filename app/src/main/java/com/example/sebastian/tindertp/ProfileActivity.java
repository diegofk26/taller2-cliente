package com.example.sebastian.tindertp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.Common;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView txtView;/**< En caso de error.*/

    private void setImgProfile(String imgFile){

        final File myImageFile = new File(imgFile);
        Bitmap myBitmap = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
        imgProfile.setImageBitmap(myBitmap);
    }

    private void getProfileImageIntoView(){
        if( getIntent().hasExtra(Common.PROFILE_IMG_KEY) ) {
            String profileImage = (String) getIntent().getStringExtra(Common.PROFILE_IMG_KEY);
            setImgProfile(profileImage);
        } else{
            txtView.setText("Error al cargar la imagen de perfil");
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

        imgProfile = (ImageView)findViewById(R.id.imageView2);
        txtView = (TextView)findViewById(R.id.textView3);

        getProfileImageIntoView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matching, menu);

        MenuItem item = menu.findItem(R.id.badge);

        MenuItemCompat.setActionView(item, R.layout.match_bar);
        /*RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText("12");*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.optionSelectedItem(item, this) || super.onOptionsItemSelected(item);
    }
}
