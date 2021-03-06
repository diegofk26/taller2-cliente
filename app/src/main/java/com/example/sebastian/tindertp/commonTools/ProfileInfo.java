package com.example.sebastian.tindertp.commonTools;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProfileInfo {

    public String name;
    public String alias;
    public int age;
    public String sex;
    public Bitmap bitmap;
    public String photo;
    public String email;
    public JSONArray interests;
    private MultiHashMap interestMap;
    private JSONObject jsonUser;
    private static final String PROFILE_INFO_TAG = "ProfileInfo";

    /**Construye los intereses sobre un MultiHashMap dado.*/
    public ProfileInfo(String json, MultiHashMap interests) {
        interestMap = interests;
        buildProfile(json);
    }

    public ProfileInfo(String json) {
        interestMap = new MultiHashMap();
        buildProfile(json);
    }

    public void buildProfile(String json) {
        try {
            Log.i(PROFILE_INFO_TAG, "Construyo la informacion del perfil del JSON");
            JSONObject jsonO = new JSONObject(json);
            jsonUser = jsonO.getJSONObject(Common.USER);
            name = jsonUser.getString(Common.NAME_KEY);
            alias = jsonUser.getString(Common.ALIAS_KEY);
            age = jsonUser.getInt(Common.AGE_KEY);
            sex = jsonUser.getString(Common.SEX_KEY);
            email = jsonUser.getString(Common.EMAIL_KEY);
            interests = jsonUser.getJSONArray(Common.INTERESTS_KEY);
            buildInterestMap();
            photo = jsonUser.getString(Common.PHOTO_KEY);
            bitmap = ImageBase64.decodeBase64(photo);

        }catch (JSONException e){
            Log.e(PROFILE_INFO_TAG, "Error en la construyccion de perfil json");
            e.printStackTrace();
        }
    }

    public JSONObject updateProfile(String alias, String name, JSONArray interests) {

        try {
            Log.i(PROFILE_INFO_TAG, "Actualizo la informacion del perfil del JSON");
            jsonUser.put(Common.NAME_KEY, name);
            jsonUser.put(Common.ALIAS_KEY, alias);
            jsonUser.put(Common.INTERESTS_KEY,interests);

        } catch (JSONException e) {
            Log.e(PROFILE_INFO_TAG, "Error en la actualizacion de perfil json");
            e.printStackTrace();
        }
        return jsonUser;
    }

    private void buildInterestMap() {
        for(int i = 0; i< interests.length(); i++) {
            try {
                Log.i(PROFILE_INFO_TAG, "Construccion de intereses de perfil json");
                JSONObject jsonO = (JSONObject) interests.get(i);
                String key = jsonO.getString(Common.CATEGORY_KEY);
                String value = jsonO.getString(Common.VALUE_KEY);

                interestMap.put(key,value);

            }catch (JSONException e) {
                Log.e(PROFILE_INFO_TAG, "Error en la contruccion de los intereses de perfil json");
                e.printStackTrace();
            }
        }
    }

    public String getInterestMap(String key) {
        StringBuilder interests = new StringBuilder();

        List<Object> listInterest = interestMap.get(key);

        for (int i = 0; i < listInterest.size(); i++) {

            interests.append((String)listInterest.get(i));

            if (i == listInterest.size() - 1) {
                interests.append(".");
            } else {
                interests.append(", ");
            }
        }

        return interests.toString();
    }
}
