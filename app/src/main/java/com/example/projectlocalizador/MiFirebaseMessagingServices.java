package com.example.projectlocalizador;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.projectlocalizador.MainActivity;
import com.example.projectlocalizador.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MiFirebaseMessagingServices extends FirebaseMessagingService {

    public static final String TAG = "MENSAJES";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();

        Log.d(TAG, "Mensaje recibido de:" +from);
        if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notificacion:" +remoteMessage.getNotification().getBody());
        }
    }

    private void mostrarNotificacion(String title, String body){

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
