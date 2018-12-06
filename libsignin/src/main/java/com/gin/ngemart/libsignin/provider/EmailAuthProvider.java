package com.gin.ngemart.libsignin.provider;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.handler.IEmailRegistrationHandler;
import com.gin.ngemart.libsignin.handler.IEmailResetPasswordHandler;
import com.gin.ngemart.libsignin.handler.IEmailUpdatePasswordHandler;
import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public class EmailAuthProvider extends FirebaseProvider {

    public EmailAuthProvider(Context context, AppCompatActivity activity, FirebaseAuth firebaseAuth, IProcessHandler listener) {
        super(context, activity, firebaseAuth, listener);
    }

    @Override
    public void register(String email, String password, String name, IEmailRegistrationHandler handler) {
        super.register(email, password, name, handler);
    }

    @Override
    public void updatePassword(String strNewPassword, IEmailUpdatePasswordHandler handler){
        super.updatePassword(strNewPassword, handler);
    }

    @Override
    public void resetPassword(String email, IEmailResetPasswordHandler handler) {
        super.resetPassword(email, handler);
    }

    @Override
    public void authenticate(String email, String password) throws Exception {
        super.authenticate(email, password);
    }

    @Override
    public void signOut() {
        super.signOut();
    }
}
