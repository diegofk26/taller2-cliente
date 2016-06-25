package com.example.sebastian.tindertp.internetTools;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public abstract class RequestResponseClient extends MediaDownloader {

    private ConnectionStruct conn;
    private Map<String, String> values;
    protected boolean badResponse;
    protected String jsonString;
    private String body;
    ConnectivityManager connMgr;
    private boolean hasBody;

    public RequestResponseClient(ConectivityManagerInterface transfer, ConnectionStruct conn, Map<String, String> values) {
        jsonString = "";
        connMgr = transfer.getConectivityManager();
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

        connection.setReadTimeout(timeOUT_R /* milliseconds */);
        connection.setConnectTimeout(timeOUT_C /* milliseconds */);
        connection.setRequestMethod(conn.requestMethod);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        if (hasBody){
            connection.setDoOutput(true);
            byte[] outputInBytes = body.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write( outputInBytes );
            os.close();
        } else {
            connection.setDoOutput(false);
        }

        connection.connect();

        responseCode = connection.getResponseCode();
        if ( responseCode < 300 && responseCode >= 200 ){
            getJson();
            isConnected = true;
        }else {
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
        }
    }

    protected abstract void onPostExec();

    @Override
    public void runInBackground() {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(conn.URL+path);
        } else {
            showText("No hay ninguna conexion de red.");
        }
    }
    protected abstract void showText(String message);
}
