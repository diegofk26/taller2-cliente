package com.example.sebastian.tindertp.internetTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.example.sebastian.tindertp.UrlArrayAdapter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.MatchingActivity;
import com.example.sebastian.tindertp.diskTools.SaveFileInBackground;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloaderClient extends MediaDownloader{

    private final String TAG = this.getClass().getSimpleName();

    private Bitmap bitmap;
    private int id;

    private ByteArrayOutputStream out;
    private MatchingActivity matchingActivity;

    private UrlArrayAdapter urlAdapter;

    TextView mText;

    public ImageDownloaderClient(MatchingActivity matchingA,TextView mtext) {
        bitmap = null;
        id = 0;

        //TODO: urlS y urlP doesn't work, seems not be coded as bitmap
        String urlS = "http://alquimistasdelapalabra.com/descripcion/12_paisajes_fantasticos/Paisaje_nieve.jpg";
        String urlP = "http://pmmv.com.es/sites/default/files/PAISAJE_0.jpg";
        //urlC, urlB y urlN  works
        String urlC = "http://concepto.de/wp-content/uploads/2015/03/Paisaje.jpg";
        String urlB = "http://live-wallpaper.net/iphone/img/app/t/h/the_dark_knight_rises_wallpaper_47_1ba030b4b76b3c07c13c4e6514328202_raw.jpg";
        String urlN = "http://k32.kn3.net/taringa/3/2/B/9/F/C/WashingtongPower/C98.jpg";
        String urlZ = "http://www.biglittlegeek.com/wp-content/uploads/2015/11/star-wars-wallpaper-for-iphone-6-plus-6.jpg";
        String urlI = "http://www.topdesignmag.com/wp-content/uploads/2011/01/iphone_4_wallpaper_hd_pack-570x8551.jpg";
        String urlq = "http://www.iphonefansite.com/wp-content/gallery/star-wars-iphone-wallpapers/01681.jpg";
        String urlr = "http://b1.img.mobypicture.com/b9a40d622601c28babf5ef9b6134174e_view.jpg";
        List<String> urls = new ArrayList<String>();
        urls.add(urlB); urls.add(urlN); urls.add(urlC); urls.add(urlI); urls.add(urlZ);urls.add(urlq);urls.add(urlr);

        urlAdapter = new UrlArrayAdapter(urls);
        matchingActivity = matchingA;
        this.mText = mtext;
    }

    @Override
    void initSpecificVar() {
        out = null;
    }

    @Override
    void connect() throws IOException {
        Log.i(CONNECTION,"Connecting");
        connection.connect();
        Log.i(CONNECTION, "Connected");
        final int length = connection.getContentLength();
        Log.i(CONNECTION,"Content's Length: " + length);
        if (length <= 0) {
            Log.e(CONNECTION, "Invalid content length. The URL is probably not pointing to a file");
            throw new IOException();
        }
        is = new BufferedInputStream(connection.getInputStream(), Common.BUFF_SIZE);
        out = new ByteArrayOutputStream();
        Log.i(CONNECTION,"is-out created.");
        byte bytes[] = new byte[Common.BUFF_SIZE];
        int count;
        long read = 0;
        while ((count = is.read(bytes)) != -1) {
            read += count;
            //Log.i(CONNECTION,"Progress... " +  ((read * 100) / length) );
            out.write(bytes, 0, count);
        }
        bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
        Log.i(CONNECTION,"Download succesful");
    }

    @Override
    void closeConnection() throws IOException {
        Log.i(CONNECTION,"closing connection...");
        try {
            if (connection != null) {
                connection.disconnect();
                Log.i(CONNECTION,"Disconected");
            }
            if (out != null) {
                out.flush();
                out.close();
                Log.i(CONNECTION,"out: flushed and closed");
            }
            if (is != null) {
                is.close();
                Log.i(CONNECTION,"is: closed");
            }
        } catch (Exception e) {
            Log.e(CONNECTION,e.toString());
            e.printStackTrace();
        }
    }

    @Override
    void onPostExec() {

        if (bitmap == null) {
            Log.e(TAG, "Factory returned a null result");
            Log.e(TAG, "Downloaded file could not be decoded as bitmap");
            mText.setText("Downloaded file could not be decoded as bitmap");
        } else {
            Log.i(TAG, "download complete, " + bitmap.getByteCount() + " bitmaps transferred");

            final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "image_test" + File.separator + "img" + id
                    + "." + mFormat.name().toLowerCase();
            final File myImageFile = new File(filePath);

            id++;
            SaveFileInBackground.writeToDisk(myImageFile, bitmap, mFormat, false);
            matchingActivity.onBackgroundTaskDataObtained(bitmap,filePath);
        }

    }

    @Override
    public void runInBackground() {
        ConnectivityManager connMgr = (ConnectivityManager)matchingActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            while (urlAdapter.hasNext())
                new DownloadInBackground(this).execute(urlAdapter.next());

        } else {
            mText.setText("No network connection available.");
        }
    }

    @Override
    void showText(String message) {

    }


    public boolean downloadComplete() {
        return urlAdapter.downloadComplete();
    }
}
