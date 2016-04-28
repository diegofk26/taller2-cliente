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

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.Conn_struct;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;
import java.util.HashMap;
import java.util.Map;
/**Actividad de logeo en el sistema.*/
public class LoginActivity extends AppCompatActivity {

    private EditText passText;/*!< Campo de Password. */
    private CheckBox checkBox;/**< Chech box para mostrar o enmascarar la contraseña.*/

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
        EditText user = (EditText)findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText3);
        TextView text = (TextView) findViewById(R.id.textView7);

        if ( Common.userAndPass_OK(user, password, text) ) {

            Map<String, String> values = new HashMap<String, String>();
            values.put(Common.USER_KEY, user.getText().toString());
            values.put(Common.PASS_KEY, password.getText().toString());

            String url = ((TinderTP) this.getApplication()).getUrl();

            if (!url.isEmpty()) {
                Conn_struct conn = new Conn_struct(Common.LOGIN,Common.GET,url);
                InfoDownloaderClient info = new InfoDownloaderClient(text, this, values,conn);
                info.runInBackground();

            } else {
                Intent main = new Intent(this, UrlActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
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
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(main);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
