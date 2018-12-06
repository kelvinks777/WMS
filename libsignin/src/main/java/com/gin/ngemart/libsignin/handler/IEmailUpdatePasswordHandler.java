package com.gin.ngemart.libsignin.handler;

/**
 * Created by bintang on 3/22/2018.
 */

public interface IEmailUpdatePasswordHandler {
    void onUpdatePasswordError(Exception e);
    void onUpdatePasswordSuccess();
}
