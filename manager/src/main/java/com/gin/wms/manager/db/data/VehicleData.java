package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.VehicleContract;

import java.io.Serializable;

/**
 * Created by bintang on 3/29/2018.
 */

public class VehicleData extends Data implements Serializable{
    public String policeNumber;
    public String vehicleOwnerId;
    public String type;
    public String workplaceId;
    public String category1;
    public String category2;

    @Override
    public Contract getContract() throws Exception {
        return new VehicleContract();
    }

    @Override
    public String toString() {
        return "VehicleData{" +
                "policeNumber='" + policeNumber + '\'' +
                ", vehicleOwnerId='" + vehicleOwnerId + '\'' +
                ", type='" + type + '\'' +
                ", workplaceId='" + workplaceId + '\'' +
                ", category1='" + category1 + '\'' +
                ", category2='" + category2 + '\'' +
                '}';
    }
}
