package com.example.sebastian.tindertp.commonTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ArraySerialization {

    private static void setStringArrayinPref(Context ctx, String key, List<String> values) {
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

    public static void persistUserAndMssg(Context ctx, String user, String mssg) {
        pushStringinPref(ctx,"USER",user);
        pushStringinPref(ctx, "MSSG", mssg);
        Log.i("asddd", "guardooo en service");
    }

    private static void pushStringinPref(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<String> values = new ArrayList<>();
        if (preferences.contains(key)) {
            Log.i("asd","Preferencias ya tienen mensajes guardados anteriomente");
            ArrayList<String> arrayList = getPersistedArray(context, key);
            arrayList.add(value);
            values.addAll(arrayList);
        } else {
            Log.i("asddd","preferencias no tienen mensajes guardados anteriomente");
            values.add(value);
        }
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

    public static void deleteStringFromArray(Context context, String userToRemove) {
        ArrayList<String> messages = getPersistedArray(context, "MSSG");
        ArrayList<String> users = getPersistedArray(context, "USER");

        for (int i = users.size() -1; i >= 0; i--) {
            if (users.get(i).equals(userToRemove)) {
                Log.i("asd","Borro "+ users.get(i));
                users.remove(i);
                messages.remove(i);
            }
        }

        setStringArrayinPref(context, "USER", users);
        setStringArrayinPref(context, "MSSG", messages);

    }

    public static boolean hasPersistedMssg(Context ctx) {
        Log.i("asddd","Pregunto si tiene mensajes persistidos");
        SharedPreferences preferences = ctx.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains("USER") && preferences.contains("MSSG");
    }

    public static ArrayList<String> getPersistedArray(Context context, String key) {
        Log.i("asddd","Obtengo mensajes");
        SharedPreferences preferences = context.getSharedPreferences(Common.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(key, null);
        ArrayList<String> stringArrayList = new ArrayList<>();
        if (json != null) {
            Log.i("asddd","preparo JSON");
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
}
