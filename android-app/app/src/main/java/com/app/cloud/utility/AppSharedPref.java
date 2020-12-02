package com.app.cloud.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.cloud.request.User;
import com.app.cloud.request.UserCognitoSessionToken;
import com.google.gson.GsonBuilder;

public class AppSharedPref {
    private static final String APP_PREF = "app_pref";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public  AppSharedPref(Context context){
        this.context = context;
        this.sharedpreferences = context.getSharedPreferences(APP_PREF,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void putString(String key , String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sharedpreferences.getString(key , ApplicationState.SIGNED_OUT.toString());
    }

    public void putUser(User user){
        String userStr = new GsonBuilder().create().toJson(user);
        editor.putString(Constants.REGISTERED_USER , userStr);
        editor.commit();
    }

    public User getUser(){
        String userStr = sharedpreferences.getString(Constants.REGISTERED_USER,null);
        return new GsonBuilder().create().fromJson(userStr,User.class);
    }

    public void putUserSession(UserCognitoSessionToken user){
        String userStr = new GsonBuilder().create().toJson(user);
        editor.putString(Constants.REGISTERED_USER_SESSION , userStr);
        editor.commit();
    }

    public UserCognitoSessionToken getUserSession(){
        String userStr = sharedpreferences.getString(Constants.REGISTERED_USER_SESSION,null);
        return new GsonBuilder().create().fromJson(userStr,UserCognitoSessionToken.class);
    }

    public void clearPref(){
        editor.clear();
        editor.commit();
    }
}
