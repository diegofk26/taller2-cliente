package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;

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
    private View view;
    private String filter;

    private String jsonString;


    public NewUserDownloaderClient(Context ctx, View v, String intentFilter, ConnectionStruct conn, Map<String,String> values) {
        this.conn = conn;
        this.values = values;
        filter = intentFilter;
        badResponse = false;
        view = v;
        context = ctx;
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

        responseCode = connection.getResponseCode();
        if ( responseCode < 300 && responseCode >= 200 ){
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

        if (!badResponse && isConnected && !jsonString.isEmpty()) {
                Intent activityMsg = new Intent(filter);
                activityMsg.putExtra("json", jsonString);
                LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
        }else {
            if (responseCode == Common.BAD_TOKEN) {
                SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
                String user = preferences.getString(Common.USER_KEY, "");
                String pass = preferences.getString(Common.PASS_KEY, "");
                String url = ((TinderTP) ((Activity)context).getApplication()).getUrl();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");

                Map<String,String> values = HeaderBuilder.forLogin(user, pass, tokenGCM);
                ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);
                InfoDownloaderClient info = new InfoDownloaderClient(context,values,conn, view,false);
                info.runInBackground();

            } else if (jsonString != null && jsonString.isEmpty()) {
                showText("No se encontraron usuarios compatibles.");
                if (context.getClass().getSimpleName().equals(MatchingActivity.class.getSimpleName())) {
                    ((MatchingActivity) context).setHaveSomeoneToMatch(false);
                }
                ((Activity) context).setTitle("");
            }else {
                if (context.getClass().getSimpleName().equals(MatchingActivity.class.getSimpleName())) {
                    ((MatchingActivity) context).setHaveSomeoneToMatch(false);
                }
                showText("No se pudo descargar Usuarios.");
            }
        }

    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadInBackground(this).execute(conn.URL+conn.path);

        } else {

            showText("No hay conección disponible.");
        }
    }

    @Override
    void showText(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
