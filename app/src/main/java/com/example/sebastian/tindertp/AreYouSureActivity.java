package com.example.sebastian.tindertp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import java.io.IOException;
import java.util.Map;

public class AreYouSureActivity extends AppCompatActivity implements ConectivityManagerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_you_sure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void goToNo(View view) {
        finish();
    }

    public void goToYes(View view) {

        String url = ((TinderTP) getApplication()).getUrl();
        String userEmail = ((TinderTP) getApplication()).getUser();

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String pass = preferences.getString(Common.PASS_KEY, "");

        if (!pass.isEmpty() && !url.isEmpty()) {

            ConnectionStruct conn = new ConnectionStruct(Common.REMOVE, Common.DELETE, url);
            Map<String, String> headers = HeaderBuilder.forUnRegister(userEmail, pass);

            RequestResponseClient client = new RequestResponseClient(this, conn, headers) {
                @Override
                protected void getJson() throws IOException {
                }

                @Override
                protected void onPostExec() {
                    if (!badResponse && isConnected) {
                        showText("Usuario eliminado exitosamente.");
                        if (ArraySerialization.hasPersistedMssg(getApplicationContext())) {
                            ArraySerialization.deleteAll(getApplicationContext());
                        }
                        Common.clearLoginSaved(getApplicationContext());
                        ActivityStarter.startClear(getApplicationContext(), RegistryActivity.class);
                    } else {
                        showText("No se pudo eliminar el perfil. Intente mas tarde.");
                    }
                }

                @Override
                protected void showText(String message) {
                    Common.showSnackbar(findViewById(R.id.sure_relative), message);
                }
            };
            client.runInBackground();
        }
    }

    @Override
    public ConnectivityManager getConectivityManager() {
        return (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
