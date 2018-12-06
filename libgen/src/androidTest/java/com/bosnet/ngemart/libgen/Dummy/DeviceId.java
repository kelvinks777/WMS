package com.bosnet.ngemart.libgen.Dummy;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;

/**
 * Purpose :
 */
public class DeviceId extends Data {
    public String RowKey;
    public String UID;
    public Float price;
    public int quantity;
    public double amount;
    public String id;
    public byte[] image;


    @Override
    public Contract getContract() throws Exception {
        return new DeviceIdContract();
    }

    @Override
    public String toString() {
        return "DeviceId{" +
                "RowKey='" + RowKey + '\'' +
                ", UID='" + UID + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", amount=" + amount +
                '}';
    }
}
