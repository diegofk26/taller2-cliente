package com.example.sebastian.tindertp.internetTools;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class InterestsInfoDownloader extends MediaDownloader{

    private View view;
    private Context context;
    private Map<String,String> values;
    private ConnectionStruct connectionStruct;

    private String jsonResponse;

    public InterestsInfoDownloader( Context context, Map<String, String> values, ConnectionStruct conn, View view) {
        //Connection vars
        this.path = conn.path;
        connectionStruct = conn;

        this.view = view;
        this.context = context;
        this.values = values;
        //initialize
        isConnected = true;
    }
    public void showText(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    void initSpecificVar() {
    }

    @Override
    void connect() throws IOException {
        Log.i(CONNECTION, "Connection with " + connectionStruct.URL + path);
        connection.setReadTimeout(timeOUT_R /* milliseconds */);
        connection.setConnectTimeout(timeOUT_C /* milliseconds */);
        connection.setRequestMethod(connectionStruct.requestMethod);
        Log.i(CONNECTION, "Set request method");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        Log.i(CONNECTION, "connecting...");
        // Starts the query
        connection.connect();
        Log.i(CONNECTION, "Conect ");

        int response = connection.getResponseCode();
        Log.i(CONNECTION, "" + response);
        if ( response < 300 && response >= 200 ){
            jsonResponse = readIt();
            isConnected = true;
        }else {
            isConnected = false;
        }
    }

    protected String readIt() throws IOException {
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
        if(isConnected) {
            Intent activityMsg = new Intent(Common.INTERESTS);
            activityMsg.putExtra("json", jsonResponse);
            LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
        } else {
            showText("No se pudo conectar al server y obtener Intereses.");
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(connectionStruct.URL+path);
        } else {
            showText("No hay conexión de red disponible.");
        }
    }
}
