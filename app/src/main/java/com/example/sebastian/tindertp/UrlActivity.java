package com.example.sebastian.tindertp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class UrlActivity extends AppCompatActivity {

    TextView mText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void sendRequest(View view) {
        EditText mEdit = (EditText)findViewById(R.id.editText);
        /*mText = (TextView)findViewById(R.id.textView);

        String url = mEdit.getText().toString();
        InfoDownloaderClient infoDownloader = new InfoDownloaderClient(mText,this,url);
        infoDownloader.runInBackground();*/
        //TODO: Hacer LoginRegistryActivity y q elija entre registrarse o loguearse
        //      Si va a registrarse y ya estaba registrado te manda a Login, sino todo bien
        //      Si va a login y no estaba registrado te manda a Registry, sino todo bien
        // Por ahora solo login
        String str = "http://192.168.0.17:8000";
        ((TinderTP)this.getApplication()).setUrl(mEdit.getText().toString());
        Intent logRegAct = new Intent(this, SelectLoginOrRegistryActivity.class);
        logRegAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(logRegAct);
        this.finish();

    }

    public void salir(View view) {
        this.finish();
        System.exit(0);
    }
}
