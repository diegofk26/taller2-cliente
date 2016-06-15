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

    public Set<Map.Entry<String, List<Object>>> entrySet() {
        return multiMap.entrySet();
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
}
