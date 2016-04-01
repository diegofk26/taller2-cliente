package com.example.sebastian.tindertp.internetTools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;

import com.example.sebastian.tindertp.RegistryActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class InfoDownloaderClient extends MediaDownloader {

    public static final String PREF_FILE_NAME = "mypreferences";

    public TextView text;
    private Context context;
    private String contentAsString;
    private String url;
    public static final int LEN = 500;

    public InfoDownloaderClient(TextView text, Context context, String url) {
        this.url = url;
        this.text = text;
        this.context = context;
        contentAsString = "";
        isConnected = true;

    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
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
        Log.i(CONNECTION, "connecting...");
        connection.setDoOutput(false);

        // Starts the query
        connection.connect();
        Log.i(CONNECTION, "Conect ");
        int response = connection.getResponseCode();
        Log.d(CONNECTION, "The response is: " + response);

        is = connection.getInputStream();
        // Convert the InputStream into a string
        contentAsString = readIt(is, LEN);
    }

    @Override
    void closeConnection() throws IOException {
        Log.i(CONNECTION,"Closing connection");
        if (is != null) {
            is.close();
            Log.i(CONNECTION,"Connection closed");
        }
    }

    private void savePreferences(){
        Log.i("SAVE","Saving preferences.");
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("url", nURL); // value to store
        editor.apply();
        Log.i("SAVE", "Preferences saved.");
    }

    @Override
    void onPostExec() {
        if ( !contentAsString.equals("") ) {
            this.text.setText(contentAsString);
            Log.i(CONNECTION, text.getText().toString());
            if (isConnected) {
                savePreferences();
                Intent registry = new Intent(context, RegistryActivity.class);
                registry.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(registry);
            }
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(url);
        } else {
            text.setText("No network connection available.");
        }

    }


}
