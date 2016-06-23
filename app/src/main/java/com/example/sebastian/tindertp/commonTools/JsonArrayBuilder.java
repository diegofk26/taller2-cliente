package com.example.sebastian.tindertp.commonTools;


import android.util.Log;

import com.example.sebastian.tindertp.ExpandedListAdapters.ExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class JsonArrayBuilder {

    public static JSONArray buildInterests(List<String> categories, ExpandableListAdapter adapter) {

        JSONArray jsonArray = new JSONArray();

        Map<Integer,String> values = adapter.getSavedTextMap();

        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);

            int childCount = adapter.getChildrenCount(i);

            for (int j = 0; j < childCount; j++ ) {
                int textID = Integer.parseInt(""+(i+1)+""+j);

                String value = values.get(textID);

                if (!value.isEmpty()) {

                    Log.i("JSONARRAY", "Categoria: " + category + " Valor: " + value);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Common.CATEGORY_KEY, category);
                        jsonObject.put(Common.VALUE_KEY, value);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return jsonArray;
    }
}
