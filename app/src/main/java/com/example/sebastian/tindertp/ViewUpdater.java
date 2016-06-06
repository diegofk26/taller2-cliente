package com.example.sebastian.tindertp;

import java.util.ArrayList;

public interface ViewUpdater {

    void addTransmitterToMssg(int index, String transmitter, String message);
    void clearRows();
    void buildRowItems();
    void haveToUpdate(int index);
    void updateListView(int index, boolean isBold);
    void removeExtra(String key);
    boolean hasExtra(String key);
    ArrayList<String> getStringArrayExtra(String key);
}
