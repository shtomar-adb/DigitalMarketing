package com.app.cloud.utility;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.app.cloud.activity.DashboardActivity;
import com.app.cloud.activity.LoginActivity;

public class Util {

    public static Intent getIntent(Context context){
        String appState = new AppSharedPref(context).getString(Constants.APP_STATE);
        if(appState.equals(ApplicationState.SIGNED_IN.toString())){
            return new Intent(context, DashboardActivity.class);
        }else {
            return new Intent(context, LoginActivity.class);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
