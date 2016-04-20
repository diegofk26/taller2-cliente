package com.example.sebastian.tindertp.commonTools;

import android.widget.EditText;
import android.widget.TextView;

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

    private static boolean userOrPassTooLong(String user, String pass,TextView message){
        if (user.length() > Common.MAX_CHARS || pass.length() > Common.MAX_CHARS) {
            message.setText("Algunos campos superan los " + Common.MAX_CHARS + " caracteres.");
            return true;
        } else if(user.length() < Common.MIN_CHARS || pass.length() < Common.MIN_CHARS) {
            message.setText("Algunos campos no superan los " + Common.MIN_CHARS + " caracteres.");
            return true;
        } else
            return false;
    }

    public static boolean userAndPass_OK(EditText user, EditText password,TextView text){
        String us = user.getText().toString();
        String pass = password.getText().toString();
        return ( !userOrPassAreEmpty(us,pass,text) && !userOrPassTooLong(us,pass,text) );
    }

}
