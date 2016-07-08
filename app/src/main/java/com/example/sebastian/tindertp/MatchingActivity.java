package com.example.sebastian.tindertp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.commonTools.NotificationIDs;
import com.example.sebastian.tindertp.commonTools.ProfileInfo;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;
import com.example.sebastian.tindertp.services.LocationGPSListener;
import com.example.sebastian.tindertp.services.ReceiverOnMssgReaded;
import com.example.sebastian.tindertp.services.ReceiverOnNewMatch;
import com.example.sebastian.tindertp.services.ReceiverOnNewMessage;
import com.example.sebastian.tindertp.services.ReceiverOnNewUserToMatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

//!Activity donde se matchean las personas.
public class MatchingActivity extends AppCompatActivity implements ConectivityManagerInterface {

    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;
    private final static String MATCH_TAG = "Matching Activity";

    private ArrayList<String> messages;
    private ArrayList<String> users;
    private ReceiverOnNewMessage onNotice;
    private ReceiverOnNewMatch onMatch;
    private ReceiverOnMssgReaded onPriorCall;
    private ReceiverOnNewUserToMatch onJsonNotice;

    private Bitmap bitmaps;
    private ImageView imgView;

    private NewUserDownloaderClient newUserDownloader;

    private final static String ADD_PIC_TAG = "Add picture Activity";
    private final static int SELECT_PICTURE = 1;
    private String selectedImagePath;

    private String photoBase64;
    private String jsonProfile;
    private boolean isDownloading;

