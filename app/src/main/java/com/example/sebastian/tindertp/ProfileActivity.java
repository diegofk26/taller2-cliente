package com.example.sebastian.tindertp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgView;
    private TextView txtView;

    private void setImg(String imgFile){

        final File myImageFile = new File(imgFile);
        Bitmap myBitmap = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
        imgView.setImageBitmap(myBitmap);
    }
    private void getProfileImageIntoView(){
        if( getIntent().hasExtra("profileFile") ) {
            String profileImage = (String) getIntent().getStringExtra("profileFile");
            setImg(profileImage);
        } else{
            txtView.setText("Error al cargar la imagen de perfil");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgView = (ImageView)findViewById(R.id.imageView2);
        txtView = (TextView)findViewById(R.id.textView3);

        getProfileImageIntoView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //settings (URL for now) is started
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent urlAct = new Intent(this, UrlActivity.class);
            urlAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.startActivity(urlAct);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
