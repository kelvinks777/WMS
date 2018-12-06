package com.gin.wms.warehouse.login;

import android.content.Context;

import com.bosnet.ngemart.libgen.Common;
import com.gin.ngemart.baseui.component.ImageToStringDownloader;
import com.gin.wms.manager.OperatorManager;
import com.gin.wms.manager.TokenLocalManager;
import com.gin.wms.manager.UserManager;
import com.gin.wms.manager.db.data.TokenData;
import com.gin.wms.warehouse.base.GenConstants;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by manbaul on 2/20/2018.
 */

public class LoginHandler {
    private Context context;
    private UserManager userAnonymousManager;
    private UserManager userManager;
    private OperatorManager operatorManager;
    private FirebaseUser firebaseUser;
    private String fcmSenderId;

    public LoginHandler(Context context, String fcmSenderId) throws Exception {
        this.context = context;
        this.fcmSenderId = fcmSenderId;
        userAnonymousManager = new UserManager(this.context);
    }

    public void start(final FirebaseUser firebaseUser, final String firebaseToken) throws Exception {
        try {
            this.firebaseUser = firebaseUser;
            TokenData tokenData = userAnonymousManager.SignIn(firebaseToken, GenConstants.APP_ID);
            new TokenLocalManager(context).SaveNgToken(tokenData.token, GenConstants.APP_ID);
            userManager = new UserManager(context);
            userManager.Get();
            operatorManager = new OperatorManager(context);
            operatorManager.getOperatorStatus();
            DownloadProfileImage();
        } catch (Exception e) {
            new TokenLocalManager(context).SaveNgToken("", "");
            if (userManager == null)
                userManager = new UserManager(context);

            userManager.SignOut();

            throw e;
        }
    }

    private void DownloadProfileImage() {
        ImageToStringDownloader imageToStringDownloader = new ImageToStringDownloader(image -> {
            try {
                userManager.SaveProfileImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imageToStringDownloader.execute(firebaseUser.getPhotoUrl().toString());
    }

}
