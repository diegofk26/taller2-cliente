package com.example.sebastian.tindertp;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;

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

        EditText user = (EditText) findViewById(R.id.email_text);
        TextView text = (TextView) findViewById(R.id.textView9);

        if( Common.userAndPass_OK(user, passText, text) ) {

            Intent interestsAct = new Intent(this, InterestsActivity.class);
            interestsAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            interestsAct.putExtra(Common.USER_KEY, user.getText().toString());
            interestsAct.putExtra(Common.PASS_KEY, passText.getText().toString());
            startActivity(interestsAct);
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