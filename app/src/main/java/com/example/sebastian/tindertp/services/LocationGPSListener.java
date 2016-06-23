package com.example.sebastian.tindertp.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.sebastian.tindertp.ConectivityManagerInterface;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Map;

public class LocationGPSListener implements LocationListener {

    private ConectivityManagerInterface context;
    private RequestResponseClient client;
    JSONObject jsonLocation = new JSONObject();

    public LocationGPSListener(ConectivityManagerInterface ctx) {
        context = ctx;
        String url = ((TinderTP) ((Activity) context).getApplication()).getUrl();
        String user = ((TinderTP) ((Activity) context).getApplication()).getUser();
        String token = ((TinderTP) ((Activity) context).getApplication()).getToken();

        ConnectionStruct conn = new ConnectionStruct(Common.GPS, Common.PUT, url);
        Map<String, String> headers = HeaderBuilder.forNewUser(user, token);
        client = new RequestResponseClient(context, conn, headers) {
            @Override
            protected void getJson() throws IOException {}

            @Override
            protected void onPostExec() {
                if (badResponse || !isConnected) {
                    showText("No se pudo conectar con el server.");
                }
            }

            @Override
            protected void showText(String message) {
                Common.showSnackbar(((Activity)context).findViewById(R.id.matchFragment), message);
            }
        };
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            jsonLocation.put(Common.LATITUDE_KEY,location.getLatitude());
            jsonLocation.put(Common.LONGITUDE_KEY, location.getLongitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.addBody(jsonLocation.toString());
        client.runInBackground();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ((Activity)context).startActivity(i);
    }
}
