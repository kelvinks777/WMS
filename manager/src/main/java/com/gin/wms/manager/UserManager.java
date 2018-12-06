package com.gin.wms.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.bosnet.ngemart.libgen.NgContext;
import com.bosnet.ngemart.libgen.Setup;
import com.gin.wms.manager.config.ContractManager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.UserContract;
import com.gin.wms.manager.db.data.TokenData;
import com.gin.wms.manager.db.data.UserData;

import java.util.List;

/**
 * Created by manbaul on 2/19/2018.
 */

public class UserManager extends Manager {
    private static final String USER_PROVIDER = "com.gin.wms.manager.user_provider";
    private static final String USER_CHECKER = "com.gin.wms.manager.user_checker";

    private SharedPreferences sharedPref;

    public UserManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainSm() + "Me", new UserContext(context), new ManagerSetup());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void SaveUserDataToLocal(final UserData userData) throws Exception {
        DbExecuteWrite(new IDbExecuteWrite() {
            @Override
            public void DoAction(DbClient dbClient) throws Exception {
                dbClient.Save(userData);
            }
        });
    }

    public UserData GetUserDataFromLocal() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<UserData> userDataList = dbClient.Query(UserData.class, UserContract.Query.SELECT);
            if (!userDataList.isEmpty())
                return userDataList.get(0);

            return null;
        });
    }

    public TokenData SignIn(String signInToken, String appId) throws Exception {
        if (signInToken == null || signInToken.equals(""))
            throw new NullPointerException("accessToken is null");
        TokenData tokenData;
        try {
            tokenData = restClient.get(TokenData.class, "SignIn?referralId=&signInToken=" + signInToken + "&appId=" + appId +"&appVersion=dummyVersion");
            new UserContext(context).Clear();
        } catch (Exception e) {
            if (e.getMessage().contains("expired"))
                throw new NullPointerException("access " + "" + "token expired");
            else
                throw new Exception(e.getMessage());
        }

        validateData(tokenData);

        return tokenData;
    }

    public void SignOut() throws Exception {
        DbExecuteWrite(dbClient -> {
            List<Contract> listOfContracts = ContractManager.getListContract();
            for (Contract contract : listOfContracts)
                dbClient.Execute("delete from " + contract.getTableName());
        });
        new UserContext(context).Clear();
    }

    public void SaveProvider(String provider) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_PROVIDER, provider);
        editor.apply();
    }

    public String GetProvider() {
        return sharedPref.getString(USER_PROVIDER, "");
    }

    public void SaveProfileImage(String profileImage) throws Exception {
        UserData userData = GetUserDataFromLocal();
        if (userData != null) {
            DbClient dbcUser = GetDbClient();
            try {
                if(profileImage != null)
                    userData.profileImage = profileImage;
                dbcUser.BeginTransaction();
                dbcUser.Save(userData);
                dbcUser.CommitTransaction();
            } finally {
                dbcUser.ReleaseConnection();
            }
        }
    }

    public UserData Get() throws Exception {
        UserData userData = restClient.get(UserData.class, "getUser");
        if (userData != null) {
            UserData oldUserData = GetUserDataFromLocal();
            if (oldUserData != null) {
                userData.profileImage = oldUserData.profileImage;
            } else {
                userData.profileImage = "";
            }
            SaveUserDataToLocal(userData);
        }

        return userData;
    }

    public void Delete() throws Exception {
        DbExecuteWrite(dbClient -> dbClient.DeleteAll(UserData.class));
    }

//    public void saveCheckerId(String checkerId) {
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString(USER_CHECKER, checkerId);
//        editor.apply();
//    }
//
//    public String getCheckerId() {
//        return sharedPref.getString(USER_CHECKER, "");
//    }

}
