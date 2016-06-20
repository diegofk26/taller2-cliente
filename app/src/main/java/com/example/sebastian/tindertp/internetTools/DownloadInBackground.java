package com.example.sebastian.tindertp.internetTools;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class DownloadInBackground extends AsyncTask<String, Void, Integer> {

    private MediaDownloader mediaDownloader;
    private static final String CONNECTION = "Connection";

    public DownloadInBackground(MediaDownloader mediaDownloader) {
        this.mediaDownloader = mediaDownloader;
    }

    @Override
    protected Integer doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            mediaDownloader.establishConnection(urls[0]);
        } catch (IOException e) {
            mediaDownloader.setConnection(false);
            Log.e(CONNECTION, "Unable to retrieve web page. " + urls[0] + " may be invalid.");
            mediaDownloader.showText( "Unable to retrieve web page. " + urls[0] + " may be invalid.");
            //return -1;
        }

        return 1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        mediaDownloader.onPostExec();
    }
}
