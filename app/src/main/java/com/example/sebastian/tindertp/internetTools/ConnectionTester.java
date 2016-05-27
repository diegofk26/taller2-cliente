package com.example.sebastian.tindertp.internetTools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.SelectLoginOrRegistryActivity;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.UrlActivity;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;

import java.io.IOException;
import java.util.Map;
/**Conexion rapida sin Loguear para verificar que el server este activo y asi poder
 * saltar algunas activities.*/
public class ConnectionTester extends MediaDownloader {

    public static final String NO_NAME = "NO_NAME";
    public static final String NO_PASS = "NO_PASS";

    private Context context;
    private String url;
    private String contentAsString;

    public ConnectionTester(Context context, String url, String path) {
        this.url= url;
        this.path = path;
        this.context = context;
        contentAsString = "";
        isConnected = true;
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
        if (response == 200) {
            contentAsString = "Conexión exitosa.";
        }
    }

    @Override
    void closeConnection() throws IOException {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private void login(String user, String password){

        Map<String,String> values = HeaderBuilder.forRegister(user, password);

        MainActivity main = ((MainActivity)context);

        String url = ((TinderTP) main.getApplication()).getUrl();

        TextView text = (TextView) main.findViewById(R.id.textView8);

        ConnectionStruct conn = new ConnectionStruct(Common.LOGIN,Common.GET,url);

        InfoDownloaderClient info = new InfoDownloaderClient(text,context,values,conn);

        info.runInBackground();
    }

    @Override
    void onPostExec() {

        MainActivity main = (MainActivity) context;

        if ( !contentAsString.equals("") && isConnected) {

            String urlSaved = verifyHTTPFormat(url);
            ((TinderTP) main.getApplication()).setUrl(urlSaved);

            SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
            String user = preferences.getString(Common.USER_KEY, NO_NAME);
            String pass = preferences.getString(Common.PASS_KEY, NO_PASS);

            if ( user.equals(NO_NAME) || pass.equals(NO_PASS) ) {
                ActivityStarter.start(context, SelectLoginOrRegistryActivity.class);
                ((Activity) context).finish();
            } else {
                login(user,pass);
            }
        } else {
            ActivityStarter.start(context, UrlActivity.class);
            ((MainActivity) context).finish();
        }
    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadInBackground(this).execute(url+path);
        }else {
            ActivityStarter.start(context, UrlActivity.class);
            ((MainActivity)context).finish();
        }
    }
}
