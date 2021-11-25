package com.example.honahlappchat.FireBase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.honahlappchat.Activity.activity_chat;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.models.UsersM;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    /**
     * DAY LA PHAN DE SETUP THONG BAO PHAN GOI THONG BAO CO TRONF CLASS ACTIVITY CHAT
     * xem youtube lam theo nen d hieu cl gi
     * */

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        UsersM usersM = new UsersM();
        /** 3 thanh phan phai co trong thong bao bay*/
        usersM.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        usersM.name = remoteMessage.getData().get(Constants.KEY_NAME);
        usersM.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationID = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, activity_chat.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER , usersM);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setContentTitle(usersM.name);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat message notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID, builder.build());


    }
}
