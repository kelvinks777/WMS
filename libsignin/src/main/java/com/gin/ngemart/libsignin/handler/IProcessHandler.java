package com.gin.ngemart.libsignin.handler;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by manbaul on 2/19/2018.
 */

public interface IProcessHandler {
    void onSignInSuccess(FirebaseUser firebaseUser, String firebaseToken);
    void onSignInError(Exception e);
    void showProgressDialog();
}
