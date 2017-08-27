package com.kmacho.juan.nurceapp.entities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kmacho.juan.nurceapp.MainActivity;
import com.kmacho.juan.nurceapp.MessageActivity;
import com.kmacho.juan.nurceapp.R;

/**
 * Created by Videos on 26/05/2017.
 */

public class MyFireBaseMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this,MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id_chat",remoteMessage.getData().get("id_chat"));
        intent.putExtra("to_id_user",remoteMessage.getData().get("to_id_user"));
        int id = Integer.parseInt(remoteMessage.getData().get("id_chat"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //builder.setSound(sound);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("NurceApp");
        notificationBuilder.setSound(sound);
        notificationBuilder.setContentText(remoteMessage.getData().get("mensaje"));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.market_nurce);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }
}
