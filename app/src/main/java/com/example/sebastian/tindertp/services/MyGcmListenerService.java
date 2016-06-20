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
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.Messages;
import com.example.sebastian.tindertp.commonTools.ViewIdGenerator;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    public MyGcmListenerService() {
    }

    private static final String TAG = "MyGcmListenerService";

    /**
     * Es llamado cuando un mensaje es recibido.
     *
     * @param from SenderID del server.
     * @param data Data que envia el server.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("Mensaje");
        if(message == null) {
            handleMatch(data);
        }else {
            handleMessage(data);
        }
    }

    private void handleMessage(Bundle data) {
        String message = data.getString("Mensaje");
        String fromUserEmail = data.getString("Emisor");
        String fromUserName = data.getString("Emisor nombre");

        Log.d(TAG, "From: " + fromUserEmail);
        Log.d(TAG, "Message: " + message);

        if(!TinderTP.isTheSameChat(fromUserEmail)) {
            update(this, fromUserEmail, message, Common.MATCH_MSG_KEY);
            update(this, fromUserEmail, message, Common.PROFILE_MSG_KEY);
            update(this, fromUserEmail, message, Common.CHAT_LIST_MSG_KEY);
                    sendNotification(fromUserEmail, message);
        }else {
            update(this, fromUserEmail, message, Common.CHAT_KEY);
        }
    }

    private void handleMatch(Bundle data) {

        String userEmailMatch = data.getString("Usuario");
        String fromUserName = data.getString("Emisor nombre");

        updateMatch(this, userEmailMatch, Common.MATCH_MATCH_KEY);
        updateMatch(this, userEmailMatch, Common.PROFILE_MATCH_KEY);
        updateMatch(this, userEmailMatch, Common.CHAT_LIST_MATCH_KEY);
        sendNotificationMatch(userEmailMatch, fromUserName);

    }

    /**Crea notificaciones y hace un pendingIntent que se usa si la notificacion
     * es clickeada. */
    private void sendNotification(String fromUser, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Messages.getInstance().addMessage(fromUser, message);

        ArraySerialization.persistUserAndMssg(this, fromUser, message);

        intent.putStringArrayListExtra(Common.MSSG_KEY, Messages.getInstance().getMessages());
        intent.putStringArrayListExtra(Common.USER_MSG_KEY, Messages.getInstance().getUsers());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("TinderTP")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        int i =  Messages.getInstance().size() - 1;
        int count = 0;
        while (i >= 0 && count < Common.MAX_MSSG_NOTIF) {
            inboxStyle.addLine(Messages.getInstance().get(i));
            i--;
            count ++;
        }

        if (count == Common.MAX_MSSG_NOTIF ) {
            inboxStyle.addLine("Tienes " + Messages.getInstance().size() + " nuevos mensajes...");
        }

        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotificationMatch(String fromUserEmail, String userName) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ArraySerialization.persistUserMatch(this, fromUserEmail);
        ArraySerialization.persistUserMatch(this, fromUserEmail, userName);

        intent.putExtra(Common.MATCH_KEY, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("TinderTP - Un nuevo match con:")
                .setContentText(userName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ViewIdGenerator.generateViewId(), notificationBuilder.build());
    }

    private static void updateMatch(Context context, String userMatched, String type) {
        Intent activityMsg = new Intent(type);
        activityMsg.putExtra("user", userMatched);
        LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
    }


    private static void update(Context context, String from, String message, String type) {

        Intent activityMsg = new Intent(type);
        activityMsg.putExtra("message", message);
        activityMsg.putExtra("user", from);
        LocalBroadcastManager.getInstance(context).sendBroadcast(activityMsg);
    }
}
