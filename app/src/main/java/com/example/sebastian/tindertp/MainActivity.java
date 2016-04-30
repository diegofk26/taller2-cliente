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

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.internetTools.TestConnectionClient;
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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                String url = preferences.getString("url", Common.FAIL);

                if (!url.equals(Common.FAIL)) {
                    Log.i("test","hay url " + url);
                    TestConnectionClient testConn = new TestConnectionClient(MainActivity.this, url, Common.TEST);
                    testConn.runInBackground();
                }
                else {
                    Log.i("test", "no hay url");
                    Common.startActivity(MainActivity.this, UrlActivity.class);
                    MainActivity.this.finish();
                }
            }
        }, 500);




    }

}
