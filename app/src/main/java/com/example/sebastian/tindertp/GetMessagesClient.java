package com.example.sebastian.tindertp;

import android.support.design.widget.Snackbar;

import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import java.io.IOException;
import java.util.Map;

public abstract class GetMessagesClient extends RequestResponseClient {

    public GetMessagesClient(DataTransfer transfer, ConnectionStruct conn, Map<String, String> values) {
        super(transfer, conn, values);
    }

    @Override
    protected void getJson() throws IOException {
        jsonString = readIt();
    }

    @Override
    protected void showText(String message) {
        Snackbar.make(dTransfer.findView(R.id.listview), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}