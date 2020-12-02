package com.app.cloud.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.app.cloud.R;
import com.app.cloud.activity.DashboardActivity;
import com.app.cloud.utility.Constants;

public class IncomingNotificationService extends Service {

    private static final String TAG = IncomingNotificationService.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopForeground(true);
        Log.d(TAG , "onStartCommand " + intent);
        String msg = "";
        String segment = "";
        String title ="";
        if(intent != null){
            if(isAppVisible()){
                title = intent.getStringExtra(Constants.PUSH_TITLE);
                msg = intent.getStringExtra(Constants.PUSH_MESSAGE);
                segment = intent.getStringExtra(Constants.SEGMENT_NAME);

                Intent activityIntent = new Intent(this, DashboardActivity.class);
                activityIntent.setAction(intent.getAction());
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityIntent.putExtra(Constants.PUSH_TITLE , title);
                activityIntent.putExtra(Constants.PUSH_MESSAGE , msg);
                activityIntent.putExtra(Constants.SEGMENT_NAME , segment);
                startActivity(activityIntent);
            }else
                startForeground(1,createNotification(intent,123,NotificationManager.IMPORTANCE_HIGH));
        }

        return START_NOT_STICKY;
    }


    private Notification createNotification(Intent intent, int notificationId, int channelImportance) {

        String title = intent.getStringExtra(Constants.PUSH_TITLE);
        String msg = intent.getStringExtra(Constants.PUSH_MESSAGE);
        String segment = intent.getStringExtra(Constants.SEGMENT_NAME);

        Intent activityIntent = new Intent(this, DashboardActivity.class);
        activityIntent.setAction(intent.getAction());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityIntent.putExtra(Constants.PUSH_TITLE , title);
        activityIntent.putExtra(Constants.PUSH_MESSAGE , msg);
        activityIntent.putExtra(Constants.SEGMENT_NAME , segment);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, notificationId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return buildNotificationN( "New Notification",
                    pendingIntent,
                    activityIntent,
                    notificationId,
                    createChannel());
        } else {
            return new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("New Notification")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private Notification buildNotificationN(String text, PendingIntent pendingIntent,Intent classIntent,
                                            int notificationId,
                                            String channelId) {

        Notification.Builder builder =
                new Notification.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String createChannel() {
        NotificationChannel callInviteChannel = new NotificationChannel("Constants.VOICE_CHANNEL_HIGH_IMPORTANCE",
                "Primary Voice Channel", NotificationManager.IMPORTANCE_HIGH);
        String channelId = "Constants.VOICE_CHANNEL_HIGH_IMPORTANCE";

        callInviteChannel.setLightColor(Color.GREEN);
        callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(callInviteChannel);

        return channelId;
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