    private String url;
    private String user;
    private String token;
    private String emailUserMatch;
    private boolean haveSomeoneToMatch;

    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    /**En la creacion se empiezan a descargas las 3 primeras imagenes o menos.*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarM);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationGPSListener(this);

        locationConfig();

        user = ((TinderTP) this.getApplication()).getUser();

        initalize();

        if (hasNotification()){
            ActivityStarter.start(getApplicationContext(), ChatListActivity.class);
        }

        url = ((TinderTP) this.getApplication()).getUrl();
        token = ((TinderTP) this.getApplication()).getToken();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotice,
                new IntentFilter(Common.MATCH_MSG_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onMatch,
                new IntentFilter(Common.MATCH_MATCH_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onPriorCall,
                new IntentFilter(Common.MSSG_READED_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onJsonNotice,
                new IntentFilter(Common.RAND_USER_KEY));

        ConnectionStruct conn = new ConnectionStruct(Common.PROFILE,Common.GET,url);
        Map<String,String> values = HeaderBuilder.forNewUser(user,token);
        newUserDownloader =  new NewUserDownloaderClient(this,findViewById(R.id.matchFragment),
                Common.RAND_USER_KEY, conn,values);
        newUserDownloader.runInBackground();
        isDownloading = true;

    }

    void locationConfig(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 900000, 200, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                locationConfig();
                break;
            default:
                break;
        }
    }

    public void onResume() {
        if (!haveSomeoneToMatch && !isDownloading){
            Log.i(MATCH_TAG,"Resumo actividad: busco nuevo match");
            isDownloading = true;
            newUserDownloader.runInBackground();
        }
        super.onResume();
    }


    private boolean hasNotification() {
        return DataThroughActivities.getInstance().hasMessages() ||
                DataThroughActivities.getInstance().hasMatches();
    }

    private void initalize(){

        onNotice = new ReceiverOnNewMessage(this);
        onMatch = new ReceiverOnNewMatch(this);
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        onMatch.setHaveMatch(preferences.getBoolean(Common.MATCH_KEY+user,false));
        onPriorCall = new ReceiverOnMssgReaded(this, onNotice);
        onJsonNotice = new ReceiverOnNewUserToMatch(this);
        haveSomeoneToMatch = false;
        isDownloading = false;

        if(DataThroughActivities.getInstance().hasMessages()) {
            Log.i(MATCH_TAG,"Se abrió nuevamente la apliacion y obtengo mensajes.");
            messages = new ArrayList<>(DataThroughActivities.getInstance().getMessages());
            users = new ArrayList<>(DataThroughActivities.getInstance().getUsers());
            onNotice.setNotificationCount(messages.size());
            Log.i(MATCH_TAG,"Notificaciones " + onNotice.getNotificationCount());
            invalidateOptionsMenu();
        } else if (ArraySerialization.hasPersistedMssg(getApplicationContext(),user)) {
            Log.i(MATCH_TAG,"Tiene mensajes persistidos.");
            messages = ArraySerialization.getPersistedArray(getApplicationContext(),user, "MSSG");
            users = ArraySerialization.getPersistedArray(getApplicationContext(),user, "USER");
            onNotice.setNotificationCount(messages.size());
            invalidateOptionsMenu();
        }

        if (messages == null && users == null) {
            messages = new ArrayList<>();
            users = new ArrayList<>();
        }

        onNotice.setUsersAndMessages(users, messages);
        onPriorCall.setUsersAndMessage(users, messages);

        bitmaps = null;
        imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageResource(RES_PLACEHOLDER);
    }

    public void sendResponse(String response, final String errorMessage) {
        ConnectionStruct conn = new ConnectionStruct(Common.MATCH, Common.POST, url);
        Map<String, String> headers = HeaderBuilder.forSendResponseMatch(user, token,
                emailUserMatch, response);

        final Context context = this;
        RequestResponseClient client = new RequestResponseClient(this, conn,headers) {

            @Override
            protected void getJson() throws IOException {}

            @Override
            protected void onPostExec() {
                if (badResponse || !isConnected) {
                    if (responseCode == Common.BAD_TOKEN) {
                        Log.d(MATCH_TAG, "Token vencido");
                        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                        String user = preferences.getString(Common.USER_KEY, "");
                        String pass = preferences.getString(Common.PASS_KEY, "");
                        String url = ((TinderTP) getApplication()).getUrl();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");

                        Map<String,String> values = HeaderBuilder.forLogin(user, pass, tokenGCM);
                        ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);
                        InfoDownloaderClient info = new InfoDownloaderClient(context,values,conn, findViewById(R.id.matchFragment),false);
                        info.runInBackground();
                    }else {
                        Log.i(MATCH_TAG, errorMessage);
                        showText(errorMessage);
                    }
                }else {
                    Log.i(MATCH_TAG,"Tengo un user para matchear");
                    setTitle("");
                    setImage(null,"");
                    newUserDownloader.runInBackground();
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(findViewById(R.id.matchFragment), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };
        client.runInBackground();
    }

    public void sendLike(View v) {
        if (haveSomeoneToMatch) {
            haveSomeoneToMatch = false;
            isDownloading = true;
            sendResponse(Common.LIKE_KEY, "No se puedo enviar el Like.");
        }
        else
            Snackbar.make(findViewById(R.id.matchFragment), "No tienes un usuario a quien calificar.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void sendDislike(View v) {
        if (haveSomeoneToMatch) {
            haveSomeoneToMatch = false;
            sendResponse(Common.DISLIKE_KEY, "No se puedo enviar el Dislike.");
        }
        else
            Snackbar.make(findViewById(R.id.matchFragment), "No tienes un usuario a quien calificar.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**Listener de boton Info (i) que va al perfil del usuario en vista. Solo si tiene la primer
     * imagen descargada, que es la del perfil.*/
    public void goToInformation(View v) {
        if (haveSomeoneToMatch){
            Intent profileAct = new Intent(getApplicationContext(), InfomationActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            MultiHashMap listDataChild = new MultiHashMap();
            ProfileInfo profileInfo = new ProfileInfo(jsonProfile,listDataChild);

            profileAct.putExtra(Common.COUNT,listDataChild.size());
            int i  = 0;
            for (String key : listDataChild.getKeys()) {
                profileAct.putExtra(Common.MAP_KEY+i,key);
                ArraySerialization.persistStringArrayinPref(this, key, listDataChild.get(key));
                i++;
            }

            profileAct.putExtra(Common.NAME_KEY, profileInfo.name);
            profileAct.putExtra(Common.ALIAS_KEY, profileInfo.alias);
            profileAct.putExtra(Common.AGE_KEY, profileInfo.age);
            profileAct.putExtra(Common.SEX_KEY, profileInfo.sex);

            SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Common.PHOTO_KEY, photoBase64);
            editor.apply();

            if (onNotice.getNotificationCount() != 0) {
                profileAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
                profileAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
            }

            this.startActivity(profileAct);
        } else
            Snackbar.make(findViewById(R.id.matchFragment), "Espere un momento o agregue nuevos intereses...",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void goToMesseges(View v) {

        if (NotificationIDs.getInstance().haveNotifications()) {
            NotificationManager notificationManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
            int notificationCount = NotificationIDs.getInstance().getSize();
            for (int i = 0; i < notificationCount; i++) {
                notificationManager.cancel(NotificationIDs.getInstance().getNotification(i));
            }
            NotificationIDs.getInstance().deleteNotifications();
        }

        Intent chatAct = new Intent(getApplicationContext(), ChatListActivity.class);
        chatAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (onNotice.getNotificationCount() != 0) {
            chatAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
            chatAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
            Log.i(MATCH_TAG, "Elimino los mensajes atraves de actividades.");
            DataThroughActivities.getInstance().deleteMssg();
        }

        onMatch.setHaveMatch(false);
        invalidateOptionsMenu();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Common.MATCH_KEY+user).apply();

        this.startActivity(chatAct);
    }

    public void goToFullScreen(View v) {
        if ( hasImage() ) {
            Intent fullScreen = new Intent(getApplicationContext(), FullScreenViewActivity.class);
            fullScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Common.PHOTO_KEY,photoBase64);
            editor.apply();
            startActivity(fullScreen);
        }
    }

