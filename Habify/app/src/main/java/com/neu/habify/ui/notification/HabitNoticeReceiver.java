package com.neu.habify.ui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.neu.habify.MainActivity;
import com.neu.habify.ui.dashboard.DashboardFragment;

public class HabitNoticeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String CHANNEL_ID = "Channel1";
        String title = intent.getStringExtra("title");
        String text = "Start at: " + intent.getStringExtra("startTime");
        String group_key = intent.getStringExtra("group");
        int id = intent.getIntExtra("id", 0);
        NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent result_intent = new Intent(context, MainActivity.class);
        result_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, result_intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setGroup(group_key)
                .setAutoCancel(true);

        notificationManager.notify(id, builder.build());
        if (id == 2) {
            Notification.Builder builderSummary =
                    new Notification.Builder(context, CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setGroup(group_key)
                            .setGroupSummary(true);
            notificationManager.notify(0, builderSummary.build());
        }
    }
}
