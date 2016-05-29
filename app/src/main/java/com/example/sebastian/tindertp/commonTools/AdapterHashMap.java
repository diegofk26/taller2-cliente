package com.example.sebastian.tindertp.commonTools;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterHashMap {

    private Map<String,List<EditText>> editTextMap;


    public AdapterHashMap() {
        this.editTextMap = new HashMap<>();
    }

    public void put (String key, EditText editText) {
        List<EditText> editTextList;

        if(editTextMap.containsKey(key)) {
            editTextList = editTextMap.get(key);

        }else {
            editTextList = new ArrayList<>();
        }

        editTextList.add(editText);
        editTextMap.put(key,editTextList);
    }

    public EditText getLast (String key) {
        List<EditText> editTextList = editTextMap.get(key);
        int lastIndex = editTextList.size() - 1;
        return editTextList.get(lastIndex);
    }

    public boolean hasKey(String hint) {
        return editTextMap.containsKey(hint);
    }
}
