package com.example.sebastian.tindertp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;


public class UrlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void sendRequest(View view) {
        EditText mEdit = (EditText)findViewById(R.id.editText);
        ((TinderTP)this.getApplication()).setUrl(mEdit.getText().toString());
        ActivityStarter.start(this, SelectLoginOrRegistryActivity.class);
    }

    public void salir(View view) {
        moveTaskToBack(true);
    }
}
