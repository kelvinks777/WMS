package com.gin.ngemart.libsignin.handler;

import com.gin.ngemart.libsignin.FirebaseUserData;

/**
 * Created by manbaul on 2/19/2018.
 */

public interface IEmailRegistrationHandler {
    void onRegistrationError(Exception e);
    void onRegistrationSuccess(FirebaseUserData firebaseUserData);
}
