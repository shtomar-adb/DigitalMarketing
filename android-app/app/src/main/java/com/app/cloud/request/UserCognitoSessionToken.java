package com.app.cloud.request;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoAccessToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoIdToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;

public class UserCognitoSessionToken {

    private CognitoAccessToken accessToken;
    private CognitoIdToken idToken;
    private CognitoRefreshToken refreshToken;
    private String userName;

    public UserCognitoSessionToken(CognitoAccessToken accessToken, CognitoIdToken idToken, CognitoRefreshToken refreshToken,
            String userName){
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
    }

    public CognitoAccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(CognitoAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public CognitoIdToken getIdToken() {
        return idToken;
    }

    public void setIdToken(CognitoIdToken idToken) {
        this.idToken = idToken;
    }

    public CognitoRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(CognitoRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
