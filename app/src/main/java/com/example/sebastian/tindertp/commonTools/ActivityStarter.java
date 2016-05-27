package com.example.sebastian.tindertp.commonTools;

import android.content.Context;
import android.content.Intent;


public class ActivityStarter {

    public static void start(Context context, Class<?> newActivity){
        Intent activity = new Intent(context, newActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }

    public static void startClear(Context context, Class<?> newAct) {
        Intent activity = new Intent(context, newAct);
        activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activity);
    }
}
