package com.example.sebastian.tindertp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.sebastian.tindertp.MainActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.Messages;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    public MyGcmListenerService() {
    }

    private static final String TAG = "MyGcmListenerService";

    /**
     * Es llamado cuando un mensaje es recibido.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String fromUser = "Aldana";

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        update(this, fromUser,message, "MATCH");
        update2(this, fromUser, message, "PROFILE");
        update2(this, fromUser, message, "CHAT_LIST");
        update2(this, fromUser, message, "CHAT");

        if(!TinderTP.isTheSameChat(fromUser)) {
            sendNotification(fromUser, message);
        }
    }


    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String fromUser, String message) {
        Log.i("NOTI", "send");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Messages.getInstance().addMessage(fromUser, message);

        Log.i("ads"," tam" + Messages.getInstance().getMessages().size());

        intent.putStringArrayListExtra(Common.MSSG_KEY, Messages.getInstance().getMessages());
        intent.putStringArrayListExtra(Common.USER_MSG_KEY, Messages.getInstance().getUsers());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TinderTP")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private static void update(Context context, String from, String message, String type) {

        Intent activityMsg = new Intent(type);
        activityMsg.putExtra("message", message);
        activityMsg.putExtra("user", from);

        Log.i("actualiza", "updateMatch");
        LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
    }
    private static void update2(Context context, String from, String message, String type) {

        Intent activityMsg = new Intent(type);
        activityMsg.putExtra("message", message);
        activityMsg.putExtra("user", from);

        Log.i("actualiza", "update");
        LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
    }
}
