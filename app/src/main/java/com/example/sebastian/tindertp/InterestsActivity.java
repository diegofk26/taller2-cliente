package com.example.sebastian.tindertp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.AdapterHashMap;
import com.example.sebastian.tindertp.commonTools.JsonArrayBuilder;
import com.example.sebastian.tindertp.commonTools.ViewIdGenerator;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestsActivity extends AppCompatActivity {


    private AdapterHashMap editTextMap;
    private Map<String,String> mapper;
    private Animation animationAplha;
    private LocationManager locationManager;
    private LocationListener listener;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        animationAplha = AnimationUtils.loadAnimation(this,R.anim.alpha);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude =  location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        editTextMap = new AdapterHashMap();
        buildMapper();
        locationConfig();
    }

    void locationConfig(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 2000, 0, listener);
    }

    private void buildMapper() {
        mapper = new HashMap<>();
        List<TextView> interets = new ArrayList<>();
        interets.add((TextView) findViewById(R.id.textView11));
        interets.add((TextView) findViewById(R.id.textView10));
        interets.add((TextView) findViewById(R.id.textView12));
        interets.add((TextView) findViewById(R.id.textView13));
        interets.add((TextView) findViewById(R.id.textView14));
        interets.add((TextView) findViewById(R.id.textView15));
        interets.add((TextView) findViewById(R.id.textView16));
        for (int i = 0; i < interets.size(); i++) {
            String text = interets.get(i).getText().toString();
            String category = text.substring(0,text.length()-2).toLowerCase();
            mapper.put(interets.get(i).getHint().toString(),category);
        }

    }

    public void goToRegister(View v) {

        if (getIntent().hasExtra(Common.USER_KEY) && getIntent().hasExtra(Common.PASS_KEY)) {
            String json = getIntent().getStringExtra("json");
            try {
                JSONObject jsonObject = new JSONObject(json);
                String userEmail = jsonObject.getString(Common.EMAIL_KEY);
                String pass = jsonObject.getString(Common.PASS_KEY);

                JSONArray jsonInterests = JsonArrayBuilder.buildInterests(editTextMap);
                jsonObject.put(Common.INTERESTS_KEY,jsonInterests);

                JSONObject jsonLocation = new JSONObject();
                jsonLocation.put(Common.LATITUDE_KEY,latitude);
                jsonLocation.put(Common.LONGITUDE_KEY, longitude);
                jsonObject.put(Common.LOCATION_KEY,jsonLocation);

                Map<String, String> values = HeaderBuilder.forNewUser(userEmail, pass);
                TextView text = (TextView) findViewById(R.id.textView17);
                String url = ((TinderTP) this.getApplication()).getUrl();

                if (!url.isEmpty()) {
                    ConnectionStruct conn = new ConnectionStruct(Common.REGISTER, Common.PUT, url);
                    InfoDownloaderClient info = new InfoDownloaderClient(text, this, values, conn);
                    info.addBody(jsonObject.toString());
                    info.runInBackground();

                } else {
                    ActivityStarter.startClear(this, UrlActivity.class);
                    this.finish();
                }
            }catch(JSONException e){}
        }
    }

    private EditText editText(int newID, int editTextID, String hint, TextView text, ImageView imgBelow) {

        EditText newEditText = new EditText(this);

        newEditText.setId(newID);
        newEditText.setHint(hint);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) text.getLayoutParams();
        textParams.addRule(RelativeLayout.BELOW, newEditText.getId());
        text.setLayoutParams(textParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_RIGHT, editTextID);
        params.addRule(RelativeLayout.LEFT_OF, imgBelow.getId());
        params.addRule(RelativeLayout.BELOW, editTextID);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        newEditText.setLayoutParams(params);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative);
        relative.addView(newEditText, params);

        return newEditText;
    }

    private EditText editText(int newID, int editTextID, String hint) {

        EditText newEditText = new EditText(this);

        newEditText.setId(newID);
        newEditText.setHint(hint);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        Button btnReg = (Button) findViewById(R.id.button8);
        RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) btnReg.getLayoutParams();
        btnParams.addRule(RelativeLayout.BELOW,newEditText.getId());
        btnReg.setLayoutParams(btnParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_RIGHT, editTextID);
        params.addRule(RelativeLayout.BELOW, editTextID);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        newEditText.setLayoutParams(params);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative);
        relative.addView(newEditText, params);

        return newEditText;
    }

    private int getIDfromRules( ViewGroup.LayoutParams params, int rule) {
        int[] editRules = ((RelativeLayout.LayoutParams) params).getRules();
        Log.i("asd", "RULE" + editRules[rule]);
        return editRules[rule];
    }

    private void setNewEditView(View v) {
        EditText editTextRightOf;

        int editID = getIDfromRules(v.getLayoutParams(), RelativeLayout.RIGHT_OF);

        editTextRightOf = (EditText) findViewById(editID);
        String hint = String.valueOf(editTextRightOf.getHint());

        if (editTextMap.hasKey(mapper.get(hint))) {
            editTextRightOf = editTextMap.getLast(hint);
        }

        EditText newEditText;

        if(v.getId() != R.id.imageView10) {

            int imgID = getIDfromRules(editTextRightOf.getLayoutParams(), RelativeLayout.LEFT_OF);
            ImageView imgBelow = (ImageView) findViewById(imgID);

            int editBelowID = getIDfromRules(imgBelow.getLayoutParams(), RelativeLayout.RIGHT_OF);
            EditText editTextBelow = (EditText) findViewById(editBelowID);

            int headerBelowID = getIDfromRules(editTextBelow.getLayoutParams(), RelativeLayout.BELOW);
            TextView text = (TextView) findViewById(headerBelowID);

            newEditText = editText(ViewIdGenerator.generateViewId(), editTextRightOf.getId(), hint, text, imgBelow);
        } else {
            newEditText = editText(ViewIdGenerator.generateViewId(), editTextRightOf.getId(), hint);
        }

        editTextMap.put(mapper.get(hint), newEditText);

        ImageView img = (ImageView) findViewById(v.getId());
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams)img.getLayoutParams();
        imgParams.addRule(RelativeLayout.ALIGN_TOP, newEditText.getId());
        imgParams.addRule(RelativeLayout.RIGHT_OF, newEditText.getId());
        imgParams.addRule(RelativeLayout.ALIGN_BOTTOM, newEditText.getId());
        img.setLayoutParams(imgParams);
    }

    public void more(final View v) {

        v.startAnimation(animationAplha);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setNewEditView(v);
            }
        },200);
    }
}
