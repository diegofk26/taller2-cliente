package com.example.sebastian.tindertp.commonTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sebastian.tindertp.LoginActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.SelectLoginOrRegistryActivity;
import com.example.sebastian.tindertp.UrlActivity;

public class Common {

    public static final String PREF_FILE_NAME = "mypreferences";

    public static final String FAIL = "fail";

    public static final String GET = "GET";
    public static final String PUT = "PUT";


    public static final String TEST = "/test";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/registro";

    public static final String IA = "file:///android_asset/IA.gif";
    public static final String DOTS = "file:///android_asset/dots.gif";

    public static final String PROFILE_IMG_KEY = "profileFile";
    public static final String URL_KEY = "url";
    public static final String USER_KEY = "Usuario";
    public static final String PASS_KEY = "Password";
    public static final String IMG_POS_KEY = "position";
    public static final String IMG_KEY = "images";
    public static final String NEW_IMG_POS_KEY = "newImagePosition";

    public static final int BUFF_SIZE = 8192;

    public static final int MAX_CHARS = 30;
    public static final int MIN_CHARS = 5;

    private static boolean userOrPassAreEmpty( String user, String password,TextView message ) {
        if ( user.isEmpty() || password.isEmpty()) {
            message.setText("Algunos campos estan vacios.");
            return true;
        } else
            return false;
    }

    private static boolean userOrPassLong (String user, String pass, TextView message){
        if (user.length() > Common.MAX_CHARS || pass.length() > Common.MAX_CHARS) {
            message.setText("Algunos campos superan los " + Common.MAX_CHARS + " caracteres.");
            return true;
        } else if(pass.length() < Common.MIN_CHARS) {
            message.setText("La contraseña no supera los " + Common.MIN_CHARS + " caracteres.");
            return true;
        } else
            return false;
    }

    public static boolean userAndPass_OK(EditText user, EditText password,TextView text){
        String us = user.getText().toString();
        String pass = password.getText().toString();
        return ( !userOrPassAreEmpty(us,pass,text) && !userOrPassLong(us, pass, text) );
    }

    public static void startActivity(Context context, Class<?> newActivity){
        Intent activity = new Intent(context, newActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

    public static void startClearTask(Context context, Class<?> newAct) {
        Intent activity = new Intent(context, newAct);
        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

    public static void clearLoginSaved(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(Common.USER_KEY).apply();
        preferences.edit().remove(Common.PASS_KEY).apply();
        Log.i("Clear", "Delete login preferences.");
    }

    public static boolean optionSelectedItem(MenuItem item, Context context) {

        //settings (URL for now) is started
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Common.startActivity(context, UrlActivity.class);
            return true;
        } else if (id == R.id.action_logout ) {
            Common.clearLoginSaved(context);
            Common.startClearTask(context, SelectLoginOrRegistryActivity.class);
            ((Activity) context).finish();
            return true;
        }

        return false;
    }

}
