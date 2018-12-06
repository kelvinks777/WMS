package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;

public class ParkingAreaManager extends Manager {

    public ParkingAreaManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainRcv() + "ParkingArea", new UserContext(context), new ManagerSetup());

    }
    public String GetParkingInfoFromPoliceNo(String policeNo) throws Exception{
        String function="GetParkingInfoFromPoliceNo?policeNo=" + policeNo;
        String status = restClient.get(String.class, function);

        return status;
    }
}
