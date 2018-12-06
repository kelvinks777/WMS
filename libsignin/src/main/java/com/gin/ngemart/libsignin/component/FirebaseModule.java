package com.gin.ngemart.libsignin.component;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public class FirebaseModule {

    FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}