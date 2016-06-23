package com.example.sebastian.tindertp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.example.sebastian.tindertp.ExpandedListAdapters.ExpandableListAdapter;
import com.example.sebastian.tindertp.ExpandedListAdapters.MyScrollListener;
import com.example.sebastian.tindertp.Interfaces.CategoryUpdater;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.JsonArrayBuilder;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;
import com.example.sebastian.tindertp.internetTools.InterestsInfoDownloader;
import com.example.sebastian.tindertp.services.ReceiverOnGetInterests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestsActivity extends AppCompatActivity implements CategoryUpdater {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> categories;
    List<String> listDataHeader;
    MultiHashMap listDataChild;

    private Map<Integer,String> mapperID;
    private Map<String,String> categoryMapper;

    private LocationManager locationManager;
    private LocationListener listener;
    private boolean finishUpdate;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapperID = new HashMap<>();
        categoryMapper = new HashMap<>();
        longitude = 1;
        latitude = 1;

        ReceiverOnGetInterests getInterests = new ReceiverOnGetInterests(this,false);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(getInterests,
                new IntentFilter(Common.INTERESTS));

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        buildCategoryMapper();

        listDataHeader = new ArrayList<>();
        listDataChild = new MultiHashMap();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
        expListView.setOnScrollListener(new MyScrollListener(this));

        if (getIntent().hasExtra(Common.PROFILE_JSON)) {
            String json = getIntent().getStringExtra(Common.PROFILE_JSON);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String userEmail = jsonObject.getString(Common.EMAIL_KEY);
                String pass = jsonObject.getString(Common.PASS_KEY);

                Map<String, String> values = HeaderBuilder.forRegister(userEmail, pass);
                String url = ((TinderTP) this.getApplication()).getUrl();
                ConnectionStruct conn = new ConnectionStruct(Common.INTERESTS, Common.GET, url);
                InterestsInfoDownloader interests = new InterestsInfoDownloader(this, values, conn,
                        findViewById(R.id.relative));
                interests.runInBackground();

            }catch (JSONException e) {}
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude =  location.getLatitude();
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

        finishUpdate = false;

        locationConfig();
    }

    private void buildCategoryMapper() {
        categoryMapper.put(Common.FOOD,"Comida:");
        categoryMapper.put(Common.OUTDOORS,"Actividades:");
        categoryMapper.put(Common.TRAVEL,"Viajes:");
        categoryMapper.put(Common.SEX,"Sexo:");
        categoryMapper.put(Common.MUSIC,"Música:");
        categoryMapper.put(Common.MUSIC_BAND,"Bandas:");
        categoryMapper.put(Common.SPORT, "Deportes:");
    }

    void locationConfig(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 2500, 0, listener);
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


    public void goToRegister(View v) {

        if (getIntent().hasExtra(Common.PROFILE_JSON) && finishUpdate) {
            finishUpdate = false;
            String json = getIntent().getStringExtra(Common.PROFILE_JSON);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String userEmail = jsonObject.getString(Common.EMAIL_KEY);
                String pass = jsonObject.getString(Common.PASS_KEY);
                jsonObject.remove(Common.PASS_KEY);
                //intereses
                JSONArray jsonInterests = JsonArrayBuilder.buildInterests(categories, listAdapter);
                jsonObject.put(Common.INTERESTS_KEY,jsonInterests);
                //gps
                JSONObject jsonLocation = new JSONObject();
                jsonLocation.put(Common.LATITUDE_KEY,latitude);
                jsonLocation.put(Common.LONGITUDE_KEY, longitude);
                jsonObject.put(Common.LOCATION_KEY, jsonLocation);
                //agrega todo_ a un user
                JSONObject jsonFinal = new JSONObject();
                jsonFinal.put("user",jsonObject);

                Map<String, String> values = HeaderBuilder.forRegister(userEmail, pass);
                String url = ((TinderTP) this.getApplication()).getUrl();

                if (!url.isEmpty()) {
                    ConnectionStruct conn = new ConnectionStruct(Common.REGISTER, Common.PUT, url);
                    InfoDownloaderClient info = new InfoDownloaderClient(this, values, conn,
                            findViewById(R.id.relative));
                    info.addBody(jsonFinal.toString());
                    info.runInBackground();

                } else {
                    ActivityStarter.startClear(this, UrlActivity.class);
                    this.finish();
                }
            }catch(JSONException e){}
        }else {
            Common.showSnackbar(findViewById(R.id.relative), "Descargando categorias. Por favor espere...");
        }
    }

    @Override
    public void update(MultiHashMap categoryValues) {

        EditText edit = null;
        listDataHeader.clear();
        mapperID.clear();
        listDataChild.clear();

        categories = categoryValues.getKeysList();

        for(int i = 0; i < categories.size(); i++) {

            String category = categories.get(i);
            String categoryMod;
            if (categoryMapper.containsKey(category)) {
                categoryMod = categoryMapper.get(category);
            }else {
                categoryMod = category.substring(0, 1).toUpperCase() + category.substring(1) + ": ";
            }

            int categoryID = Integer.parseInt(""+(i+1)+""+0 );
            mapperID.put(categoryID,categoryMod);

            listDataHeader.add(categoryMod);
            listDataChild.put(categoryMod,edit);
        }

        listAdapter.setSuggestions(categories, categoryValues);
        listAdapter.addHeaders(listDataHeader);


        finishUpdate = true;
    }


    public void more(View view) {
        int ID = view.getId();

        int digitSize = String.valueOf(listDataHeader.size()).length();

        int categoryID = Integer.parseInt(("" + ID).substring(0, digitSize));
        int childIDprev = Integer.parseInt(("" + ID).substring(digitSize));
        int childIDnext = childIDprev + 1;
        int nextViewID = Integer.parseInt(""+categoryID+""+childIDnext);

        mapperID.put(nextViewID, mapperID.get(ID));
        EditText newEdit = null;
        listDataChild.put(mapperID.get(nextViewID), newEdit);
        listAdapter.addHeaders(listDataHeader);
    }
}
