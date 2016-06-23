package com.example.sebastian.tindertp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.internetTools.ConnectionTester;
import com.example.sebastian.tindertp.services.OnGCMRegistrationComplite;
import com.example.sebastian.tindertp.services.RegistrationIntentService;

import java.util.ArrayList;

/**Actividad Launcher. Splash Screen. Salta las actividades que puede esquivar, como UrlActivity,
 * SelectLoginOrRegisterAcivity, LoginActivity o RegistryActivity*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        OnGCMRegistrationComplite onRegistComplete = new OnGCMRegistrationComplite(this);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onRegistComplete,
                new IntentFilter(Common.REGIST_COMPLETE));

        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        startService(intent);

        WebView gifV = (WebView)findViewById(R.id.webView);
        WebView dots = (WebView) findViewById(R.id.webView2);
        gifV.setBackgroundColor(Color.WHITE);
        dots.setBackgroundColor(Color.TRANSPARENT);
        gifV.loadUrl(Common.IA);
        gifV.reload();
        dots.loadUrl(Common.DOTS);
        dots.reload();
    }

}
