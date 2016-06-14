package com.example.sebastian.tindertp.internetTools;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.ConnectionStruct;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class NewUserDownloaderClient extends MediaDownloader{

    private final String TAG = this.getClass().getSimpleName();

    private boolean badResponse;

    private Context context;
    private ConnectionStruct conn;
    private Map<String,String> values;

    private String jsonString;

    TextView mText;


    public NewUserDownloaderClient(Context ctx, TextView mtext,ConnectionStruct conn, Map<String,String> values) {
        this.conn = conn;
        this.values = values;

        badResponse = false;

        context = ctx;
        this.mText = mtext;
    }

    @Override
    void initSpecificVar() {}

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
    void connect() throws IOException {

        connection.setReadTimeout(timeOUT_R /* milliseconds */);
        connection.setConnectTimeout(timeOUT_C /* milliseconds */);
        connection.setRequestMethod(conn.requestMethod);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        Log.i(CONNECTION, "Connecting");
        connection.connect();
        Log.i(CONNECTION, "Connected");

        int response = connection.getResponseCode();
        if ( response < 300 && response >= 200 ){
            jsonString = readIt();
            isConnected = true;
        }else {
            badResponse = true;
        }
    }

    @Override
    void closeConnection() throws IOException {
        Log.i(CONNECTION,"closing connection...");
        try {
            if (connection != null) {
                connection.disconnect();
                Log.i(CONNECTION,"Disconected");
            }
            if (is != null) {
                is.close();
                Log.i(CONNECTION,"is: closed");
            }
        } catch (Exception e) {
            Log.e(CONNECTION,e.toString());
            e.printStackTrace();
        }
    }

    @Override
    void onPostExec() {

        if (!badResponse && isConnected) {
                Intent activityMsg = new Intent("JSON");
                activityMsg.putExtra("json", jsonString);
                LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
        }else {
            Intent activityMsg = new Intent("JSON");
            Log.i("DOWN","termino mal");
            activityMsg.putExtra("json", jsonString);
            LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
            mText.setText("No se pudo descarga Usuarios.");
        }

    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadInBackground(this).execute(conn.URL+conn.path);

        } else {
            mText.setText("No network connection available.");
        }
    }

    @Override
    void showText(String message) {

    }


    /*public boolean downloadComplete() {
        return urlAdapter.downloadComplete();
    }*/
}
