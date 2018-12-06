package com.gin.ngemart.libsignin.provider;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.FirebaseUserData;
import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by manbaul on 2/19/2018.
 */

public class GoogleProvider extends FirebaseProvider {
    public static final int REQ_CODE_SIGN_IN = 9001;
    static final int RESULT_OK = -1;
    private final GoogleApiClient googleApiClient;

    public GoogleProvider(Context context, AppCompatActivity activity, GoogleApiClient googleApiClient, IProcessHandler listener, FirebaseAuth firebaseAuth) {
        super(context, activity, firebaseAuth, listener);
        this.googleApiClient = googleApiClient;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws Exception {
        if (requestCode == REQ_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account == null || account.getIdToken() == null)
                    throw new NullPointerException("Account or IdToken is null");

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                super.authenticate(credential, getFirebaseUserDataFromGoogleSignInAccount(account));
            }
        }
    }

    private FirebaseUserData getFirebaseUserDataFromGoogleSignInAccount(GoogleSignInAccount account) {
        FirebaseUserData result = new FirebaseUserData();
        result.email = account.getEmail();
        result.nickName = account.getGivenName() == null ? "" : account.getGivenName();
        result.fullName = account.getDisplayName();
        result.photoUrl = account.getPhotoUrl() == null ? "" : account.getPhotoUrl().toString();
        return result;
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, REQ_CODE_SIGN_IN);
    }

    @Override
    public void signOut() {
        super.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    public void revokeAccess() {
        super.signOut();
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }
}
