package com.gin.ngemart.libsignin.component;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.provider.EmailAuthProvider;
import com.gin.ngemart.libsignin.provider.FacebookProvider;
import com.gin.ngemart.libsignin.provider.GoogleProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by manbaul on 2/19/2018.
 */

public final class SignInComponent implements ISignInComponent {

    private FirebaseAuth firebaseAuth;
    private GoogleProvider googleProvider;
    private FacebookProvider facebookProvider;
    private EmailAuthProvider emailProvider;

    private SignInComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(Builder builder) {
        firebaseAuth = builder.firebaseModule.provideFirebaseAuth();

        if (builder.googleModule != null) {
            GoogleSignInOptions gso = builder.googleModule.provideGso(builder.appContext);
            GoogleApiClient gac = builder.googleModule.provideGac(builder.appContext, builder.activity, gso);
            googleProvider = builder.googleModule.provideGoogle(builder.appContext, builder.activity, gac, firebaseAuth);
        }

        if (builder.emailAuthModule != null) {
            emailProvider = builder.emailAuthModule.provideEmailProvider(builder.appContext, builder.activity, firebaseAuth);
        }

        if (builder.facebookModule != null) {
            facebookProvider = builder.facebookModule.getFacebook(builder.appContext, builder.activity, firebaseAuth);
        }
    }

    @Override
    public GoogleProvider getGoogle() {
        if (googleProvider == null)
            throw new IllegalStateException(GoogleProvider.class.getCanonicalName() + " must be set");

        return googleProvider;
    }

    @Override
    public FacebookProvider getFacebook() {
        if (facebookProvider == null)
            throw new IllegalStateException(FacebookProvider.class.getCanonicalName() + " must be set");

        return facebookProvider;
    }

    @Override
    public EmailAuthProvider getEmail() {
        if (emailProvider == null)
            throw new IllegalStateException(EmailAuthProvider.class.getCanonicalName() + " must be set");

        return emailProvider;
    }

    @Override
    public void signOutAll() {
        if (googleProvider != null)
            googleProvider.signOut();

        if (facebookProvider != null)
            facebookProvider.signOut();

        if (emailProvider != null)
            emailProvider.signOut();

    }

    public static final class Builder {
        private FirebaseModule firebaseModule;

        private GoogleModule googleModule;

        private FacebookModule facebookModule;

        private EmailAuthModule emailAuthModule;

        private Context appContext;

        private AppCompatActivity activity;

        public Builder init(Context appContext, AppCompatActivity activity) {
            this.appContext = appContext;
            this.activity = activity;

            return this;
        }

        public Builder firebaseModule(FirebaseModule firebaseModule) {
            if (firebaseModule == null)
                throw new IllegalStateException(FirebaseModule.class.getCanonicalName() + " must be set");

            this.firebaseModule = firebaseModule;
            return this;
        }

        public Builder googleModule(GoogleModule googleModule) {
            if (googleModule == null)
                throw new IllegalStateException(GoogleModule.class.getCanonicalName() + " must be set");

            this.googleModule = googleModule;
            return this;
        }

        public Builder facebookModule(FacebookModule facebookModule) {
            if (facebookModule == null)
                throw new IllegalStateException(FacebookModule.class.getCanonicalName() + " must be set");

            this.facebookModule = facebookModule;
            return this;
        }

        public Builder emailAuthModule(EmailAuthModule emailAuthModule) {
            if (emailAuthModule == null)
                throw new IllegalStateException(EmailAuthModule.class.getCanonicalName() + " must be set");

            this.emailAuthModule = emailAuthModule;
            return this;
        }

        public SignInComponent build() {

            if (firebaseModule == null) {
                throw new IllegalStateException(FirebaseModule.class.getCanonicalName() + " must be set");
            }

            return new SignInComponent(this);
        }
    }
}
