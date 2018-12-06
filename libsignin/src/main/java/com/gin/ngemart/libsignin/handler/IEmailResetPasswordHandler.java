package com.gin.ngemart.libsignin.handler;

/**
 * Created by manbaul on 2/19/2018.
 */

public interface IEmailResetPasswordHandler {
    void onResetPasswordError(Exception e);
    void onResetPasswordSuccess(String email);
}
