package com.example.sebastian.tindertp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sebastian on 28/03/16.
 */
public class DownloadImage {

    public String downloadImage( String imageUrl, boolean displayProgress) {

        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        try {
            URL imgURL = new URL (imageUrl);
            connection = (HttpURLConnection) imgURL.openConnection();
            if (displayProgress) {

                connection.connect();
                final int length = connection.getContentLength();

                if (length <= 0) {
                    error = new ImageError("Invalid content length. The URL is probably not pointing to a file")
                            .setErrorCode(ImageError.ERROR_INVALID_FILE);
                    this.cancel(true);
                }

                is = new BufferedInputStream(connection.getInputStream(), 8192);
                out = new ByteArrayOutputStream();
                byte bytes[] = new byte[8192];
                int count;
                long read = 0;
                while ((count = is.read(bytes)) != -1) {
                    read += count;
                    out.write(bytes, 0, count);
                    publishProgress((int) ((read * 100) / length));
                }
                bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            } else {
                is = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (Throwable e) {
            if (!this.isCancelled()) {
                error = new ImageError(e).setErrorCode(ImageError.ERROR_GENERAL_EXCEPTION);
                this.cancel(true);
            }
        } finally {
            try {
                if (connection != null)
                    connection.disconnect();
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (is != null)
                    is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
