package com.example.sebastian.tindertp.commonTools;


public class ConnectionStruct {

    public String path;
    public String URL;
    public String requestMethod;

    public ConnectionStruct(String path, String request, String url) {
        this.path = path;
        this.requestMethod = request;
        this.URL = url;
    }
}
