package com.gin.ngemart.libsignin.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gin.ngemart.libsignin.FirebaseUserData;
import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by manbaul on 2/19/2018.
 */

public class FacebookProvider extends FirebaseProvider implements FacebookCallback<LoginResult> {
    public static final int FACEBOOK_LOGIN_REQUEST_CODE = 64206;

    private static final String GRAP_REQUEST_BUNDLE_KEY = "fields";
    private static final String GRAP_REQUEST_BUNDLE_VALUE = "email,first_name,name,picture.sourceType(large)";

    private final CallbackManager callbackManager;

    public FacebookProvider(Context context, AppCompatActivity activity, IProcessHandler listener, FirebaseAuth firebaseAuth) {
        super(context, activity, firebaseAuth, listener);
        this.callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws Exception {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signIn() {
        LoginManager.getInstance().logInWithReadPermissions(activity, getPermissionNeeds());
    }

    public void signOut() {
        super.signOut();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        final FirebaseProvider firebaseProvider = this;
        GraphRequest.GraphJSONObjectCallback requestCallback = new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    AuthCredential credential = FacebookAuthProvider
                            .getCredential(loginResult
                                    .getAccessToken()
                                    .getToken());
                    FirebaseUserData firebaseUserData = GetFirebaseUserDataFromJSONObject(object);
                    firebaseProvider.authenticate(credential, firebaseUserData);
                } catch (Exception e) {
                    listener.onSignInError(e);
                }
            }
        };

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), requestCallback);
        Bundle parameters = new Bundle();
        parameters.putString(GRAP_REQUEST_BUNDLE_KEY, GRAP_REQUEST_BUNDLE_VALUE);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private FirebaseUserData GetFirebaseUserDataFromJSONObject(JSONObject object) throws Exception {
        FirebaseUserData firebaseUserData = new FirebaseUserData();
        if (object.has("email"))
            firebaseUserData.email = object.getString("email");
        else
            throw new NullPointerException("Kami tidak berhasil mendapatkan email dari account facebook Anda");

        if (object.has("first_name"))
            firebaseUserData.nickName = object.getString("first_name");

        if (object.has("name"))
            firebaseUserData.fullName = object.getString("name");

        if (object.has("picture"))
            firebaseUserData.photoUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

        return firebaseUserData;
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        listener.onSignInError(error);
    }

    private List<String> getPermissionNeeds() {
        return Arrays.asList(
                Permissions.USER_FRIENDS,
                Permissions.PUBLIC_PROFILE,
                Permissions.EMAIL);
    }


    private static final class Permissions {
        static final String PUBLIC_PROFILE = "public_profile";
        static final String USER_FRIENDS = "user_friends";
        static final String EMAIL = "email";
    }
}