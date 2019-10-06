package com.project.diyetikapp.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.project.diyetikapp.R;

public class NotificationHelper extends ContextWrapper {
    private static final String DIYETIK_CHANEL_ID="com.project.diyetikapp.DiyetikApp";
    // Emin değilim böylede olabilir :)))   private static final String DIYETIK_CHANEL_ID="com.project.diyetikapp"
    private static final String DIYETIK_CHANEL_NAME="Diyetik";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)//only working this function if API is 26 or higer
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel diyetikChannel= new NotificationChannel( DIYETIK_CHANEL_ID,
                DIYETIK_CHANEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        diyetikChannel.enableLights(false);
        diyetikChannel.enableVibration(true);
        diyetikChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(diyetikChannel);
        

    }

    public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getDiyetikChannelNotification(String title, String body, PendingIntent contentIntent,
                                                                          Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(), DIYETIK_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getDiyetikChannelNotification(String title, String body,
                                                                          Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(), DIYETIK_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

}
