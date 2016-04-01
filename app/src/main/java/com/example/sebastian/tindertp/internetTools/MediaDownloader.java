package com.example.sebastian.tindertp.internetTools;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MediaDownloader {

    protected InputStream is;
    protected HttpURLConnection connection;
    protected String nURL;
    protected boolean isConnected;

    public static final String CONNECTION = "Connection";

    public void setConnection(boolean state) {
        isConnected = state;
    }

    private boolean isHTTPFormat(String url) {
        Pattern pattern = Pattern.compile("^http://.*$");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    private String verifyHTTPFormat(String url) {
        if( !isHTTPFormat(url) ) {
            Log.d(CONNECTION, "Without http://");
            StringBuilder correctURL = new StringBuilder();
            correctURL.append( "http://" );
            correctURL.append( url );
            return correctURL.toString();
        }
        return url;
    }

    abstract void initSpecificVar();

    abstract void connect() throws IOException;

    abstract void closeConnection() throws IOException;

    abstract void onPostExec();

    public abstract void runInBackground();

    abstract void showText(String message);

    public void establishConnection(String url) throws IOException {
        is = null;
        connection = null;
        initSpecificVar();
        nURL = verifyHTTPFormat(url);
        try {
            Log.i(CONNECTION,"Starting url connection");
            URL urlOK = new URL(nURL);
            connection = (HttpURLConnection) urlOK.openConnection();
            Log.i(CONNECTION,"Starting url connection opened");
            connect();
        } finally {
            closeConnection();
        }
    }

}
