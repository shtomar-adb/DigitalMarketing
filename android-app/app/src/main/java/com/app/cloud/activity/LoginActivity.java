package com.app.cloud.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.regions.Regions;
import com.app.cloud.R;
import com.app.cloud.background.DBAsyncTask;
import com.app.cloud.fragment.ErrorHandlerFragment;
import com.app.cloud.fragment.NotificationFragment;
import com.app.cloud.listeners.HandlePostExecuteListener;
import com.app.cloud.request.Action;
import com.app.cloud.request.User;
import com.app.cloud.request.UserCognitoSessionToken;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;

import android.widget.EditText;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements HandlePostExecuteListener {
    private static final String TAG = LoginActivity.class.getSimpleName() ;
    String from_activity;
    CognitoUser cognitoUser;
    @BindView(R.id.email_edit_text)
    EditText emailAddress;
    @BindView(R.id.password_edit_text)
    EditText password;
    String pwd;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if(getIntent() != null){
            if(getIntent().getStringExtra(Constants.USER_ID) != null){
                emailAddress.setText(getIntent().getStringExtra(Constants.USER_ID));
            }
            if(getIntent().getStringExtra(Constants.FROM_ACTIVITY) != null){
                from_activity = getIntent().getStringExtra(Constants.FROM_ACTIVITY);
            }
        }
    }

    @OnClick(R.id.login_btn)
    public void onClick(){
        if(emailAddress.getText().toString().equals("") || password.getText().toString().equals("")){
            showErrorDialog("Fields cannot be empty");
        }else {
            String email = emailAddress.getText().toString();
            pwd = password.getText().toString();
            CognitoUserPool userPool = new CognitoUserPool(this,
                    Constants.POOL_ID,
                    Constants.APP_CLIENT_ID,
                    null,
                    Regions.US_WEST_2);

            cognitoUser = userPool.getUser(email);

            cognitoUser.getSessionInBackground(authenticationHandler);
        }
    }

    @OnClick(R.id.signUp_btn)
    public void signUp(){
        Log.d(TAG , "SignUp");
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Log.d(TAG, "Login Success");
            UserCognitoSessionToken cognitoSession = new UserCognitoSessionToken(userSession.getAccessToken(),userSession.getIdToken(),userSession.getRefreshToken(),
                    userSession.getUsername());
            new AppSharedPref(LoginActivity.this).putUserSession(cognitoSession);

            // Fetch the user details
            cognitoUser.getDetailsInBackground(getDetailsHandler);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, pwd, null);
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            authenticationContinuation.continueTask();
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG, "Login Failed: " + exception.getMessage());
            showErrorDialog(exception.getMessage());
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) { }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) { }
    };

    GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            // The user detail are in cognitoUserDetails
            CognitoUserAttributes userAttributes = cognitoUserDetails.getAttributes();
            Map<String,String> map = userAttributes.getAttributes();
            String name = map.get("name");
            String email = map.get("email");
            String phone_number = map.get("phone_number");
            String gender = map.get("gender");
            String birthdate = map.get("birthdate");

            user = new User(name,email,phone_number,birthdate,"28",gender);
            //new AppSharedPref(LoginActivity.this).putUser(user);
            new AppSharedPref(LoginActivity.this).putString(Constants.USER_NAME, name);
            getFirebaseToken();
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG , "Failed:"+exception.getMessage());
            // Fetch user details failed, check exception for the cause
        }
    };

    private void getFirebaseToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                        new AppSharedPref(LoginActivity.this).putString(Constants.FCM_TOKEN , token);

                        if(from_activity != null && from_activity.equals("RegisterActivity")){
                            Intent intent = new Intent(LoginActivity.this , DashboardActivity.class);
                            intent.putExtra(Constants.FROM_ACTIVITY, from_activity);
                            startActivity(intent);
                            finish();
                            //new DBAsyncTask(LoginActivity.this , Action.DBINSERT , LoginActivity.this).execute();
                        }
                        else {
                            new AppSharedPref(LoginActivity.this).putUser(user);
                            new DBAsyncTask(LoginActivity.this , Action.DBUPDATE , LoginActivity.this).execute();
                        }

                    }
                });
    }

    @Override
    public void handlePostExecute(boolean isSuccess) {
        Log.d(TAG , "DB Update Success: " + isSuccess);
        Intent intent = new Intent(LoginActivity.this , DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void showErrorDialog(String msg){
        FragmentManager fm = getSupportFragmentManager();
//        NotificationFragment fragment = new NotificationFragment("Black friday Sales","hdgjhkjegdegjdjnjb vdhguehdkbhcfhdgchjd dhgj" , null);
//        fragment.show(fm,Constants.ERROR_DIALOG_FRAGMENT);
        ErrorHandlerFragment errorDialogFragment = new ErrorHandlerFragment(msg);
        errorDialogFragment.show(fm, Constants.ERROR_DIALOG_FRAGMENT);
    }
}