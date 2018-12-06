package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.ManualMutationContract;

import java.io.Serializable;
import java.util.Date;

public class ManualMutationData extends Data implements Serializable {
    public String recOrderNo;
    public String trxTypeId;
    public String docStatus;
    public Date created;
    public Date lastUpdated;
    public int orderType;
    public int operator;
    public String productId;
    public double qty;
    public String sourcePalletNo;
    public String sourceBinId;
    public String destPalletNo;
    public String destBinId;
    public String uOMId;
    public String clientId;
    public String clientLocationId;
    public Date expiredDate;
    public String refDocUri;
    public int finished;

    @Override
    public Contract getContract() throws Exception {
        return new ManualMutationContract();
    }

    @Override
    public String toString() {
        return "manualMutationData{"+
                "recOrderNo='"+ recOrderNo + '\'' +
                ", trxTypeId='" + trxTypeId + '\'' +
                ", docStatus='" + docStatus + '\'' +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                ", orderType=" + orderType +
                ", operator=" + operator +
                ", productId='" + productId + '\'' +
                ", qty=" + qty +
                ", sourcePalletNo='" + sourcePalletNo + '\'' +
                ", sourceBinId='" + sourceBinId + '\'' +
                ", destPalletNo='" + destPalletNo + '\'' +
                ", destBinId='" + destBinId + '\'' +
                ", uOMId='" + uOMId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientLocationId='" + clientLocationId + '\'' +
                ", expiredDate=" + expiredDate +
                ", refDocUri='" + refDocUri + '\'' +
                ", finished=" + finished +
                '}';
    }

}
