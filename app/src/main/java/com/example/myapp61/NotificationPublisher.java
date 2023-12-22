package com.example.myapp61;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.createNotificationChannel();
    }
}
