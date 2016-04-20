package com.example.sebastian.tindertp;

import java.util.Iterator;
import java.util.List;

public class UrlArrayAdapter {

    private int end;
    private Iterator<String>it;
    private int i;

    public UrlArrayAdapter(List<String> urls) {
        it = urls.iterator();
        i = 0; //to iterate
        end = 3;
    }

    public boolean hasNext() {
        boolean has = (it.hasNext() && i < end);

        if (it.hasNext() && !(i < end)){
            end++;
        }
        return has;
    }

    public String next() {
        i++;
        return it.next();
    }

    public boolean downloadComplete(){
        return !it.hasNext();
    }
}
