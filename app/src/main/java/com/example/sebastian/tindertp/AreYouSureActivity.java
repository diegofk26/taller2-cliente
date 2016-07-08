package com.example.sebastian.tindertp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;
import com.example.sebastian.tindertp.services.LocationGPSListener;

import java.io.IOException;
import java.util.Map;

public class AreYouSureActivity extends AppCompatActivity implements ConectivityManagerInterface {

    private static final String SURE_TAG = "AreYouSureActivity";

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
        final String userEmail = ((TinderTP) getApplication()).getUser();

        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String pass = preferences.getString(Common.PASS_KEY, "");

        if (!pass.isEmpty() && !url.isEmpty()) {

            ConnectionStruct conn = new ConnectionStruct(Common.REMOVE, Common.DELETE, url);
            Map<String, String> headers = HeaderBuilder.forUnRegister(userEmail, pass);

            final Context context = this;
            RequestResponseClient client = new RequestResponseClient(this, conn, headers) {
                @Override
                protected void getJson() throws IOException {
                }

                @Override
                protected void onPostExec() {
                    if (!badResponse && isConnected) {
                        Log.i(SURE_TAG, "Usuario eliminado exitosamente.");
                        showText("Usuario eliminado exitosamente.");
                        if (ArraySerialization.hasPersistedMssg(getApplicationContext(),userEmail)) {
                            ArraySerialization.deleteAll(getApplicationContext(),userEmail);
                        }
                        Common.clearLoginSaved(getApplicationContext());
                        ActivityStarter.startClear(getApplicationContext(), RegistryActivity.class);
                    } else {
                        if (responseCode == Common.BAD_TOKEN) {
                            Log.d(SURE_TAG, "Token vencido");
                            SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                            String user = preferences.getString(Common.USER_KEY, "");
                            String pass = preferences.getString(Common.PASS_KEY, "");
                            String url = ((TinderTP) getApplication()).getUrl();
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");

                            Map<String,String> values = HeaderBuilder.forLogin(user, pass, tokenGCM);
                            ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);
                            InfoDownloaderClient info = new InfoDownloaderClient(context,values,conn, findViewById(R.id.sure_relative),false);
                            info.runInBackground();
                        }else {
                            Log.i(SURE_TAG, "No se pudo eliminar el perfil");
                            showText("No se pudo eliminar el perfil. Intente mas tarde.");
                        }
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
