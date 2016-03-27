package com.example.sebastian.tindertp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sebastian on 27/03/16.
 */
public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

    private TextView text;

    private static final String CONNECTION = "Connection";

    public DownloadWebpageTask(TextView text){
        this.text = text;
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
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

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            //String urlTest= "http://maps.googleapis.com/maps/api/geocode/json?address=chicago&sensor=false";
            String URLCorrect = verifyHTTPFormat(myurl);

            URL url = new URL(URLCorrect);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            Log.i(CONNECTION, "connecting...");
            conn.setDoOutput(false);

            // Starts the query
            conn.connect();
            Log.i(CONNECTION, "Conect " );
            int response = conn.getResponseCode();
            Log.d(CONNECTION, "The response is: " + response);

            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. " + urls[0] + " may be invalid.";
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        this.text.setText(result);
    }
}
