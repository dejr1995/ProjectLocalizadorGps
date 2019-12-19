package com.example.projectlocalizador;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;


public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");

        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notific = new NotificationCompat.Builder(context)
                .setContentTitle("Alerta: ")
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setVibrate(new long[] { 500, 500, 500, 500, 500 })
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentText("El objeto esta fuera de su lugar");
        notificationManager.notify(NOTIFICATION_ID, notific.build());
//.sound = Uri.parse("file:///sdcard/notification/notification.mp3");

    }


    private Notification createNotification() {
        Notification notification = new Notification();

        notification.icon = R.drawable.ic_launcher_background;
        notification.when = System.currentTimeMillis();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;

        return notification;
    }

}
