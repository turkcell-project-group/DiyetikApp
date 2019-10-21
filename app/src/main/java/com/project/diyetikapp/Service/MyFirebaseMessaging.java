package com.project.diyetikapp.Service;

import android.app.Notification;
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

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI26(remoteMessage);
            else
                sendNotification(remoteMessage);

        }}

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        //click to notificatio => go to order list
        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder;

        if (Common.currentUser != null) {

            Intent ıntent = new Intent(this, OrderStatus.class);
            ıntent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            ıntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, ıntent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getDiyetikChannelNotification(title, message, pendingIntent, defaultSoundUri);

            helper.getManager().notify(new Random().nextInt(), builder.build());
        }
        else {  //fix crash if notification send from news system (Common.currentUser== null)
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            helper = new NotificationHelper(this);
            builder = helper.getDiyetikChannelNotification(title, message, defaultSoundUri);

            helper.getManager().notify(new Random().nextInt(), builder.build());

        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Intent ıntent = new Intent(this, MainActivity.class);
        ıntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ıntent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());
    }
}
