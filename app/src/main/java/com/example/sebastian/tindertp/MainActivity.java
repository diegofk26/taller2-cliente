package com.example.sebastian.tindertp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.internetTools.ConnectionTester;
import com.example.sebastian.tindertp.services.MyInstanceIDListenerService;
import com.example.sebastian.tindertp.services.RegistrationIntentService;

import java.util.ArrayList;

/**Actividad Launcher. Splash Screen. Salta las actividades que puede esquivar, como UrlActivity,
 * SelectLoginOrRegisterAcivity, LoginActivity o RegistryActivity*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        WebView gifV = (WebView)findViewById(R.id.webView);
        WebView dots = (WebView) findViewById(R.id.webView2);
        gifV.setBackgroundColor(Color.WHITE);
        dots.setBackgroundColor(Color.TRANSPARENT);
        gifV.loadUrl(Common.IA);
        gifV.reload();
        dots.loadUrl(Common.DOTS);
        dots.reload();


        if (getIntent().hasExtra(Common.MSSG_KEY)){
            Log.i("asd","Obtengo los EXTRAS del serive, pendingIntent");
            ArrayList<String> messages = getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            ArrayList<String> users = getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);
            Log.i("asd","size in MAIN" + messages.size());
            DataThroughActivities.getInstance().setMessages(users,messages);
        }

        if (getIntent().hasExtra(Common.MATCH_KEY)){
            boolean haveMatch = getIntent().getBooleanExtra(Common.MATCH_KEY,false);
            DataThroughActivities.getInstance().setMatches(haveMatch);
        }

        Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        startService(intent);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                String url = preferences.getString("url", Common.FAIL);

                if (!url.equals(Common.FAIL)) {
                    Log.i("test", "hay url " + url);
                    ConnectionTester testConn = new ConnectionTester(MainActivity.this, url, Common.TEST);
                    testConn.runInBackground();
                } else {
                    Log.i("test", "no hay url");
                    ActivityStarter.start(MainActivity.this, UrlActivity.class);
                    MainActivity.this.finish();
                }
            }
        }, 500);
    }

}
