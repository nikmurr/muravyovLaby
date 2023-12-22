package com.example.myapp61;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("MY_NOTIFICATION_ACTION")) {
            // Обработка нажатия на уведомление
            Toast.makeText(context, "Уведомление нажато", Toast.LENGTH_SHORT).show();

            // Открываем активность приложения по нажатию на уведомление
            Intent notificationIntent = new Intent(context, ViewNotificationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notificationIntent);
        }
    }
}
