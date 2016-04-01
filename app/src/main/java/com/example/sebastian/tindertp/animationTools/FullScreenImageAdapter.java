package com.example.sebastian.tindertp.animationTools;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.sebastian.tindertp.R;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity fullScreenAct;
    private String[] imgFiles;
    private LayoutInflater inflater;

    // constructor
    public FullScreenImageAdapter(Activity activity, String[] imgFile) {
        this.fullScreenAct = activity;
        this.imgFiles = imgFile;
    }

    @Override
    public int getCount() {
        return this.imgFiles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    private void setImgViewPager(View viewLayout,int position){
        ImageView imgDisplay;
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

        final File myImageFile = new File(imgFiles[position]);
        Bitmap myBitmap = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());

        imgDisplay.setImageBitmap(myBitmap);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;

        inflater = (LayoutInflater) fullScreenAct
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        //Set the current Image to ImageView in ViewPager
        setImgViewPager(viewLayout, position);
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}