package com.example.sebastian.tindertp.commonTools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sebastian.tindertp.ChatListActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.SelectLoginOrRegistryActivity;
import com.example.sebastian.tindertp.UrlActivity;

/**Contiene principalmente todas las variables en strings necesarias para una conexcion,
 * headers y sus keys. Reglas para registrar usuarios.
 * */
public class Common {

    public static final String PREF_FILE_NAME = "mypreferences";

    public static final String FAIL = "fail";

    public static final String GET = "GET";             /**< Vars para la conexion url */
    public static final String PUT = "PUT";             /**< Vars para la conexion url */
    public static final String POST = "POST";           /**< Vars para la conexion url */

    public static final String TEST = "/test";          /**< Vars para la conexion url */
    public static final String LOGIN = "/login";        /**< Vars para la conexion url */
    public static final String REGISTER = "/registro";  /**< Vars para la conexion url */
    public static final String MESSAGES = "/mensajes";  /**< Vars para la conexion url */
    public static final String CHAT = "/chat";          /**< Vars para la conexion url */

    public static final String RESPONSE = "RESPONSE";
    public static final String JSON = "JSON";

    public static final String IA = "file:///android_asset/IA.gif";     /**< Vars para la ScreenSplash */
    public static final String DOTS = "file:///android_asset/dots.gif"; /**< Vars para la ScreenSplash */

    public static final String PROFILE_IMG_KEY = "profileFile";
    public static final String URL_KEY = "url";
    //Headers
    public static final String USER_KEY = "Usuario";    /**< Vars para la conexion url */
    public static final String PASS_KEY = "Password";   /**< Vars para la conexion url */
    public static final String USER1 = "Usuario1";      /**< Vars para la conexion url */
    public static final String USER2 = "Usuario2";      /**< Vars para la conexion url */
    public static final String DESDE = "Desde";         /**< Vars para la conexion url */
    public static final String CANT = "Cantidad";       /**< Vars para la conexion url */
    public static final String MAX_MESSAGES= "10";      /**< Vars para la conexion url */
    public static final String TOKEN = "Token";         /**< Vars para la conexion url */
    public static final String RECEPTOR = "Receptor";   /**< Vars para la conexion url */

    public static final String IMG_POS_KEY = "position";
    public static final String IMG_KEY = "images";
    public static final String NEW_IMG_POS_KEY = "newImagePosition";
    public static final String MSSG_KEY = "MSSG";
    public static final String USER_MSG_KEY = "USER_MSG";

    public static final int BUFF_SIZE = 8192;

    public static final int MAX_CHARS = 30;
    public static final int MIN_CHARS = 5;

    private static boolean userOrPassAreEmpty( String user, String password,TextView message ) {
        if ( user.isEmpty() || password.isEmpty()) {
            message.setText(R.string.empty_spaces);
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

    public static void clearLoginSaved(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(Common.USER_KEY).apply();
        preferences.edit().remove(Common.PASS_KEY).apply();
        Log.i("Clear", "Delete login preferences.");
    }

    public static boolean optionSelectedItem(MenuItem item, Context context) {

        //settings (URL for now) is started
        int id = item.getItemId();
        Log.i("acca", "item seleccionado: " + id + " <-> " + R.id.badge);
        if (id == R.id.action_settings) {
            ActivityStarter.start(context, UrlActivity.class);
            return true;
        } else if (id == R.id.action_logout ) {
            Common.clearLoginSaved(context);
            ActivityStarter.startClear(context, SelectLoginOrRegistryActivity.class);
            ((Activity) context).finish();
            return true;
        }else if (id == R.id.badge) {
            Log.i("acccaa","mensajes");
            ActivityStarter.start(context, ChatListActivity.class);
            return true;
        }

        return false;
    }

}
