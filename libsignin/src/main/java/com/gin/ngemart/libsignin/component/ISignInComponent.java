package com.gin.ngemart.libsignin.component;

import com.gin.ngemart.libsignin.provider.EmailAuthProvider;
import com.gin.ngemart.libsignin.provider.FacebookProvider;
import com.gin.ngemart.libsignin.provider.GoogleProvider;

/**
 * Created by manbaul on 2/19/2018.
 */

public interface ISignInComponent {
    GoogleProvider getGoogle();
    EmailAuthProvider getEmail();
    FacebookProvider getFacebook();
    void signOutAll();
}
