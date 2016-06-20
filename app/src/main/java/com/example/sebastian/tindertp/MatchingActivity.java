package com.example.sebastian.tindertp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.NotificationIDs;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;
import com.example.sebastian.tindertp.services.ReceiverOnMssgReaded;
import com.example.sebastian.tindertp.services.ReceiverOnNewMatch;
import com.example.sebastian.tindertp.services.ReceiverOnNewMessage;
import com.example.sebastian.tindertp.services.ReceiverOnNewUserToMatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

//!Activity donde se matchean las personas.
public class MatchingActivity extends AppCompatActivity implements ConectivityManagerInterface{

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

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(" location", "Longitude" + location.getLongitude());
                Log.i(" location", "Latitude" + location.getLatitude());
                // TODO: enviar al server
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonLocation = new JSONObject();
                try {
                    jsonLocation.put(Common.LATITUDE_KEY,location.getLatitude());
                    jsonLocation.put(Common.LONGITUDE_KEY, location.getLongitude());
                    jsonObject.put(Common.LOCATION_KEY, jsonLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        locationConfig();

        initalize();

        if (hasNotification()){
            ActivityStarter.start(getApplicationContext(), ChatListActivity.class);
        }

        url = ((TinderTP) this.getApplication()).getUrl();
        user = ((TinderTP) this.getApplication()).getUser();
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

        locationManager.requestLocationUpdates("gps", 900000, 20, listener);
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


    private boolean hasNotification() {
        return DataThroughActivities.getInstance().hasMessages() ||
                DataThroughActivities.getInstance().hasMatches();
    }

    private void initalize(){

        onNotice = new ReceiverOnNewMessage(this);
        onMatch = new ReceiverOnNewMatch(this);
        onPriorCall = new ReceiverOnMssgReaded(this, onNotice);
        onJsonNotice = new ReceiverOnNewUserToMatch(this);
        haveSomeoneToMatch = false;

        if(DataThroughActivities.getInstance().hasMessages()) {
            Log.i(MATCH_TAG,"Se abrió nuevamente la apliacion y obtengo mensajes.");
            messages = new ArrayList<>(DataThroughActivities.getInstance().getMessages());
            users = new ArrayList<>(DataThroughActivities.getInstance().getUsers());
            onNotice.setNotificationCount(messages.size());
            Log.i(MATCH_TAG,"Notificaciones " + onNotice.getNotificationCount());
            invalidateOptionsMenu();
        } else if (ArraySerialization.hasPersistedMssg(getApplicationContext())) {
            Log.i(MATCH_TAG,"Tiene mensajes persistidos.");
            messages = ArraySerialization.getPersistedArray(getApplicationContext(), "MSSG");
            users = ArraySerialization.getPersistedArray(getApplicationContext(), "USER");
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

        RequestResponseClient client = new RequestResponseClient(this, conn,headers) {

            @Override
            protected void getJson() throws IOException {}

            @Override
            protected void onPostExec() {
                if (badResponse || !isConnected) {
                    showText(errorMessage);
                }else {
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
        if (haveSomeoneToMatch)
            sendResponse(Common.LIKE_KEY, "No se puedo enviar el Like.");
        else
            Snackbar.make(findViewById(R.id.matchFragment), "No tienes un usuario a quien calificar.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void sendDislike(View v) {
        if (haveSomeoneToMatch)
            sendResponse(Common.DISLIKE_KEY, "No se puedo enviar el Dislike.");
        else
            Snackbar.make(findViewById(R.id.matchFragment), "No tienes un usuario a quien calificar.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**Listener de boton Info (i) que va al perfil del usuario en vista. Solo si tiene la primer
     * imagen descargada, que es la del perfil.*/
    public void goToProfile(View v) {
        if (hasImage()){
            Intent profileAct = new Intent(getApplicationContext(), ProfileActivity.class);
            profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            profileAct.putExtra(Common.PROFILE_JSON, jsonProfile);
            if (onNotice.getNotificationCount() != 0) {
                profileAct.putStringArrayListExtra(Common.MSSG_KEY, messages);
                profileAct.putStringArrayListExtra(Common.USER_MSG_KEY, users);
            }

            this.startActivity(profileAct);
        } else
            Snackbar.make(findViewById(R.id.matchFragment), "No tienes un usuario.",
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

        this.startActivity(chatAct);
    }

    public void goToFullScreen(View v) {
        if ( hasImage() ) {
            Intent fullScreen = new Intent(getApplicationContext(), FullScreenViewActivity.class);
            fullScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fullScreen.putExtra(Common.IMG_KEY, photoBase64);
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
                Log.i(ADD_PIC_TAG, "getting data" + selectedImagePath);

                final File myImageFile = new File(selectedImagePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                String picBase64 = ImageBase64.encodeToBase64(myBitmap, Bitmap.CompressFormat.JPEG);

                System.out.println(picBase64);

                ConnectionStruct conn = new ConnectionStruct(Common.PICTURE, Common.PUT, url);
                Map<String, String> headers = HeaderBuilder.forNewUser(user, token);

                RequestResponseClient client = new RequestResponseClient(this,conn,headers) {
                    @Override
                    protected void getJson() throws IOException {}

                    @Override
                    protected void onPostExec() {
                        if (!badResponse && isConnected) {
                            showText("Foto actualizada.");
                        }else {
                            showText("No se pudo conectar con el server.");
                        }
                    }

                    @Override
                    protected void showText(String message) {
                        Snackbar.make(findViewById(R.id.matchFragment), message, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                };
                client.addBody(picBase64);
                client.runInBackground();

            }
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        Log.i("reg","getting data");
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            Log.i("reg","getting NADA");
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.i("reg", "getting cursor != null");
            return cursor.getString(column_index);
        }
        // this is our fallback here
        Log.i("reg", "getting final");
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
    }
}