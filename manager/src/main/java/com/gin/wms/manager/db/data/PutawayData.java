package com.gin.wms.manager.db.data;

import java.util.Date;

/**
 * Created by manbaul on 3/26/2018.
 */

public class PutawayData {
    public static String location;

    public static class PutawayTaskData {
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