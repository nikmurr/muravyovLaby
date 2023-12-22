package com.example.myapp61;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import java.time.Instant;
import java.time.Duration;

import androidx.core.app.NotificationCompat;

import java.time.ZonedDateTime;

public class NotificationUtils {

    private Context context;
    private NotificationManager notificationManager;
    private final String channelId = "my_channel_id";
    private final String channelName = "My channel";

    public NotificationUtils(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    public void showNotification(int id, String title, String text, ZonedDateTime dateTime) {
        Instant now = Instant.now();
        long delay = Duration.between(now, dateTime).toMillis();
        Log.i("NotificationsUtils", "Задержка " + String.valueOf(delay));

        if (delay > 0) {
            Intent intent = new Intent(context, NotificationPublisher.class);
            intent.putExtra("title", title);
            intent.putExtra("text", text);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.title, title);
            remoteViews.setTextViewText(R.id.text, text);

            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .setCustomContentView(remoteViews)
                    .build();
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    Log.i("NotificationsUtils", "Notification send");
                    notificationManager.notify(id, notification);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void cancelNotification(int id) {
        notificationManager.cancel(id);
    }
}



