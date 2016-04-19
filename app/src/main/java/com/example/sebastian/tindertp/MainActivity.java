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
import com.example.sebastian.tindertp.internetTools.TestConnectionClient;

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
                    TestConnectionClient testConn = new TestConnectionClient(MainActivity.this,url, Common.TEST);
                    testConn.runInBackground();
                }
                else {
                    Log.i("test", "no hay url");
                    Intent main = new Intent(MainActivity.this, UrlActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(main);
                    MainActivity.this.finish();
                }
            }
        }, 2900);




    }

}
