package com.example.sebastian.tindertp;

import com.example.sebastian.tindertp.commonTools.MultiHashIntStr;

import java.util.List;

public interface CategoryUpdater {
    void update(List<String> categoryList, MultiHashIntStr categoryValues);
}
