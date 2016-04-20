package com.example.sebastian.tindertp.commonTools;


public class Conn_struct {

    public String path;
    public String URL;
    public String requestMethod;

    public Conn_struct (String path,String request,String url) {
        this.path = path;
        this.requestMethod = request;
        this.URL = url;
    }
}
