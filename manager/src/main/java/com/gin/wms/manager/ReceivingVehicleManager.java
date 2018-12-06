package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.VehicleData;

/**
 * Created by bintang on 4/3/2018.
 */

public class ReceivingVehicleManager extends Manager {
    public ReceivingVehicleManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainRcv() + "ReceivingVehicle", new UserContext(context), new ManagerSetup());
    }

    public void SaveReceivingVehicleToServer(VehicleData vehicleData, String driverName)throws Exception{
        String function = "CreateReceivingVehicle?driverName=" + driverName;
        restClient.put(function, vehicleData);
    }
}