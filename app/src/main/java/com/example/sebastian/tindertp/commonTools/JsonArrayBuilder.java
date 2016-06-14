package com.example.sebastian.tindertp.commonTools;

import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class JsonArrayBuilder {

    public static JSONArray buildInterests(AdapterHashMap adpHashMap) {

        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<String, List<EditText>> entry : adpHashMap.entrySet()) {
            List<EditText> interests = entry.getValue();
            for(int i = 0; i < interests.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("category",entry.getKey());
                    jsonObject.put("value", interests.get(i).getText().toString());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }
}
