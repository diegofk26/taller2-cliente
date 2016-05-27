package com.example.sebastian.tindertp;

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
import android.widget.TextView;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;

import java.util.Map;

public class RegistryActivity extends AppCompatActivity {

    private EditText passText;
    private CheckBox checkBox;

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
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    passText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    passText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    public void salir2(View view) {
        this.finish();
        System.exit(0);
    }

    public void toRegister(View v) {

        EditText user = (EditText) findViewById(R.id.email_text);
        TextView text = (TextView) findViewById(R.id.textView9);

        if( Common.userAndPass_OK(user, passText, text) ) {

            Map<String,String> values = HeaderBuilder.forRegister(
                    user.getText().toString(), passText.getText().toString());

            String url = ((TinderTP) this.getApplication()).getUrl();

            if (!url.isEmpty()) {
                ConnectionStruct conn = new ConnectionStruct(Common.REGISTER,Common.PUT,url);
                InfoDownloaderClient info = new InfoDownloaderClient(text, this, values, conn);
                info.runInBackground();

            } else {
                ActivityStarter.startClear(this, UrlActivity.class);
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ActivityStarter.startClear(this, UrlActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
