package com.example.sebastian.tindertp.commonTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiHashMap {

    private Map<String,List<Object>> multiMap;

    public MultiHashMap() {
        this.multiMap = new HashMap<>();
    }

    public void put (String key, Object value) {
        List<Object> list;

        if(multiMap.containsKey(key)) {
            list = multiMap.get(key);
        }else {
            list = new ArrayList<>();
        }

        list.add(value);
        multiMap.put(key, list);
    }

    public void clear() {
        multiMap.clear();
    }

    public void set(String key, int arrayPosition, Object value) {
        get(key).set(arrayPosition, value);
    }

    public Set<Map.Entry<String, List<Object>>> entrySet() {
        return multiMap.entrySet();
    }

    public Set<String> getKeys() {
        return multiMap.keySet();
    }

    public List<Object> get(String key) {
        return multiMap.get(key);
    }

    public int size() {
        return multiMap.size();
    }

    public Object getLast (String key) {
        List<Object> editTextList = multiMap.get(key);
        int lastIndex = editTextList.size() - 1;
        return editTextList.get(lastIndex);
    }

    public boolean hasKey(String hint) {
        return multiMap.containsKey(hint);
    }

    public List<String> getKeysList() {
        List<String> keys = new ArrayList<>();
        for (String key : getKeys()) {
            keys.add(key);
        }
        return keys;
    }
}
