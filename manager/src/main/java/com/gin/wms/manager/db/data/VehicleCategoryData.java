package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.VehicleCategoryContract;

import java.io.Serializable;

/**
 * Created by bintang on 4/2/2018.
 */

public class VehicleCategoryData extends Data implements Serializable {
    public String trnId;
    public String vehicleCategoryId;
    public String name;

    @Override
    public Contract getContract() throws Exception {
        return new VehicleCategoryContract();
    }

    @Override
    public String toString() {
        return "VehicleCategoryData{" +
                "trnId='" + trnId + '\'' +
                ", vehicleCategoryId='" + vehicleCategoryId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
