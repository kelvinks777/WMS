package com.gin.ngemart.libsignin.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.gin.ngemart.libsignin.FirebaseUserData;
import com.gin.ngemart.libsignin.component.FirebaseModule;
import com.gin.ngemart.libsignin.handler.IEmailRegistrationHandler;
import com.gin.ngemart.libsignin.handler.IEmailResetPasswordHandler;
import com.gin.ngemart.libsignin.handler.IEmailUpdatePasswordHandler;
import com.gin.ngemart.libsignin.handler.IProcessHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.gin.ngemart.libsignin.handler.FirebaseErrorHandler.EMAIL_NOT_VERIFIED;

/**
 * Created by manbaul on 2/19/2018.
 */

public abstract class FirebaseProvider {
    final FirebaseAuth firebaseAuth;
    final IProcessHandler listener;
    final Context context;
    final AppCompatActivity activity;
    private DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReference("users");

    public FirebaseProvider(Context context, AppCompatActivity activity, FirebaseAuth firebaseAuth, IProcessHandler listener) {
        this.firebaseAuth = firebaseAuth;
        this.listener = listener;
        this.context = context;
        this.activity = activity;
    }

    void authenticate(AuthCredential authCredential, final FirebaseUserData firebaseUserData) throws Exception {
        listener.showProgressDialog();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            listener.onSignInError(task.getException());
                        } else {
                            final FirebaseUser firebaseUser = task.getResult().getUser();
                            saveUserToDb(firebaseUser.getUid(), firebaseUserData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        firebaseUser.getIdToken(false).addOnCompleteListener(activity, new OnCompleteListener<GetTokenResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (!task.isSuccessful()) {
                                                    listener.onSignInError(task.getException());
                                                } else {
                                                    listener.onSignInSuccess(firebaseUser, task.getResult().getToken());
                                                }
                                            }
                                        });
                                    } else {
                                        listener.onSignInError(databaseError.toException());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    void authenticate(String email, String password) throws Exception {
        if (email.isEmpty() || password.isEmpty())
            throw new NullPointerException("Username or password is empty.");

        listener.showProgressDialog();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            listener.onSignInError(task.getException());
                        } else {
                            final FirebaseUser firebaseUser = task.getResult().getUser();
                            if (!firebaseUser.isEmailVerified()) {
                                firebaseUser.sendEmailVerification();
                                listener.onSignInError(new Exception(EMAIL_NOT_VERIFIED));
                                return;
                            }

                            databaseReference.child(firebaseUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final FirebaseUserData firebaseUserData = dataSnapshot.getValue(FirebaseUserData.class);
                                            firebaseUser.getIdToken(false)
                                                    .addOnCompleteListener(activity, new OnCompleteListener<GetTokenResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                            if (task.isSuccessful()) {
                                                                listener.onSignInSuccess(firebaseUser, task.getResult().getToken());
                                                            } else {
                                                                listener.onSignInError(task.getException());
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            listener.onSignInError(databaseError.toException());
                                        }
                                    });
                        }
                    }
                });
    }

    void signOut() {
        firebaseAuth.signOut();
    }

    void register(final String email, final String password, final String name, final IEmailRegistrationHandler handler) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handler.onRegistrationError(e);
                    }
                })
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser firebaseUser = authResult.getUser();
                        firebaseUser.sendEmailVerification()
                                .addOnFailureListener(activity, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        handler.onRegistrationError(e);
                                    }
                                })
                                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final FirebaseUserData firebaseUserData = new FirebaseUserData();
                                        firebaseUserData.email = email;
                                        firebaseUserData.fullName = name;

                                        saveUserToDb(firebaseUser.getUid(), firebaseUserData, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    handler.onRegistrationSuccess(firebaseUserData);
                                                } else {
                                                    handler.onRegistrationError(databaseError.toException());
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                });
    }

    void resetPassword(final String email, final IEmailResetPasswordHandler handler) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            handler.onResetPasswordSuccess(email);
                        else
                            handler.onResetPasswordError(task.getException());
                    }
                });
    }

    void updatePassword(final String strNewPassword, final IEmailUpdatePasswordHandler handler){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            user.updatePassword(strNewPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                handler.onUpdatePasswordSuccess();
                            else
                                handler.onUpdatePasswordError(task.getException());
                        }
                    });
        }else{
            handler.onUpdatePasswordError(new Exception("firebaseAuth is null"));
        }
    }

    void saveUserToDb(final String uid, final FirebaseUserData firebaseUserData, final DatabaseReference.CompletionListener completionListener) {
        databaseReference
                .child(uid)
                .setValue(firebaseUserData, completionListener);
    }
}
