package com.gin.ngemart.libsignin.component;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.gin.ngemart.libsignin.provider.GoogleProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public class GoogleModule {
    private final IProcessHandler listener;
    private final String google_client_id;

    public GoogleModule(IProcessHandler listener, String google_client_id) {
        this.listener = listener;
        this.google_client_id = google_client_id;
    }

    GoogleSignInOptions provideGso(Context context) {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(google_client_id)
                .build();
    }

    GoogleApiClient provideGac(Context context, AppCompatActivity activity, GoogleSignInOptions gso) {
        return new GoogleApiClient
                .Builder(context)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (connectionResult.getErrorCode() != ConnectionResult.CANCELED)
                            listener.onSignInError(new Exception("Google Play Services error."));
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        listener.onSignInError(new Exception("Connection suspended."));
                    }
                })
                .build();
    }

    GoogleProvider provideGoogle(Context context, AppCompatActivity activity, GoogleApiClient googleApiClient, FirebaseAuth firebaseAuth) {
        return new GoogleProvider(context, activity, googleApiClient, listener, firebaseAuth);
    }

}
