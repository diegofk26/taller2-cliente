package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.Conn_struct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by sebastian on 21/05/16.
 */
public class RequestResponseClient extends MediaDownloader {

    private Activity ctx;
    private Conn_struct conn;
    private Map<String, String> values;
    private boolean badResponse;
    private String jsonString;

    public RequestResponseClient(Activity ctx, Conn_struct conn, Map<String, String> values) {
        jsonString = "";
        this.ctx = ctx;
        this.conn = conn;
        this.values = values;
        badResponse = false;
    }



    @Override
    void initSpecificVar() {

    }

    @Override
    void connect() throws IOException {

        Log.i(CONNECTION, "Connection with " + conn.URL + path);
        connection.setReadTimeout(timeOUT_R /* milliseconds */);
        connection.setConnectTimeout(timeOUT_C /* milliseconds */);
        connection.setRequestMethod(conn.requestMethod);
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
        if ( response < 300 && response >= 200 ){
            jsonString = readIt();
            //contentAsString = "Operación exitosa.";
            isConnected = true;
        }else {
           // contentAsString = "Fallo la operación.";
            badResponse = true;
        }
    }
    private String readIt() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    @Override
    void closeConnection() throws IOException {
        if (connection != null) {
            connection.disconnect();
            Log.i(CONNECTION,"Disconected");
        }
    }

    @Override
    void onPostExec() {
        if (!badResponse) {
            if (isConnected) {
                Intent activityMsg = new Intent(Common.RESPONSE);
                activityMsg.putExtra(Common.JSON, jsonString);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(activityMsg);
            }
        }

    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(conn.URL+path);
        } else {
           // text.setText("No network connection available.");
        }
    }

    @Override
    void showText(String message) {

    }
}
