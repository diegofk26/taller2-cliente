package com.example.sebastian.tindertp.commonTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MultiHashIntStr {

    private Map<Integer,List<String>> multiMap;


    public MultiHashIntStr() {
        this.multiMap = new HashMap<>();
    }

    public void put (Integer key, String value) {
        List<String> list;

        if(multiMap.containsKey(key)) {
            list = multiMap.get(key);
        }else {
            list = new ArrayList<>();
        }

        list.add(value);
        multiMap.put(key, list);
    }

    public void set(Integer key, int pos, String value) {
        get(key).set(pos,value);
    }

    public Set<Map.Entry<Integer, List<String>>> entrySet() {
        return multiMap.entrySet();
    }

    public List<String> get(Integer key) {
        return multiMap.get(key);
    }

    public int size() {
        return multiMap.size();
    }

    public String getLast (Integer key) {
        List<String> editTextList = multiMap.get(key);
        int lastIndex = editTextList.size() - 1;
        return editTextList.get(lastIndex);
    }

    public boolean hasKey(Integer key) {
        return multiMap.containsKey(key);
    }
}
