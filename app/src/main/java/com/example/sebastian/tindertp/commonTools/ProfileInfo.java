package com.example.sebastian.tindertp.commonTools;

import android.graphics.Bitmap;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileInfo {

    public String name;
    public String alias;
    public int age;
    public String sex;
    public Bitmap bitmap;
    public String photo;
    public String email;
    public JSONArray interests;

    public ProfileInfo(String json) {

        try {
            JSONObject jsonO = new JSONObject(json);
            name = jsonO.getString(Common.NAME_KEY);
            alias = jsonO.getString(Common.ALIAS_KEY);
            age = jsonO.getInt(Common.AGE_KEY);
            sex = jsonO.getString(Common.SEX_KEY);
            email = jsonO.getString(Common.EMAIL_KEY);
            interests = jsonO.getJSONArray(Common.INTERESTS_KEY);

            photo = jsonO.getString(Common.PHOTO_KEY);
            bitmap = ImageBase64.decodeBase64(photo);

        }catch (JSONException e){}
    }

}
