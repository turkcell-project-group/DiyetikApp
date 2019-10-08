package com.project.diyetikapp.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Helper.NotificationHelper;
import com.project.diyetikapp.MainActivity;
import com.project.diyetikapp.OrderStatus;
import com.project.diyetikapp.R;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(remoteMessage);
        else
            sendNotification(remoteMessage);

    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title=notification.getTitle();
        String content=notification.getBody();

        //click to notificatio => go to order list
        Intent ıntent = new Intent(this, OrderStatus.class);
        ıntent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
        ıntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ıntent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper=new NotificationHelper(this);
        android.app.Notification.Builder builder=helper.getDiyetikChannelNotification(title,content,pendingIntent,defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent ıntent = new Intent(this, MainActivity.class);
        ıntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ıntent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());
    }
}
