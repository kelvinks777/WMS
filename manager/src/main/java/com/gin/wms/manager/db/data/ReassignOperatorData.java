package com.gin.wms.manager.db.data;

/**
 * Created by manbaul on 3/23/2018.
 */

public class ReassignOperatorData {
    public String docUri;
    public String docNo;
    public String id;
    public String receivingOrderNo;
    public String policeNoBfr;
    public String docNoBfr;

    public ReassignOperatorData() {}
    public ReassignOperatorData(String docUri, String docNo, String id, String receivingOrderNo, String policeNoBfr, String docNoBfr){
        this.docUri = docUri;
        this.docNo = docNo;
        this.id = id;
        this.receivingOrderNo = receivingOrderNo;
        this.policeNoBfr = policeNoBfr;
        this.docNoBfr = docNoBfr;
    }
}
