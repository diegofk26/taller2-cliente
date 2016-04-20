package com.example.sebastian.tindertp.gestureTools;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;

import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.FullScreenViewActivity;
import com.example.sebastian.tindertp.MatchingActivity;

import java.util.List;

public class OnSwipeTapTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    protected MatchingActivity context;
    protected int i;

    public OnSwipeTapTouchListener(MatchingActivity ctx){
        context = ctx;
        i = 0;
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if ( context.getImgFiles().size() != 0 ) {
                Intent fullScreen = new Intent(context, FullScreenViewActivity.class);
                fullScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                List<String> imgFiles = context.getImgFiles();
                String[] b = imgFiles.toArray(new String[imgFiles.size()]);

                int imgPosition = context.getImagePosition();

                fullScreen.putExtra(Common.IMG_KEY, b);
                fullScreen.putExtra(Common.IMG_POS_KEY, imgPosition);
                context.startActivity(fullScreen);
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    protected void setImgViewAndAnimation(){
        context.setImagePosition(i);
        context.getImgView().startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        context.getImgView().setImageBitmap(context.getBitmaps().get(i));
        context.getImgView().startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
    }
    private void onSwipeRight() {

        if (i < context.getBitmaps().size() && i != 0) {
            i--;
            setImgViewAndAnimation();
        }
    }

    private void onSwipeLeft() {
        if (i < (context.getBitmaps().size() - 1)) {
            i++;
            setImgViewAndAnimation();
        }
    }
}
