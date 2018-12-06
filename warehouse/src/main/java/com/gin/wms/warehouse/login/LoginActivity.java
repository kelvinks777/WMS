package com.gin.wms.warehouse.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.libsignin.component.FacebookModule;
import com.gin.ngemart.libsignin.component.FirebaseModule;
import com.gin.ngemart.libsignin.component.GoogleModule;
import com.gin.ngemart.libsignin.component.ISignInComponent;
import com.gin.ngemart.libsignin.component.SignInComponent;
import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.gin.ngemart.libsignin.provider.FacebookProvider;
import com.gin.ngemart.libsignin.provider.GoogleProvider;
import com.gin.wms.manager.TokenLocalManager;
import com.gin.wms.manager.config.FileConfigReader;
import com.gin.wms.warehouse.BuildConfig;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.SwitcherActivity;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.google.firebase.auth.FirebaseUser;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends WarehouseActivity implements View.OnClickListener {

    private ISignInComponent signInComponent;
    private boolean contentViewWasSet;

    private IProcessHandler processHandler = new IProcessHandler() {
        @Override
        public void onSignInSuccess(FirebaseUser firebaseUser, String firebaseToken) {
            try {
                LoginHandler loginHandler = new LoginHandler(getApplicationContext(), getString(R.string.fcmSenderId));
                ThreadStart(new ThreadHandler<Boolean>() {
                    @Override
                    public void onPrepare() throws Exception {
                        dismissProgressDialog();
                        startProgressDialog(getString(R.string.registration_progress_content), NgemartActivity.ProgressType.SPINNER);
                    }

                    @Override
                    public Boolean onBackground() throws Exception {
                        loginHandler.start(firebaseUser, firebaseToken);
                        return true;
                    }

                    @Override
                    public void onError(Exception e) {
                        try {
                            if (e.getMessage().contains("Unauthorized"))
                                showInfoDialog(getString(R.string.common_information_title), getString(R.string.user_has_not_been_validated));
                            else if (e.getMessage().contains("[OPERATORSTATUS]"))
                                showInfoDialog(getString(R.string.common_information_title), getString(R.string.user_not_yet_present));
                            else
                                showErrorDialog(e);

                        } catch (Exception ex) {
                            showErrorDialog(e);
                        }
                    }

                    @Override
                    public void onSuccess(Boolean data) throws Exception {
                        Intent intent = new Intent(getApplicationContext(), SwitcherActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFinish() {
                        dismissProgressDialog();
                        signInComponent.signOutAll();
                    }
                });

            } catch (Exception e) {
                showErrorDialog(e);
            }
        }

        @Override
        public void onSignInError(Exception e) {
            dismissProgressDialog();
            showErrorDialog(e);
        }

        @Override
        public void showProgressDialog() {
            startProgressDialog(getString(R.string.registration_progress_content), ProgressType.SPINNER);
        }
    };

    //region onCreate
    //region storage permission
    @Override
    protected void OnPermissionStorageDenied() {
        try {
            super.OnPermissionStorageDenied();
            showView();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    @Override
    protected void OnPermissionStorageGranted() {
        try {
            super.OnPermissionStorageGranted();
            FileConfigReader.setHostApiFromFile(this);
            showView();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }
    //endregion

    private void showView() throws Exception {
        setView();
        onCreate();
    }

    private void setView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        contentViewWasSet = true;
    }

    protected void onCreate() throws Exception {
        if (checkPreviousToken()) {
            startActivity(SwitcherActivity.class);
        } else {
            if (!contentViewWasSet) setView();
            initComponent();
            initLoginComponent();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);
            //Fabric.with(this, new Crashlytics());

            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                    .build();
            Fabric.with(this, crashlyticsKit);
            if (BuildConfig.DEBUG)
                RequestStoragePermission();
            else
                showView();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void initLoginComponent() throws Exception {
        if (signInComponent == null)
            signInComponent = SignInComponent.builder()
                    .init(getApplicationContext(), this)
                    .googleModule(new GoogleModule(processHandler, getString(R.string.googleClientId)))
                    .firebaseModule(new FirebaseModule())
                    .facebookModule(new FacebookModule(processHandler))
                    .build();
    }

    private void initComponent() throws Exception {
        Button btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        Button btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);

        btnGoogleSignIn.setOnClickListener(this);
        btnFacebookSignIn.setOnClickListener(this);
    }

    private boolean checkPreviousToken() throws Exception {
        String existingToken = new TokenLocalManager(getApplicationContext()).GetNgToken();
        if (existingToken == null || existingToken.isEmpty())
            return false;
        else
            return true;
    }
    //endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == GoogleProvider.REQ_CODE_SIGN_IN)
                    signInComponent.getGoogle().onActivityResult(requestCode, resultCode, data);
                else if (requestCode == FacebookProvider.FACEBOOK_LOGIN_REQUEST_CODE)
                    signInComponent.getFacebook().onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                showErrorDialog(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!new TokenLocalManager(getApplicationContext()).GetNgToken().isEmpty())
                super.onBackPressed();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnGoogleSignIn:
                    signInComponent.getGoogle().signIn();
                    break;
                case R.id.btnFacebookSignIn:
                    signInComponent.getFacebook().signIn();
                    break;
            }

        } catch (Exception e) {
            showErrorDialog(e);
        }
    }
}
