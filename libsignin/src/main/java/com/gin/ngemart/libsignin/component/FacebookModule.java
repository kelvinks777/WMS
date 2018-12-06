package com.gin.ngemart.libsignin.component;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.gin.ngemart.libsignin.provider.FacebookProvider;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public class FacebookModule {

    private final IProcessHandler listener;

    public FacebookModule(IProcessHandler listener) {
        this.listener = listener;
    }

    FacebookProvider getFacebook(Context context, AppCompatActivity activity, FirebaseAuth firebaseAuth) {
        return new FacebookProvider(context, activity, listener, firebaseAuth);
    }
}
