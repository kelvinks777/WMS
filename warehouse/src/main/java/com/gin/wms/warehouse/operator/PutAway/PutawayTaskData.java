package com.gin.wms.warehouse.operator.PutAway;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/26/2018.
 */

public class PutawayTaskData {
    public static String location;
    public static Pallet pallet = new Pallet();

    public static class Pallet{
        public static String product;
        public static String palletNo;
        public static int qty;
    }

    public List<Product> lstProduct = new ArrayList<>();

    public static class Product implements Serializable {
        public String productId;
        public String productName;
        public String uomId;    //BASE
        public String uomPalletId;
        public String compUomId; // Urutan UOM
        public List<String> compUomItem = new ArrayList<>();
        public List<Integer> convertionValue = new ArrayList<>();
    }

    public static class PutawayTask implements Serializable {
        public String PutawayId;
        public String operatorId;
        public String ProductId;    //BASE
        public String DestBinId;
        public String SourceBinId; // Urutan UOM
        public Date StartTime;
        public Date EndTime;
        public Float Qty;    //BASE
        public String PalletNo;
        public String ReceivingDocumentId; // Urutan UOM
        public Integer StatusAsInt;
        public Integer TypeAsInt;    //BASE
    }
}
