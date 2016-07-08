package com.example.sebastian.tindertp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;

import java.util.Map;
/**Actividad de logeo en el sistema.*/
public class LoginActivity extends AppCompatActivity {

    private EditText passText;/*!< Campo de Password. */
    private CheckBox checkBox;/**< Chech box para mostrar o enmascarar la contraseña.*/
    private static final String LOGIN_TAG = "LoginActivity";

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

    /**Listener del boton Login. Si los campos de entrada son validos se procede hacer el logeo.
     * Si no hay URL seteada se llama a UrlActivity, terminando la actual.*/
    public void loginL(View v) {
        String user = ((EditText)findViewById(R.id.editText2)).getText().toString();
        EditText password = (EditText) findViewById(R.id.editText3);
        StringBuilder response = new StringBuilder();

        boolean userEmpty = user.isEmpty();

        if (userEmpty) {
            response.append("El campo usuario está vacío. ");
        }

        if ( Common.pass_OK(password, response) && !userEmpty) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");
            Map<String, String> values = HeaderBuilder.forLogin(
                    user, passText.getText().toString(), tokenGCM);

            String url = ((TinderTP) this.getApplication()).getUrl();

            if (!url.isEmpty()) {
                Log.i(LOGIN_TAG,"Listo para loguearse");
                ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);
                InfoDownloaderClient info = new InfoDownloaderClient( this, values,conn,
                        findViewById(R.id.login_relative), true );
                info.runInBackground();

            } else {
                Log.i(LOGIN_TAG,"No hay url guardada anteiormente");
                ActivityStarter.startClear(this, UrlActivity.class);
                this.finish();
            }
        } else {
            Log.i(LOGIN_TAG,response.toString());
            Snackbar.make(findViewById(R.id.login_relative), response.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
