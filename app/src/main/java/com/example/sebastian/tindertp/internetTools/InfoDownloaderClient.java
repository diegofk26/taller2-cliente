package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;

import com.example.sebastian.tindertp.LoginActivity;
import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.UrlActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class InfoDownloaderClient extends MediaDownloader {

    public static final String PREF_FILE_NAME = "mypreferences";

    public TextView text;
    private Context context;
    private String contentAsString;
    private String url;
    private Map<String,String> values;
    public static final int LEN = 500;
    private boolean loginFail;
    SharedPreferences.Editor editor;

    public InfoDownloaderClient(TextView text, Context context, String url,String path, Map<String,String> values) {
        this.url = url;
        this.path = path;
        this.text = text;
        this.context = context;
        this.values = values;
        contentAsString = "";
        isConnected = true;
        loginFail = false;
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

    }
    public void showText(String message) {
        contentAsString = message;
    }

    @Override
    void initSpecificVar() {
    }

    @Override
    void connect() throws IOException {
        connection.setReadTimeout(10000 /* milliseconds */);
        connection.setConnectTimeout(15000 /* milliseconds */);
        connection.setRequestMethod("GET");
        Log.i(CONNECTION, "Set request method");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.setDoOutput(false);

        Log.i(CONNECTION, "connecting...");
        // Starts the query
        connection.connect();
        Log.i(CONNECTION, "Conect ");

        int response = connection.getResponseCode();
        Log.i(CONNECTION, "" + response);
        if ( response == 200 ){
            contentAsString = "Operación exitosa.";
            savePreferencesLogin();
            isConnected = true;
        }else {
            contentAsString = "Fallo la operación.";
            loginFail = true;
        }

        savePreferencesUrl();
    }

    @Override
    void closeConnection() throws IOException {
        Log.i(CONNECTION, "Closing connection");
        if (is != null) {
            is.close();
            Log.i(CONNECTION,"Connection closed");
        }
    }

    private void savePreferencesLogin(){
        editor.putString("Usuario",values.get("Usuario"));
        editor.putString("Password",values.get("Password"));
        editor.apply();
    }

    private void savePreferencesUrl(){
        Log.i("SAVE", "Saving preferences.");

        String urlSaved = verifyHTTPFormat(url);
        editor.putString("url", urlSaved); // value to store
        editor.apply();
        Log.i("SAVE", "Preferences saved.");
    }

    private boolean isExecutedByMainActivity(){
        return context.getClass().getSimpleName().equals(MainActivity.class.getSimpleName());
    }

    private void startActivity(Class<?> newActivity){
        Intent activity = new Intent(context, newActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

    @Override
    void onPostExec() {
        if (!contentAsString.equals("")) {
            this.text.setText(contentAsString);
            Log.i(CONNECTION, contentAsString);
        }

        if(!loginFail) {
            if (isConnected) {
                Intent activity = new Intent(context, MatchingActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activity);
            }
            else {
                startActivity(UrlActivity.class);
                ((Activity) context).finish();
            }

        } else if (isExecutedByMainActivity()) {
            startActivity(LoginActivity.class);
            ((Activity) context).finish();
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(url+path);
        } else {
            text.setText("No network connection available.");
        }

    }


}
