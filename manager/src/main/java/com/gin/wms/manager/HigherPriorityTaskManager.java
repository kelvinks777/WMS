package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.CompUomItemContract;
import com.gin.wms.manager.db.contract.HigherPriorityTaskOperatorContract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskContract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskItemContract;
import com.gin.wms.manager.db.contract.UomConversionContract;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.HigherPriorityTaskData;
import com.gin.wms.manager.db.data.HigherPriorityTaskItemData;
import com.gin.wms.manager.db.data.UomConversionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/13/2018.
 */

public class HigherPriorityTaskManager extends Manager {
    public HigherPriorityTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "orderMonitoring", new UserContext(context), new ManagerSetup());
    }

    public List<HigherPriorityTaskData> getNewOrderMonitoringWithHigherPriorityThanRcvCheckerTasks() throws Exception {
        List<HigherPriorityTaskData> results = restClient.getList(HigherPriorityTaskData[].class,"GetNewOrderMonitoringWithHigherPriorityThanRcvCheckerTasks");
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(HigherPriorityTaskData.class);
            dbClient.DeleteAll(HigherPriorityTaskItemData.class);

            for (HigherPriorityTaskData higherPriorityTaskData :
                    results) {

                for (HigherPriorityTaskItemData product :
                        higherPriorityTaskData.lstProduct) {

                    product.policeNo = higherPriorityTaskData.policeNo;
                    product.refDocUri = higherPriorityTaskData.refDocUri;
                    product.refDocId = higherPriorityTaskData.refDocId;
                    product.compUomId = product.getCompUom().compUomId;
                    dbClient.Save(product.getCompUom());
                    for (CompUomItemData compUomItemData :
                            product.getCompUom().compUomItems) {
                        compUomItemData.compUomId = product.getCompUom().compUomId;
                        dbClient.Save(compUomItemData);
                    }

                    for (UomConversionData uomConversionData :
                            product.getCompUom().uomConversions) {
                        dbClient.Save(uomConversionData);
                    }

                    product.palletQty = product.getCalcPalletByQty();
                    product.qtyCompUomValue = product.getCompUomValueFromQty();
                    dbClient.Save(product);
                }

                dbClient.Save(higherPriorityTaskData);
            }
        });
        return results;
    }

    public HigherPriorityTaskData getLocalHigherPriorityTaskData(String refDocUri, String refDocId) throws Exception {
        return DbExecuteRead(dbClient -> {
            HigherPriorityTaskData result = dbClient.Find(HigherPriorityTaskData.class, new String [] {refDocUri, refDocId});
            if (result != null) {
                result.lstProduct = getLocalCheckerTaskItems(refDocUri, refDocId);
            }
            return result;
        });
    }

    public List<HigherPriorityTaskData> getListOfLocalHigherPriorityTaskData() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<HigherPriorityTaskData> results = dbClient.Query(HigherPriorityTaskData.class, HigherPriorityTaskContract.Query.getSelectAll());
            for (HigherPriorityTaskData higherPriorityTaskData:
                    results) {
                higherPriorityTaskData.lstProduct = getLocalCheckerTaskItems(higherPriorityTaskData.refDocUri, higherPriorityTaskData.refDocId);
            }
            return results;
        });
    }

    public List<HigherPriorityTaskItemData> getLocalCheckerTaskItems(String refDocUri, String refDocId) throws Exception {
        List<HigherPriorityTaskItemData> results = DbExecuteRead(dbClient ->
                dbClient.Query(HigherPriorityTaskItemData.class, HigherPriorityTaskItemContract.Query.getSelectList(refDocUri, refDocId))
        );

        for (HigherPriorityTaskItemData checkerTaskItemData :
                results) {
            checkerTaskItemData.setCompUom(getLocalCompUom(checkerTaskItemData.compUomId));
        }
        return results;
    }

    public CompUomData getLocalCompUom(String compUomId) throws Exception {
        return DbExecuteRead(dbClient -> {
            CompUomData result = dbClient.Find(CompUomData.class, compUomId);
            result.compUomItems = getLocalCompUomItems(compUomId);
            result.uomConversions = getLocalUomConversions(result.compUomItems);
            return result;
        });
    }

    public List<CompUomItemData> getLocalCompUomItems(String compUomId) throws Exception {
        return DbExecuteRead(dbClient -> {
            List<CompUomItemData> results = dbClient.Query(CompUomItemData.class, CompUomItemContract.Query.getSelectList(compUomId));
            return results;
        });
    }

    public List<UomConversionData> getLocalUomConversions(List<CompUomItemData> compUomItems) throws Exception {
        return DbExecuteRead(dbClient -> {
            List<UomConversionData> results = new ArrayList<>();
            String uom1 = "", uom2 = "", uom3 = "";
            if (compUomItems.size() == 0)
                return results;

            if (compUomItems.size() > 2)
                uom3 = compUomItems.get(2).uomId;

            if (compUomItems.size() > 1)
                uom2 = compUomItems.get(1).uomId;

            if (compUomItems.size() > 0)
                uom1 = compUomItems.get(0).uomId;

            results = dbClient.Query(UomConversionData.class, UomConversionContract.Query.getSelectList(uom1, uom2, uom3));
            return results;
        });
    }

    public void rejectOrderMonitoringBasedOrderNo(String orderUri, String orderNo) throws Exception {
        restClient.post("RejectOrderMonitoringBasedOrderNo?orderUri=" + orderUri + "&orderNo=" + orderNo);
    }

    public void removeHigherPriorityTask(String refDocUri, String refDocId) throws Exception {
        DbExecuteWrite(dbClient -> {
            removeOperator(refDocUri, refDocId);
            dbClient.Execute(HigherPriorityTaskItemContract.Query.getDeleteList(refDocUri, refDocId));
            dbClient.Execute(HigherPriorityTaskContract.Query.getDeleteList(refDocUri, refDocId));
        });
    }


    public void removeOperator(String refDocUri, String refDocId) throws Exception {
        DbExecuteWrite(dbClient ->
                dbClient.Execute(HigherPriorityTaskOperatorContract.Query.getDeleteList(refDocUri, refDocId))
        );
    }}
