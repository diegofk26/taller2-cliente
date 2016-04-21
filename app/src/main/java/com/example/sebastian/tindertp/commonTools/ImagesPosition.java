package com.example.sebastian.tindertp.commonTools;

public class ImagesPosition {
    private static ImagesPosition Singleton = null;
    private int imgPosition;

    public static ImagesPosition getInstance() {
        if(Singleton == null)
        {
            Singleton = new ImagesPosition();
        }
        return Singleton;
    }

    public static ImagesPosition getInstance(int newPos) {
        if(Singleton == null)
        {
            Singleton = new ImagesPosition(newPos);
        }
        return Singleton;
    }

    public int getPosition() {
        return imgPosition;
    }

    public void setPosition(int newPos) {
        imgPosition = newPos;
    }

    private ImagesPosition() {
        imgPosition = 0;
    }

    private ImagesPosition(int pos) {
        imgPosition = pos;
    }
}
