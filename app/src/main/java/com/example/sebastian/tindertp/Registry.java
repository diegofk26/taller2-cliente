package com.example.sebastian.tindertp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

public class Registry extends AppCompatActivity {

    private EditText passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
