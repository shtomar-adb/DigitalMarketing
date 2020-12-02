package com.app.cloud.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cloud.database.DatabaseAccess;
import com.app.cloud.listeners.HandlePostExecuteListener;
import com.app.cloud.request.Action;

public class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = DBAsyncTask.class.getSimpleName();
    private Context context;
    private Action action;
    HandlePostExecuteListener listener;

    public DBAsyncTask(Context context , Action action , HandlePostExecuteListener listener){
        this.context = context;
        this.action = action;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG , "Executing doInBackground...");
        boolean isSuccess = false;
        DatabaseAccess.getInstance(context).dbInteraction(action);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
       listener.handlePostExecute(isSuccess);
    }

}
