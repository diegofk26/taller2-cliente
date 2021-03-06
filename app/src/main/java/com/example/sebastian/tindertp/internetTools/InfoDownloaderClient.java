package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.sebastian.tindertp.LoginActivity;
import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.UrlActivity;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class InfoDownloaderClient extends MediaDownloader {

    private View view;
    private Context context;
    private String contentAsString;
    private String url;
    private String requestMethod;
    private Map<String,String> values;
    private boolean loginFail;
    SharedPreferences.Editor editor;

    private boolean hasBody;
    private String jsonBody;

    private String user;
    private String token;
    private boolean goMatch;

    public InfoDownloaderClient( Context context, Map<String, String> values, ConnectionStruct conn,
                                 View view, boolean goMatch) {
        //Connection vars
        this.url = conn.URL;
        this.path = conn.path;
        this.requestMethod = conn.requestMethod;
        this.goMatch = goMatch;

        this.view = view;
        this.context = context;
        this.values = values;
        //initialize
        contentAsString = "";
        isConnected = true;
        loginFail = false;
        hasBody = false;
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

    }
    public void showText(String message) {
        contentAsString = message;
    }

    @Override
    void initSpecificVar() {
    }

    @Override
    void connect() throws IOException {
        Log.i(CONNECTION, "Connection with " + url + path);
        connection.setReadTimeout(timeOUT_R /* milliseconds */);
        connection.setConnectTimeout(timeOUT_C /* milliseconds */);
        connection.setRequestMethod(requestMethod);
        Log.i(CONNECTION, "Set request method");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        if (hasBody){
            connection.setDoOutput(true);
            byte[] outputInBytes = jsonBody.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write( outputInBytes );
            os.close();
        } else {
            connection.setDoOutput(false);
        }

        Log.i(CONNECTION, "connecting...");
        // Starts the query
        connection.connect();
        Log.i(CONNECTION, "Conect ");

        responseCode = connection.getResponseCode();
        Log.i(CONNECTION, "" + responseCode);
        if ( responseCode < 300 && responseCode >= 200 ){
            token = connection.getHeaderField(Common.TOKEN);
            if (values.containsKey(Common.USER_KEY)) {
                user = values.get(Common.USER_KEY);
            }
            contentAsString = "Operación exitosa.";
            savePreferencesLogin();
            isConnected = true;
        }else {
            contentAsString = "Fallo la operación.";
            loginFail = true;
        }

        savePreferencesUrl();
    }

    public void addBody(String json) {
        hasBody = true;
        jsonBody = json;
    }

    @Override
    void closeConnection() throws IOException {
        if (connection != null) {
            connection.disconnect();
            Log.i(CONNECTION,"Disconected");
        }
    }

    private void savePreferencesLogin(){
        editor.putString(Common.USER_KEY,values.get(Common.USER_KEY));
        editor.putString(Common.PASS_KEY,values.get(Common.PASS_KEY));
        editor.apply();
    }

    private void savePreferencesUrl(){
        Log.i("SAVE", "Saving preferences.");

        String urlSaved = verifyHTTPFormat(url);
        editor.putString(Common.URL_KEY, urlSaved); // value to store
        editor.apply();
        Log.i("SAVE", "Preferences saved.");
    }

    private boolean isExecutedByMainActivity(){
        return context.getClass().getSimpleName().equals(MainActivity.class.getSimpleName());
    }

    @Override
    void onPostExec() {
        if (!contentAsString.equals("")) {
            Snackbar.make(view, contentAsString, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Log.i(CONNECTION, contentAsString);
        }

        if(!loginFail) {
            if (isConnected) {

                if (path.equals(Common.REGISTER)){
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");
                    values.put(Common.TOKEN_GCM,tokenGCM);
                    ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET, url);
                    InfoDownloaderClient info = new InfoDownloaderClient(context, values, conn,view,true);
                    info.runInBackground();
                }else {
                    ((TinderTP) ((Activity) context).getApplication()).setToken(token);
                    ((TinderTP) ((Activity) context).getApplication()).setUser(user);
                    if (goMatch) {
                        ActivityStarter.startClear(context, MatchingActivity.class);
                    }else {
                        ((Activity) context).recreate();
                    }
                }
            }
            else {
                ActivityStarter.startClear(context, UrlActivity.class);
            }

        } else if (responseCode == Common.BAD_TOKEN) {
            Log.d(CONNECTION,"Token vencido.");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");
            values.put(Common.TOKEN_GCM,tokenGCM);
            ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET, url);
            InfoDownloaderClient info = new InfoDownloaderClient(context, values, conn,view,false);
            info.runInBackground();
        }else if (isExecutedByMainActivity()) {
            ActivityStarter.start(context, LoginActivity.class);
            ((Activity) context).finish();
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadInBackground(this).execute(url+path);
        } else {
            Snackbar.make(view, "No hay conexión de red disponible.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }
}
