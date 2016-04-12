package com.example.sebastian.tindertp;

import android.app.Application;

/**
 * Created by sebastian on 10/04/16.
 */
public class TinderTP extends Application {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String someVariable) {
        this.url = someVariable;
    }

}
