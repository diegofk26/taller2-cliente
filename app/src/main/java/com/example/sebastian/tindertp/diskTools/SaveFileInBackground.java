package com.example.sebastian.tindertp.diskTools;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFileInBackground {

    private static final String DISK_TAG ="SavingFile";

    public static void writeToDisk(@NonNull final File imageFile, @NonNull final Bitmap image,
                                   @NonNull final Bitmap.CompressFormat format, boolean shouldOverwrite) {

        if (imageFile.isFile() && imageFile.exists()) {
            if (!shouldOverwrite) {
                Log.d(DISK_TAG, "file already exists, write operation cancelled");
                return;
            } else if (!imageFile.delete()) {
                Log.d( DISK_TAG,"could not delete existing file, most likely the write permission was denied");
                return;
            }
        }

        if (imageFile.isDirectory()) {
            Log.d(DISK_TAG, "the specified path points to a directory, should be a file");
            return;
        }

        File parent = imageFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            Log.d(DISK_TAG, "could not create parent directory");
            return;
        }

        try {
            if (!imageFile.createNewFile()) {
                Log.e(DISK_TAG, "could not create file");
                return;
            }
        } catch (IOException e) {
            Log.e(DISK_TAG,"IOException");
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.i(DISK_TAG,"beginnig to save image in disk.");
                FileOutputStream fos = null;
                try {
                    Log.i(DISK_TAG,"Creating a Streaming image file...");
                    fos = new FileOutputStream(imageFile);
                    Log.i(DISK_TAG,"... Done");
                    Log.i(DISK_TAG,"Compression image...");
                    image.compress(format, 100, fos);
                    Log.i(DISK_TAG,"... Done");
                } catch (IOException e) {
                   Log.e(DISK_TAG,"IOException");
                    this.cancel(true);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                            Log.i(DISK_TAG,"File output stream flushed and closed");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.i(DISK_TAG,"Image saved.");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
