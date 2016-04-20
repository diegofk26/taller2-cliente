package com.example.sebastian.tindertp;

import java.util.Iterator;
import java.util.List;

public class UrlArrayAdapter {

    private List<String> urls;
    private int begin;
    private int end;
    private Iterator<String>it;
    private int i;

    public UrlArrayAdapter(List<String> urls) {
        this.urls = urls;
        it = urls.iterator();
        i = 0; //to iterate
        begin = 0;
        end = 3;
    }

    public int size(){
        return end;
    }

    public String getUrl(int i){
        return urls.get(i);
    }

    public int begin(){
        return begin;
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
