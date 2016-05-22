package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.sebastian.tindertp.commonTools.Conn_struct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public abstract class RequestResponseClient extends MediaDownloader {

    protected Activity ctx;
    private Conn_struct conn;
    private Map<String, String> values;
    protected boolean badResponse;
    protected String jsonString;
    private String body;
    private boolean hasBody;

    public RequestResponseClient(Activity ctx, Conn_struct conn, Map<String, String> values) {
        jsonString = "";
        this.ctx = ctx;
        this.conn = conn;
        path = conn.path;
        this.values = values;
        badResponse = false;
        hasBody = false;
    }

    public void addBody(String body) {
        hasBody = true;
        this.body = body;


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

        if (hasBody){
            Log.i("asd","TIENE BODY");
            connection.setDoOutput(true);
            byte[] outputInBytes = body.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write( outputInBytes );
            os.close();
        } else {
            connection.setDoOutput(false);
        }

        Log.i(CONNECTION, "connecting...");
        connection.connect();
        Log.i(CONNECTION, "Conected ");

        int response = connection.getResponseCode();
        Log.i(CONNECTION, "" + response);
        if ( response < 300 && response >= 200 ){
            getJson();
            Log.i("devuelve",jsonString);
            //contentAsString = "Operación exitosa.";
            isConnected = true;
        }else {
           // contentAsString = "Fallo la operación.";
            badResponse = true;
        }
    }

    protected abstract void getJson() throws IOException;

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

    protected abstract void onPostExec();

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(conn.URL+path);
        } else {
            showText("No hay ninguna conexion de red.");
        }
    }
    protected abstract void showText(String message);
}
