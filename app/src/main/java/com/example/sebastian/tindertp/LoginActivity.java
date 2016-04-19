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
import android.widget.TextView;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText passText;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passText = (EditText) findViewById(R.id.editText3);
        checkBox = (CheckBox) findViewById(R.id.checkBox2);

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

    private boolean userOrPassAreEmpty( String user, String password,TextView message ) {
        if ( user.isEmpty() || password.isEmpty()) {
            message.setText("Algunos campos estan vacios.");
            return true;
        } else
            return false;
    }

    private boolean userOrPassTooLong(String user, String pass,TextView message){
        if (user.length() > Common.MAX_CHARS || pass.length() > Common.MAX_CHARS) {
            message.setText("Algunos campos superan los " + Common.MAX_CHARS + " caracteres.");
            return true;
        } else if(user.length() < Common.MIN_CHARS || pass.length() < Common.MIN_CHARS) {
            message.setText("Algunos campos no superan los " + Common.MIN_CHARS + " caracteres.");
            return true;
        } else
            return false;
    }

    private boolean userAndPass_OK(EditText user, EditText password){
        String us = user.getText().toString();
        String pass = password.getText().toString();
        TextView text = (TextView) findViewById(R.id.textView7);
        return ( !userOrPassAreEmpty(us,pass,text) && !userOrPassTooLong(us,pass,text) );
    }

    public void loginL(View v) {
        EditText user = (EditText)findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText3);
        TextView text = (TextView) findViewById(R.id.textView7);

        if ( userAndPass_OK(user,password) ) {

            Map<String, String> values = new HashMap<String, String>();
            values.put(Common.USER_KEY, user.getText().toString());
            values.put(Common.PASS_KEY, password.getText().toString());

            String url = ((TinderTP) this.getApplication()).getUrl();

            if (!url.isEmpty()) {

                InfoDownloaderClient info = new InfoDownloaderClient(text, this, url, Common.LOGIN, values);
                info.runInBackground();

            } else {
                Intent main = new Intent(this, UrlActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(main);
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
            Intent main = new Intent(this, UrlActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(main);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
