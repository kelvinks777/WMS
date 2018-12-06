package com.gin.ngemart.libsignin.component;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.gin.ngemart.libsignin.provider.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public class EmailAuthModule {

    private final IProcessHandler listener;

    public EmailAuthModule(IProcessHandler listener) {
        this.listener = listener;
    }

    EmailAuthProvider provideEmailProvider(Context context, AppCompatActivity activity, FirebaseAuth firebaseAuth) {
        return new EmailAuthProvider(context, activity, firebaseAuth, listener);
    }
}
