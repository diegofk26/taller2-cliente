package com.example.sebastian.tindertp.commonTools;

import android.graphics.Bitmap;

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

    public ProfileInfo(String json) {

        try {
            JSONObject jsonO = new JSONObject(json);
            name = jsonO.getString(Common.NAME_KEY);
            alias = jsonO.getString(Common.ALIAS_KEY);
            age = jsonO.getInt(Common.AGE_KEY);
            sex = jsonO.getString(Common.SEX_KEY);
            email = jsonO.getString(Common.EMAIL_KEY);
            interests = jsonO.getJSONArray(Common.INTERESTS_KEY);
            buildInterestMap();
            photo = jsonO.getString(Common.PHOTO_KEY);
            bitmap = ImageBase64.decodeBase64(photo);

        }catch (JSONException e){}
    }

    private void buildInterestMap() {
        interestMap = new MultiHashMap();
        for(int i = 0; i< interests.length(); i++) {
            try {
                JSONObject jsonO = (JSONObject) interests.get(i);

                String key = jsonO.getString(Common.CATEGORY_KEY);
                String value = jsonO.getString(Common.VALUE_KEY);

                interestMap.put(key,value);

            }catch (JSONException e) {}
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
