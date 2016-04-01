package com.example.sebastian.tindertp.internetTools;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.sebastian.tindertp.UrlActivity;
import com.example.sebastian.tindertp.RegistryActivity;
import com.example.sebastian.tindertp.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class TestConnectionClient extends MediaDownloader {

    private Context context;
    private String url;
    private String contentAsString;
    public static final int LEN = 25;

    public TestConnectionClient( Context context, String url) {
        this.url= url;
        this.context = context;
        contentAsString = "";
        isConnected = true;
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader;
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
        connection.setDoOutput(false);

        // Starts the query
        connection.connect();
        int response = connection.getResponseCode();

        is = connection.getInputStream();
        // Convert the InputStream into a string
        contentAsString = readIt(is, LEN);
    }

    @Override
    void closeConnection() throws IOException {
        if (is != null) {
            is.close();
        }
    }

    @Override
    void onPostExec() {

        if ( !contentAsString.equals("") && isConnected) {
            Intent registry = new Intent(context, RegistryActivity.class);
            registry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(registry);
            ((MainActivity) context).finish();
        } else {
            Intent main = new Intent(context, UrlActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(main);
            ((MainActivity) context).finish();
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadInBackground(this).execute(url);
        }else {
            Intent main = new Intent(context, UrlActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(main);
            ((MainActivity)context).finish();
        }
    }
}
