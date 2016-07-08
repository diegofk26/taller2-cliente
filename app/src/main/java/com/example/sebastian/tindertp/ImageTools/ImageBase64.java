package com.example.sebastian.tindertp.ImageTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.example.sebastian.tindertp.commonTools.Dimension;

import java.io.ByteArrayOutputStream;

public class ImageBase64 {

    private final static int quality = 100;

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(String file, Display display) {

        // fja las dimesiones
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        Dimension dimension = new Dimension(options.outWidth,options.outHeight);

        scalingBounds(display,dimension);

        // Calcula el tamaño
        options.inSampleSize = calculateInSampleSize(options, dimension.width, dimension.height);

        // Decodifica decuardo a optiosn
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public static void scalingBounds(Display display, Dimension dimension) {

        Point size = new Point();
        display.getSize(size);
        int maxWidth = 720;
        int maxHeight = 1280;

        if (dimension.width > dimension.height) {
            // landscape
            float ratio = (float) dimension.width / maxWidth;
            dimension.width = maxWidth;
            dimension.height = (int)(dimension.height / ratio);
        } else if (dimension.height > dimension.width) {
            // portrait
            float ratio = (float) dimension.height / maxHeight;
            dimension.height = maxHeight;
            dimension.width = (int)(dimension.width / ratio);
        } else {
            // square
            dimension.height = maxHeight;
            dimension.width = maxWidth;
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        Log.i("BASE64","decode");
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
