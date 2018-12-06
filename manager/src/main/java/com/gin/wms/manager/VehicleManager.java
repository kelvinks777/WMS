package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.VehicleCategoryContract;
import com.gin.wms.manager.db.contract.VehicleTypeContract;
import com.gin.wms.manager.db.data.VehicleCategoryData;
import com.gin.wms.manager.db.data.VehicleData;
import com.gin.wms.manager.db.data.VehicleTypeData;

import java.util.List;

/**
 * Created by bintang on 3/29/2018.
 */

public class VehicleManager extends Manager {
    public VehicleManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainMs() + "Vehicle", new UserContext(context), new ManagerSetup());
    }

    //region Vehicle
    public VehicleData getVehicleFromServer(String policeNumber)throws Exception{
        String function = "GetVehicleData?policeNumber=" + policeNumber;
        return restClient.get(VehicleData.class, function);
    }

    public void saveToLocal(final VehicleData vehicleData)throws Exception{
        DbExecuteWrite(dbClient -> dbClient.Save(vehicleData));
    }

    public void saveToServer(final VehicleData vehicleData)throws Exception{
        restClient.post("", vehicleData);
    }
    //endregion

    //region VehicleType
    public List<VehicleTypeData> getVehicleTypeListFromServer()throws Exception{
        return restClient.getList(VehicleTypeData[].class,"GetVehicleTypeList");
    }

    public List<VehicleTypeData> getVehicleTypeListFromLocal()throws Exception{
        String query = "SELECT * FROM " + new VehicleTypeContract().getTableName();
        return DbExecuteRead(dbClient -> dbClient.Query(VehicleTypeData.class, query));
    }

    public VehicleTypeData getVehicleTypeDataByIdFromLocal(final String vehicleTypeId)throws Exception{
        return DbExecuteRead(dbClient -> dbClient.Find(VehicleTypeData.class, vehicleTypeId));
    }

    public void saveVehicleTypeListToLocal(List<VehicleTypeData> vehicleTypeDataList)throws Exception{
        if(vehicleTypeDataList != null){
            if(vehicleTypeDataList.size() > 0)
                justSaveVehicleType(vehicleTypeDataList);
        }
    }

    private void justSaveVehicleType(List<VehicleTypeData> vehicleTypeDataList) throws Exception{
        for(VehicleTypeData data : vehicleTypeDataList){
            DbExecuteWrite(dbClient -> dbClient.Save(data));
        }
    }
    //endregion

    //region VehicleCategory
    public List<VehicleCategoryData> getVehicleCategoryList()throws Exception{
        return restClient.getList(VehicleCategoryData[].class,"GetVehicleCategoryList");
    }

    public List<VehicleCategoryData> getVehicleCategoryListFromLocal()throws Exception{
        String query = "SELECT * FROM " + new VehicleCategoryContract().getTableName();
        return DbExecuteRead(dbClient -> dbClient.Query(VehicleCategoryData.class, query));
    }

    public VehicleCategoryData getVehicleCategoryDataByIdFromLocal(final String categoryId)throws Exception{
        String query = "SELECT * FROM " + new VehicleCategoryContract().getTableName() +
                        " WHERE " + VehicleCategoryContract.Column.VEHICLE_CATEGORY_ID + " = '" + categoryId + "'";
       List<VehicleCategoryData> resultList = DbExecuteRead(dbClient -> dbClient.Query(VehicleCategoryData.class, query));
       if(resultList.size() > 0)
           return resultList.get(0);
       else
           return null;
    }

    public void saveVehicleCategoryListToLocal(List<VehicleCategoryData> vehicleCategoryDataList)throws Exception{
        if(vehicleCategoryDataList != null){
            if(vehicleCategoryDataList.size() > 0)
                justSaveVehicleCategory(vehicleCategoryDataList);
        }
    }

    private void justSaveVehicleCategory(List<VehicleCategoryData> vehicleCategoryDataList) throws Exception{
        for(VehicleCategoryData data : vehicleCategoryDataList){
            DbExecuteWrite(dbClient -> dbClient.Save(data));
        }
    }
    //endregion
}