    public boolean hasImage(){
        return bitmaps != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //muestra el menu.
        getMenuInflater().inflate(R.menu.matching, menu);

        MenuItem matchItem = menu.findItem(R.id.match_fire);
        MenuItemCompat.setActionView(matchItem, R.layout.fire_icon);

        RelativeLayout matchRelative = (RelativeLayout) MenuItemCompat.getActionView(matchItem);
        ImageView matchIcon = (ImageView)matchRelative.findViewById(R.id.fire_item);

        MenuItem mssgItem = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(mssgItem, R.layout.mssg_icon);

        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(mssgItem);
        ImageView mssgIcon = (ImageView)notifCount.findViewById(R.id.img);
        TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);

        if (onMatch.haveMatch()) {
            Log.i(MATCH_TAG,"Nuevo match view update");
            matchIcon.setImageResource(R.drawable.match_fire);
        } else {
            Log.i(MATCH_TAG,"No tiene nuevos match");
            matchIcon.setImageResource(R.drawable.match_no_fire);
        }

        if(onNotice.getNotificationCount() != 0) {
            Log.i(MATCH_TAG,"Actualizo la cantidad de mensajes");
            mssgIcon.setImageResource(R.drawable.new_msgg);
            tv.setText("+" + onNotice.getNotificationCount());
        } else {
            if (onPriorCall.areMessagesReaded()) {
                Log.i(MATCH_TAG, "Todos los mensajes leidos");
                mssgIcon.setImageResource(R.drawable.empty_msg);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ActivityStarter.start(getApplicationContext(), UrlActivity.class);
            return true;
        } else if (id == R.id.action_logout ) {
            Common.clearLoginSaved(getApplicationContext());
            ActivityStarter.startClear(getApplicationContext(), SelectLoginOrRegistryActivity.class);
            finish();
            return true;
        }else if (id == R.id.badge) {
            ActivityStarter.start(getApplicationContext(), ChatListActivity.class);
            return true;
        }else if (id == R.id.change_pic) {
            selectImageProfile();
            return true;
        }else if (id == R.id.my_profile) {
            Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
            profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profile.putExtra(Common.EMAIL_KEY, user);
            startActivity(profile);
        }else if (id == R.id.edit_profile) {
            ActivityStarter.start(getApplicationContext(),EditProfileActivity.class);
        }else if (id == R.id.delete_profile) {
            ActivityStarter.start(getApplicationContext(),AreYouSureActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectImageProfile() {
        Log.i(ADD_PIC_TAG, "seleccion imagen");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(ADD_PIC_TAG, "Resultado onActivityResult");
        if (resultCode == RESULT_OK) {
            Log.i(ADD_PIC_TAG, "onActivityResult OK");
            if (requestCode == SELECT_PICTURE) {
                Log.i(ADD_PIC_TAG, "onActivityResult select picture");
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Log.i(ADD_PIC_TAG, "Obtengo dato" + selectedImagePath);

                final File myImageFile = new File(selectedImagePath);

                Bitmap myBitmap = ImageBase64.decodeSampledBitmap(myImageFile.getAbsolutePath(),getWindowManager().getDefaultDisplay());

                String picBase64 = ImageBase64.encodeToBase64(myBitmap, Bitmap.CompressFormat.JPEG);

                if (Common.bytesToMeg(picBase64.length()) < Common.MAX_MEGAS_PIC) {

                    ConnectionStruct conn = new ConnectionStruct(Common.PICTURE, Common.PUT, url);
                    Map<String, String> headers = HeaderBuilder.forNewUser(user, token);

                    final Context context = this;
                    RequestResponseClient client = new RequestResponseClient(this, conn, headers) {
                        @Override
                        protected void getJson() throws IOException {}

                        @Override
                        protected void onPostExec() {
                            if (!badResponse && isConnected) {
                                showText("Foto actualizada.");
                            }else if (responseCode == Common.BAD_TOKEN) {
                                Log.d(MATCH_TAG, "Token vencido");
                                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                                String user = preferences.getString(Common.USER_KEY, "");
                                String pass = preferences.getString(Common.PASS_KEY, "");
                                String url = ((TinderTP) getApplication()).getUrl();
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");

                                Map<String,String> values = HeaderBuilder.forLogin(user, pass, tokenGCM);
                                ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);
                                InfoDownloaderClient info = new InfoDownloaderClient(context,values,conn, findViewById(R.id.matchFragment),false);
                                info.runInBackground();
                            }else {
                                showText("No se pudo conectar con el server.");
                            }
                        }

                        @Override
                        protected void showText(String message) {
                            Common.showSnackbar(findViewById(R.id.matchFragment), message);
                        }
                    };
                    client.addBody(picBase64);
                    client.runInBackground();
                } else {
                    Common.showSnackbar(findViewById(R.id.matchFragment), "Imagen muy grande, seleccione otra.");
                }
            }
        }
    }

    public String getPath(Uri uri) {
        Log.i(MATCH_TAG,"Obtengo path de la foto");
        if( uri == null ) {
            Common.showSnackbar(findViewById(R.id.matchFragment), "Error en la subida");
            Log.e(MATCH_TAG,"Error en la uri");
            return null;
        }
        //galeria
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.i(MATCH_TAG, "Cursor != null en la subida de imagenes");
            return cursor.getString(column_index);
        }

        Log.i(MATCH_TAG, "Resuelvo path final");
        return uri.getPath();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void setImage(Bitmap bitmap, String photo) {
        Log.i(MATCH_TAG,"set image");
        bitmaps = bitmap;
        imgView.setImageBitmap(bitmap);
        photoBase64 = photo;
    }

    public void storeToProfile(String json) {
        jsonProfile = json;
    }

    @Override
    public ConnectivityManager getConectivityManager() {
        return (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void saveEmailPossibleMatch(String email) {
        this.emailUserMatch = email;
    }

    public void setHaveSomeoneToMatch(boolean have) {
        haveSomeoneToMatch = have;
        isDownloading = have;
    }
}