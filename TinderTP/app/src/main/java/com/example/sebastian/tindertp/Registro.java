package com.example.sebastian.tindertp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import java.net.PasswordAuthentication;

public class Registro extends AppCompatActivity {
    private EditText nameText;
    private boolean editingName;
    private EditText emailText;
    private boolean editingEmail;
    private EditText passText;
    private boolean editingPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        passText = (EditText) findViewById(R.id.password_text);
        passText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passText.setTypeface(Typeface.DEFAULT);
                passText.setTransformationMethod(new PasswordTransformationMethod());
            }

        });
    }



}
