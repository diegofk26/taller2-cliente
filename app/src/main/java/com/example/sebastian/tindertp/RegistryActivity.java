package com.example.sebastian.tindertp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistryActivity extends AppCompatActivity {

    private EditText passText;
    private CheckBox checkBox;
    private RadioButton menrButton;
    private RadioButton womanrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passText = (EditText) findViewById(R.id.textPassword1);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // En cambios realizados
                if (!isChecked) {
                    // muestra password
                    passText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // oculta password
                    passText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        menrButton = (RadioButton) findViewById(R.id.radioButton);
        womanrButton = (RadioButton) findViewById(R.id.radioButton2);

        setOnCheckedChangeListener(menrButton);
        setOnCheckedChangeListener(womanrButton);

    }

    private RadioButton opposite(RadioButton rButton) {
        if (rButton.equals(menrButton)) {
            return womanrButton;
        } else {
            return menrButton;
        }
    }

    private void setOnCheckedChangeListener(final RadioButton rButton) {
        rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    RadioButton oposite = opposite(rButton);
                    oposite.setChecked(false);
                }
            }
        });
    }

    public void salir2(View view) {
        this.finish();
        System.exit(0);
    }

    public void goToInterests(View v) {

        String email = ((EditText) findViewById(R.id.email_text)).getText().toString();
        String alias = ((EditText) findViewById(R.id.alias)).getText().toString();
        String age = ((EditText) findViewById(R.id.age)).getText().toString();
        String name = ((EditText) findViewById(R.id.name)).getText().toString();

        boolean invalidAge;
        try {
            invalidAge = Integer.parseInt(age) > 150;
        }catch (NumberFormatException e) {
            invalidAge = true;
        }

        boolean wrongFields = email.isEmpty() || alias.isEmpty() || age.isEmpty() || invalidAge
                || name.isEmpty() || ( !womanrButton.isChecked() && !menrButton.isChecked());

        StringBuilder response =  new StringBuilder();

        if (wrongFields) {
            response.append("Algunos campos están vacíos o erroneos. ");
        }

        if( !wrongFields && Common.pass_OK(passText, response) ) {

            Intent interestsAct = new Intent(this, InterestsActivity.class);
            interestsAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Common.EMAIL_KEY, email);
                jsonObject.put(Common.PASS_KEY, passText.getText().toString());
                jsonObject.put(Common.ALIAS_KEY, alias);
                jsonObject.put(Common.AGE_KEY, Integer.parseInt(age));
                jsonObject.put(Common.NAME_KEY, name);
                if (menrButton.isChecked()){
                    jsonObject.put(Common.SEX_KEY, menrButton.getText().toString());
                }else {
                    jsonObject.put(Common.SEX_KEY, womanrButton.getText().toString());
                }
            }catch(JSONException e){}
            interestsAct.putExtra(Common.PROFILE_JSON,jsonObject.toString());
            startActivity(interestsAct);
        } else {
            if (invalidAge) {
                Common.showSnackbar(findViewById(R.id.scrollView1),"Ingrese una edad valida.");
            }else
                Snackbar.make(findViewById(R.id.scrollView1),response.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            ActivityStarter.startClear(this, UrlActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}