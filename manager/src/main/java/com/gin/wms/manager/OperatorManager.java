package com.gin.wms.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.enums.OperatorStatusEnum;
import com.gin.wms.manager.db.data.enums.OperatorTypeEnum;

/**
 * Created by manbaul on 3/6/2018.
 */

public class OperatorManager extends Manager {
    private static final String OPERATOR_STATUS = "operatorStatus";
    private SharedPreferences sharedPref;

    public OperatorManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainMs() + "Operator", new UserContext(context), new ManagerSetup());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public OperatorStatusEnum getOperatorStatus() throws Exception {
        int opStatus = restClient.get(int.class, "GetOperatorStatus");
        saveOperatorStatusToLocal(opStatus);
        return OperatorStatusEnum.init(opStatus);
    }

    public OperatorTypeEnum getOperatorType() throws Exception {
        int opType = restClient.get(int.class, "GetOperatorType");
        return OperatorTypeEnum.init(opType);
    }

    private void saveOperatorStatusToLocal(int opStatus){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(OPERATOR_STATUS, opStatus);
        editor.apply();
    }

    public OperatorStatusEnum getOperatorStatusFromLocal(){
        int opStatus = sharedPref.getInt(OPERATOR_STATUS, 0);
        return OperatorStatusEnum.init(opStatus);
    }
}
