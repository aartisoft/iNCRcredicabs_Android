package com.ncr.interns.codecatchers.incredicabs.notification;

/**
 * Created by pg250235 on 2/25/2018.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ncr.interns.codecatchers.incredicabs.*;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public String reqSubString;
    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private static final String TAG = "MyFirebaseInstanceClass";
    private NotificationManager notificationManager;
    private static final String MY_PREFERENCES = "MyPrefs";
    public static final String ACTION1 = "Approve";
    public static final String ACTION2 = "Reject";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent notificationIntent;
        Intent action1Intent = new Intent(this, Approve.class).setAction(ACTION1);
        Intent action2Intent = new Intent(this, Reject.class).setAction(ACTION2);
        PendingIntent action1PendingIntent = PendingIntent.getBroadcast(this, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent action2PendingIntent = PendingIntent.getBroadcast(this, 0,
                action2Intent, PendingIntent.FLAG_ONE_SHOT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting up Notification channels for android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);
        String msg = remoteMessage.getData().get("message");
        int ind1 = msg.indexOf(":");
        int ind2 = msg.indexOf("\n");
        reqSubString = msg.substring(++ind1, ind2);

        Log.d(TAG, "onMessageReceived: request ID " + reqSubString);

        //<editor-fold desc="Putting reqId in shared Preferences">
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().putString("reqId", reqSubString).putString("notificationId", "" + notificationId + "").apply();
        //</editor-fold>
        if (!remoteMessage.getData().get("title").equals("Cab Request Status")) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_car_notification)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
                    .setSound(defaultSoundUri)
                    .setOngoing(true)
                    .addAction(new NotificationCompat.Action(R.drawable.ic_check_black_24dp, "Approve", action1PendingIntent))
                    .addAction(new NotificationCompat.Action(R.drawable.ic_clear_black_24dp, "Reject", action2PendingIntent));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
            //notificationManager.cancel(notificationId);
        } else {

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_car_notification)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setAutoCancel(true)
                    .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }


}
