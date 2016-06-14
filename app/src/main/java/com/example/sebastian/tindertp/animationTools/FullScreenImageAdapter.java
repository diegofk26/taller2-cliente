package com.example.sebastian.tindertp.animationTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.sebastian.tindertp.ImageTools.ImageBase64;
import com.example.sebastian.tindertp.R;
/**Adaptador entre PageView y ImageView*/
public class FullScreenImageAdapter extends PagerAdapter {

    private Context ctx;
    private String picBase64;
    ImageView imgDisplay;
    private LayoutInflater inflater;

    // constructor
    public FullScreenImageAdapter(Context ctx, String base64) {
        this.ctx = ctx;
        this.picBase64 = base64;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    private void setImgViewPager(View viewLayout){
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imageView3);

        Bitmap myBitmap = ImageBase64.decodeBase64(picBase64);

        imgDisplay.setImageBitmap(myBitmap);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        //Set the current Image to ImageView in ViewPager
        setImgViewPager(viewLayout);
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}