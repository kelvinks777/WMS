package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.VehicleTypeContract;

import java.io.Serializable;

/**
 * Created by bintang on 4/2/2018.
 */

public class VehicleTypeData extends Data implements Serializable{
    public String vehicleTypeId;
    public String name;
    public double length;
    public double width;
    public double height;
    public String uomLength;
    public String uomWidth;
    public String uomHeight;

    @Override
    public Contract getContract() throws Exception {
        return new VehicleTypeContract();
    }


    @Override
    public String toString() {
        return "VehicleTypeData{" +
                "vehicleTypeId='" + vehicleTypeId + '\'' +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", uomLength='" + uomLength + '\'' +
                ", uomWidth='" + uomWidth + '\'' +
                ", uomHeight='" + uomHeight + '\'' +
                '}';
    }
}
