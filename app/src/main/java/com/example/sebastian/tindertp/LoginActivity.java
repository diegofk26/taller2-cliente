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

    private boolean userOrPassAreEmpty( EditText user, EditText password) {
        String userText = user.getText().toString();
        String passText = password.getText().toString();
        return userText.isEmpty() || passText.isEmpty();
    }

    public void loginL(View v) {
        EditText user = (EditText)findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText3);
        TextView text = (TextView) findViewById(R.id.textView7);

        if ( !userOrPassAreEmpty(user,password) ) {

            Map<String, String> values = new HashMap<String, String>();
            values.put("Usuario", user.getText().toString());
            values.put("Password", password.getText().toString());

            String url = ((TinderTP) this.getApplication()).getUrl();

            if (!url.isEmpty()) {

                InfoDownloaderClient info = new InfoDownloaderClient(text, this, url, "/login", values);

                info.runInBackground();
            } else {
                Intent main = new Intent(this, UrlActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(main);
                this.finish();
            }
        } else {
            text.setText("Campos vacios.");
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
