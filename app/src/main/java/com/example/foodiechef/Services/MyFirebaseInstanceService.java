package com.example.foodiechef.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.foodiechef.MainActivity;
import com.example.foodiechef.OrderDetailsActivity;
import com.example.foodiechef.R;
import com.google.firebase.database.core.utilities.Validation;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.example.foodiechef.OrderDetailsActivity.isOpen;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    public static final String REQUEST_ACCEPT = "King";

    @Override
    public void onNewToken(@NonNull String s) {
        Log.e("Refreshed Token",s);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString("token",s);
        editor.commit();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e("From",remoteMessage.getFrom());
        Map<String ,String> data = remoteMessage.getData();
        Log.e("Data", String.valueOf(remoteMessage.getData()));

        NotificationCompat.Builder notificationBuilder;
        String channelId = "fcm_default_channel";
        if(!isOpen) {
            Intent intent = new Intent(this, OrderDetailsActivity.class);
            if (data.get("to").equals("chef")) {
                intent.putExtra("buy", "sell");
                intent.putExtra("chef",data.get("id"));
            } else {
                intent.putExtra("buy", "buy");
                intent.putExtra("chef",data.get("id"));
            }
            intent.putExtra("ID", data.get("body").trim());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(data.get("title"))
                            .setContentText("Order : "+data.get("body"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.logo_trans);
                notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            } else {
                notificationBuilder.setSmallIcon(R.drawable.logo_trans);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
        else {

            Intent intent = new Intent(REQUEST_ACCEPT);
            if (data.get("to").equals("chef")) {
                intent.putExtra("buy", "sell");
                intent.putExtra("chef",data.get("id"));
            } else {
                intent.putExtra("buy", "buy");
                intent.putExtra("chef",data.get("id"));
            }
            intent.putExtra("ID", data.get("body").trim());
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.logo_trans)
                            .setContentTitle(data.get("title"))
                            .setContentText("Order : "+data.get("body"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}

