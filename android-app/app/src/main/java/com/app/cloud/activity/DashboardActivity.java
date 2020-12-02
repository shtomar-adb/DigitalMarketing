package com.app.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.cloud.R;
import com.app.cloud.background.DBAsyncTask;
import com.app.cloud.fragment.NotificationFragment;
import com.app.cloud.listeners.HandlePostExecuteListener;
import com.app.cloud.listeners.PushDialogListener;
import com.app.cloud.request.Action;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.ApplicationState;
import com.app.cloud.utility.Constants;
import com.app.cloud.utility.Util;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements PushDialogListener, HandlePostExecuteListener {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    String segmentName;
    String message;
    String title;

    @BindView(R.id.welcome_text)
    TextView welcome;

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        new AppSharedPref(this).putString(Constants.APP_STATE , ApplicationState.SIGNED_IN.toString());
        setSupportActionBar(toolbar);

        String name = new AppSharedPref(this).getString(Constants.USER_NAME);
        welcome.setText(String.format(getResources().getString(R.string.welcome),name));

        if(getIntent() != null ){
            Intent intent = getIntent();
            if(intent.getAction() != null && intent.getAction().equals(Constants.PUSH)){
                segmentName = intent.getStringExtra(Constants.SEGMENT_NAME);
                message = intent.getStringExtra(Constants.PUSH_MESSAGE);
                title = intent.getStringExtra(Constants.PUSH_TITLE);
                showMessageDialog(message,segmentName);
            }
            if(intent.getStringExtra(Constants.FROM_ACTIVITY)!= null && intent.getStringExtra(Constants.FROM_ACTIVITY).equals("RegisterActivity")){
                new DBAsyncTask(DashboardActivity.this , Action.DBINSERT , this).execute();
            }
        }
    }

    private void showMessageDialog(String message, String segmentName){
        FragmentManager fm = getSupportFragmentManager();
        NotificationFragment notificationDialog = new NotificationFragment(title,message,this);
        notificationDialog.show(fm, "fragment_notification_msg");
    }

    @OnClick(R.id.logout_btn)
    public void logout(){
        new AppSharedPref(this).clearPref();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void sendInterested() {
        sendUserActionToServer(true);
    }

    @Override
    public void sendNotInterested() {
        sendUserActionToServer(false);
    }

    private void sendUserActionToServer(boolean result) {
        Log.d(TAG, "sendUserActionToServer");
        String name = new AppSharedPref(this).getString(Constants.USER_NAME);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("segment", segmentName);
            jsonObject.put("interested", result);
        }catch (JSONException exception){
            Log.d(TAG , "Exception: "+ exception.getMessage());
        }

        if (Util.isNetworkAvailable(this)) {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://tv5nilluy7.execute-api.us-west-2.amazonaws.com/dev/digital_marketing_user_response_handler";

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            Log.d(TAG , "User Action Sent Successfully");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG , "User Action Sent Failed");
                }
            });
            queue.add(stringRequest);
        }
    }

    @Override
    public void handlePostExecute(boolean isSuccess) {

    }
}
