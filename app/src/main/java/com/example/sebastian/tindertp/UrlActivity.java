package com.example.sebastian.tindertp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


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
