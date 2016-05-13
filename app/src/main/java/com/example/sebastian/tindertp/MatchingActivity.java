package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.ImagesPosition;
import com.example.sebastian.tindertp.internetTools.ImageDownloaderClient;
import com.example.sebastian.tindertp.gestureTools.OnSwipeTapTouchListener;

import java.util.ArrayList;
import java.util.List;
//!Activity donde se matchean las personas.
public class MatchingActivity extends AppCompatActivity {

    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;

    private int notificationCount;
    private ArrayList<String> messages;
    private ArrayList<String> users;

    private List<Bitmap> bitmaps;
    private ImageView imgView;
    private boolean firstTime;
    private List<String> imgFiles;
    private int imgPosition;
    private ImageDownloaderClient imageDownloader;
    private OnSwipeTapTouchListener customListener;/**< Listener para fling y tap.*/

    @Override
    /**En la creacion se empiezan a descargas las 3 primeras imagenes o menos.*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarM);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mText = (TextView)findViewById(R.id.textView2);

        initalize();

        if (DataThroughActivities.getInstance().hasMessages() ){
            Common.startActivity(this,ChatListActivity.class);
        }

        imageDownloader =  new ImageDownloaderClient(this,mText);
        imageDownloader.runInBackground();

        customListener = new OnSwipeTapTouchListener(this);
        //listen gesture fling or tap
        imgView.setOnTouchListener(customListener);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("MATCH"));
    }

    private void initalize(){
        notificationCount = 0;

        if(DataThroughActivities.getInstance().hasMessages()) {
            Log.i("as","estoy actuzalizando match desde la aplicacion cerrada");
            messages = DataThroughActivities.getInstance().getMessages();
            users = DataThroughActivities.getInstance().getUsers();
            notificationCount = messages.size();
            Log.i("asd","size en MATCH" + notificationCount);
            invalidateOptionsMenu();
        }

        if (messages == null && users == null) {
            messages = new ArrayList<>();
            users = new ArrayList<>();
        }

        bitmaps = new ArrayList<>();
        imgFiles = new ArrayList<>();
        firstTime = true;
        imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageResource(RES_PLACEHOLDER);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String user = intent.getStringExtra("user");

            if(message!=null && user != null) {
                Log.i("asd","estoy actualizando el match from app abierta");
                messages.add(message);
                users.add(user);
                notificationCount++;
            }

            invalidateOptionsMenu();
        }
    };

    /**Listener de boton Info (i) que va al perfil del usuario en vista. Solo si tiene la primer
     * imagen descargada, que es la del perfil.*/
    public void goToProfile(View v) {
        if (imgFiles.size()!= 0){
            Intent profileAct = new Intent(this, ProfileActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profileAct.putExtra(Common.PROFILE_IMG_KEY, imgFiles.get(0));
            profileAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
            profileAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);

            this.startActivity(profileAct);
        }
    }

    public void goToMesseges(View v) {
        Common.startActivity(this, ChatListActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.matchPaused();
    }

    //!Actualiza la posicion de la imagen al retornar a la actividad.
    /**Cuando se retorna a la actividad si tiene imagenes en bitmaps, se llama al
     * singleton ImagesPosition, que de no estar seteado devuelve la posicion actual de las imagenes
     * y si esta seteado es porque se ingreso en FullscreenActivity y se cambio el foco de la imagen.
     * Setea la nueva imagen y si esta en la anteultima posicion de las imagenes ya descargadas
     * se descarga la siguiente.*/
    public void onResume() {
        super.onResume();
        TinderTP.matchResumed();

        if (bitmaps.size() != 0 && ImagesPosition.getInstance().positionChanged() ) {
            int newImgPos = ImagesPosition.getInstance(imgPosition).getPosition();
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
        getMenuInflater().inflate(R.menu.menu_matching, menu);

        MenuItem item = menu.findItem(R.id.badge);

        MenuItemCompat.setActionView(item, R.layout.match_bar);
        if(notificationCount!=0) {
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
            TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
            tv.setText("+" + notificationCount);
        }

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