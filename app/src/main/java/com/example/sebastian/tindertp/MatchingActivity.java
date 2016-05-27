package com.example.sebastian.tindertp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatTools.ChatMessage;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.ImagesPosition;
import com.example.sebastian.tindertp.internetTools.ImageDownloaderClient;
import com.example.sebastian.tindertp.gestureTools.OnSwipeTapTouchListener;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;
import com.example.sebastian.tindertp.services.MyBroadCastReceiver;
import com.example.sebastian.tindertp.services.PriorActivitiesUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//!Activity donde se matchean las personas.
public class MatchingActivity extends AppCompatActivity {

    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;

    private ArrayList<String> messages;
    private ArrayList<String> users;
    private MyBroadCastReceiver onNotice;
    private PriorActivitiesUpdater onPriorCall;

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
            ActivityStarter.start(this, ChatListActivity.class);
        }

        imageDownloader =  new ImageDownloaderClient(this,mText);
        imageDownloader.runInBackground();

        customListener = new OnSwipeTapTouchListener(this);
        //escucha por fling o tap
        imgView.setOnTouchListener(customListener);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("MATCH"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onPriorCall, new IntentFilter("PRIOR"));
    }

    private void initalize(){

        onNotice = new MyBroadCastReceiver(this);
        onPriorCall = new PriorActivitiesUpdater(this, onNotice);

        if(DataThroughActivities.getInstance().hasMessages()) {
            Log.i("as","estoy actuzalizando match desde la aplicacion cerrada");
            messages = DataThroughActivities.getInstance().getMessages();
            users = DataThroughActivities.getInstance().getUsers();
            onNotice.setNotificationCount(messages.size());
            Log.i("asd","size en MATCH" + onNotice.getNotificationCount());
            invalidateOptionsMenu();
        } else if (ArraySerialization.hasPersistedMssg(this)) {
            Log.i("asddd","tiene mensajes persistidos TRUE");
            messages = ArraySerialization.getPersistedArray(this, "MSSG");
            users = ArraySerialization.getPersistedArray(this, "USER");
            onNotice.setNotificationCount(messages.size());
            invalidateOptionsMenu();
        }

        if (messages == null && users == null) {
            messages = new ArrayList<>();
            users = new ArrayList<>();
        }

        onNotice.setUsersAndMessages(users, messages);
        onPriorCall.setUsersAndMessage(users, messages);

        bitmaps = new ArrayList<>();
        imgFiles = new ArrayList<>();
        firstTime = true;
        imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageResource(RES_PLACEHOLDER);
    }

    public void sendLike(View v) {
        String url = ((TinderTP) this.getApplication()).getUrl();
        String token = ((TinderTP) this.getApplication()).getToken();
        ConnectionStruct conn = new ConnectionStruct(Common.CHAT, Common.POST, url);
        Map<String, String> headers = HeaderBuilder.forSendMessage(token, "", "");

        RequestResponseClient client = new RequestResponseClient(this, conn,headers) {
            @Override
            protected void getJson() throws IOException {

            }

            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {

                } else {
                    showText("No se pudo enviar el Like.");
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(findViewById(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

    }

    public void sendDislike(View v) {
        String url = ((TinderTP) this.getApplication()).getUrl();
        String token = ((TinderTP) this.getApplication()).getToken();
        ConnectionStruct conn = new ConnectionStruct(Common.CHAT, Common.POST, url);
        Map<String,String> headers = HeaderBuilder.forSendMessage(token, "", "");


    }

    /**Listener de boton Info (i) que va al perfil del usuario en vista. Solo si tiene la primer
     * imagen descargada, que es la del perfil.*/
    public void goToProfile(View v) {
        if (imgFiles.size()!= 0){
            Intent profileAct = new Intent(this, ProfileActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profileAct.putExtra(Common.PROFILE_IMG_KEY, imgFiles.get(0));
            if (onNotice.getNotificationCount() != 0) {
                profileAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
                profileAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
            }

            this.startActivity(profileAct);
        }
    }

    public void goToMesseges(View v) {
        Intent chatAct = new Intent(this, ChatListActivity.class);
        chatAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (onNotice.getNotificationCount() != 0) {
            chatAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
            chatAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
        }

        this.startActivity(chatAct);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        //muestra el menu.
        getMenuInflater().inflate(R.menu.menu_matching, menu);

        MenuItem item = menu.findItem(R.id.badge);

        MenuItemCompat.setActionView(item, R.layout.match_bar);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView icon = (ImageView)notifCount.findViewById(R.id.img);
        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        if(onNotice.getNotificationCount() != 0) {
            icon.setImageResource(R.drawable.new_msgg);
            tv.setText("+" + onNotice.getNotificationCount());
        } else {
            if (onPriorCall.areMessagesReaded()) {
                icon.setImageResource(R.drawable.empty_msg);
                tv.setText("");
            }
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