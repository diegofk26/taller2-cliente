package com.example.sebastian.tindertp.commonTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ArraySerialization {

    private final static String PERSISTED_TAG = "Persisted Data";

    private static void setStringArrayinPref(Context ctx, String key, List<String> values) {
        Log.i(PERSISTED_TAG, "Guardo un array entero.");
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();

    }

    //pasa info entre activities
    public static void persistStringArrayinPref(Context ctx, String key, List<Object> values) {
        Log.i(PERSISTED_TAG, "Guardo un array entero.");
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put((String)values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();

    }

    public static void persistUserAndMssg(Context ctx, String userFrom, String mssg) {
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String user = preferences.getString(Common.USER_KEY, " ");

        pushStringinPref(ctx, user,"USER", userFrom);
        pushStringinPref(ctx, user,"MSSG", mssg);
        Log.i(PERSISTED_TAG, "Guardooo " + userFrom + " en service");
    }

    private static void pushStringinPref(Context context, String userProp, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<String> values = new ArrayList<>();
        if (preferences.contains(userProp+key)) {
            Log.i(PERSISTED_TAG,"Preferencias ya tienen datos guardados anteriomente");
            ArrayList<String> arrayList = getPersistedArray(context,userProp, key);
            arrayList.add(value);
            values.addAll(arrayList);
        } else {
            Log.i(PERSISTED_TAG,"preferencias no tienen datos guardados anteriomente");
                    values.add(value);
        }
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(userProp+key, a.toString());
        } else {
            editor.putString(userProp+key, null);
        }
        editor.apply();
    }

    public static void deleteAll(Context context, String userProp) {
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(PERSISTED_TAG,"Borro todos los mensajes.");
        editor.remove(userProp+"MSSG");
        editor.remove(userProp+"USER");
        if (hasPersistedMatches(context,userProp)) {
            Log.i(PERSISTED_TAG,"Borro todos los matches.");
            editor.remove(userProp+Common.MATCH_KEY);
        }
        editor.apply();
    }

    public static void deleteStringFromArray(Context context, String userProp, String userToRemove) {
        Log.i(PERSISTED_TAG,"Borro datos persistidos de " + userToRemove);
        ArrayList<String> messages = getPersistedArray(context, userProp,"MSSG");
        ArrayList<String> users = getPersistedArray(context, userProp,"USER");

        for (int i = users.size() -1; i >= 0; i--) {
            if (users.get(i).equals(userToRemove)) {
                users.remove(i);
                messages.remove(i);
            }
        }

        setStringArrayinPref(context, userProp+"USER", users);
        setStringArrayinPref(context, userProp+"MSSG", messages);

    }

    public static boolean hasPersistedMssg(Context ctx, String userProp) {
        Log.i(PERSISTED_TAG,"Pregunto si tiene mensajes persistidos");
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(userProp+"USER") && preferences.contains(userProp+"MSSG");
    }

    public static boolean hasPersistedMatches(Context ctx, String userProp) {
        Log.i(PERSISTED_TAG,"Pregunto si tiene matches persistidos");
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(userProp+Common.MATCH_KEY);
    }

    public static ArrayList<String> getPersistedArray(Context context, String useProp, String key) {

        Log.i(PERSISTED_TAG,"Obtengo datos con key: " +useProp+ key);
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(useProp+key, null);
        ArrayList<String> stringArrayList = new ArrayList<>();
        if (json != null) {
            Log.i(PERSISTED_TAG,"preparo JSON");
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    stringArrayList.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArrayList;
    }

    public static ArrayList<Object> getPersistedArrayObject(Context context, String key) {
        Log.i(PERSISTED_TAG,"Obtengo datos con key: " + key);
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(key, null);
        ArrayList<Object> stringArrayList = new ArrayList<>();
        if (json != null) {
            Log.i(PERSISTED_TAG,"preparo JSON");
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    stringArrayList.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArrayList;
    }

    public static void persistUserMatch(Context ctx, String userMatch) {
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String userProp = preferences.getString(Common.USER_KEY, " ");
        pushStringinPref(ctx, userProp,Common.MATCH_KEY, userMatch);
        Log.i(PERSISTED_TAG, "Guardo " + userMatch + " en service");
    }

    public static String getUserName(Context ctx, String userEmailMatch) {
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(userEmailMatch, null);

    }

    public static void persistUserMatch(Context ctx, String userEmailMatch, String userNameMatch) {
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userEmailMatch, userNameMatch);
        editor.apply();

        Log.i(PERSISTED_TAG, "Guardo " + userEmailMatch + "->" + userNameMatch + " en service");
    }

    public static void deleteArray(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
