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
import com.example.sebastian.tindertp.commonTools.ImagesPosition;
import com.example.sebastian.tindertp.internetTools.ImageDownloaderClient;
import com.example.sebastian.tindertp.gestureTools.OnSwipeTapTouchListener;

import java.util.ArrayList;
import java.util.List;
//!Activity donde se matchean las personas.
public class MatchingActivity extends AppCompatActivity {

    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;

    private List<Bitmap> bitmaps;
    private ImageView imgView;
    private boolean firstTime;
    private List<String> imgFiles;
    private int imgPosition;
    private ImageDownloaderClient imageDownloader;
    private OnSwipeTapTouchListener customListener;/**< Listener para fling y tap.*/

    private void initalize(){
        bitmaps = new ArrayList<Bitmap>();
        imgFiles = new ArrayList<String>();
        firstTime = true;
        imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageResource(RES_PLACEHOLDER);
    }

    @Override
    /**En la creacion se empiezan a descargas las 3 primeras imagenes o menos.*/
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

        customListener = new OnSwipeTapTouchListener(this);
        //listen gesture fling or tap
        imgView.setOnTouchListener(customListener);

    }

    /**Listener de boton Info (i) que va al perfil del usuario en vista. Solo si tiene la primer
     * imagen descargada, que es la del perfil.*/
    public void goToProfile(View v) {
        if (imgFiles.size()!= 0){
            Intent profileAct = new Intent(this, ProfileActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profileAct.putExtra("profileFile", imgFiles.get(0));
            this.startActivity(profileAct);
        }
    }
    //!Actualiza la posicion de la imagen al retornar a la actividad.
    /**Cuando se retorna a la actividad si tiene imagenes en bitmaps, se llama al
     * singleton ImagesPosition, que de no estar seteado devuelve la posicion actual de las imagenes
     * y si esta seteado es porque se ingreso en FullscreenActivity y se cambio el foco de la imagen.
     * Setea la nueva imagen y si esta en la anteultima posicion de las imagenes ya descargadas
     * se descarga la siguiente.*/
    public void onResume() {
        super.onResume();
        if (bitmaps.size() != 0 && ImagesPosition.getInstance().positionChanged() ) {
            int newImgPos = ImagesPosition.getInstance(imgPosition).getPosition();
            Log.i("SEEEEE","cambioooo");
            setImagePosition(newImgPos);
            if (newImgPos + 1 == getBitmaps().size()) {
                if (!downloadComplete())
                    downloadNextImg();
            }
            customListener.setPosition(newImgPos);
            imgView.setImageBitmap(this.bitmaps.get(newImgPos));
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

    /**Cuando la descarga de una imagen se completo se obtiene los datos y setea la imagen. */
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.optionSelectedItem(item, this) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
