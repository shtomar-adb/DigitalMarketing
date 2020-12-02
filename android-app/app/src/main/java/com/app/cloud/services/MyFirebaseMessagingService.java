package com.app.cloud.services;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.app.cloud.background.DBAsyncTask;
import com.app.cloud.listeners.HandlePostExecuteListener;
import com.app.cloud.request.Action;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements HandlePostExecuteListener {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived " + remoteMessage);
        Log.d(TAG , "priority: " + remoteMessage.getPriority());
        Map<String,String> data = remoteMessage.getData();
        Intent intent = new Intent(this, IncomingNotificationService.class);
        intent.setAction(Constants.PUSH);
        intent.putExtra(Constants.PUSH_TITLE, data.get("title"));
        intent.putExtra(Constants.PUSH_MESSAGE, data.get("message"));
        intent.putExtra(Constants.SEGMENT_NAME, data.get("segment_name"));
        startService(intent);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG , "Firebase Token: " + s);
        new AppSharedPref(this).putString(Constants.FCM_TOKEN , s);
       // new DBAsyncTask(this, Action.DBUPDATE,this).execute();
    }

    @Override
    public void handlePostExecute(boolean isSuccess) {
        Log.d(TAG , "DB Update Success: " + isSuccess);
        Log.d(TAG, "DB Updated");
    }
}
