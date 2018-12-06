package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.base.OperatorBaseContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskItemContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskItemResultContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskOperatorContract;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.ProcessingTaskData;
import com.gin.wms.manager.db.data.ProcessingTaskItemData;
import com.gin.wms.manager.db.data.ProcessingTaskItemResultData;
import com.gin.wms.manager.db.data.ProcessingTaskOperatorData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.UomConversionData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.manager.db.data.helper.CompUomHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by Fernandes on 10/05/2018.
 */

public class ProcessingTaskManager extends Manager {

    private ProductManager productManager;

    public ProcessingTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "processing", new UserContext(context), new ManagerSetup());
        productManager = new ProductManager(context);
    }

    public ProcessingTaskItemResultData getBandedCheckResultFromStockLocation(String taskId, String palletNo, String productId) throws Exception {
        return restClient.get(ProcessingTaskItemResultData.class,  "GetBandedCheckResultFromStockLocation?taskId=" + taskId + "&palletNo=" + palletNo + "&productId=" + productId);
    }

    public void putawayBandedProduct(String refDocId) throws Exception {
        restClient.post("putawayBandedProduct?id=" + refDocId);
    }

    public void startProcessingTask(String refDocUri, String refDocId) throws Exception {
        restClient.post("startProcessingTask?refUri=" + refDocUri + "&refId=" + refDocId);
    }

    public void finishProcessingTask(ProcessingTaskData processingTaskData) throws Exception {
        restClient.post("finishProcessingTask?refDocUri=" + processingTaskData.refDocUri + "&refDocId=" + processingTaskData.refDocId);
    }

    private void saveProcessingTaskData(DbClient dbClient, ProcessingTaskData processingTaskData) throws Exception {

        for (ProcessingTaskItemData product :
                processingTaskData.lstProduct) {

            CompUomData compUomData = product.getCompUom();
            CompUomHelper helper = new CompUomHelper(product.getCompUom());

            product.taskId = processingTaskData.taskId;

            if (compUomData != null) {
                product.compUomId = product.getCompUom().compUomId;
                dbClient.Save(product.getCompUom());
                for (CompUomItemData compUomItemData :
                        compUomData.compUomItems) {
                    compUomItemData.compUomId = compUomData.compUomId;
                    dbClient.Save(compUomItemData);
                }

                for (UomConversionData uomConversionData :
                        compUomData.uomConversions) {
                    dbClient.Save(uomConversionData);
                }
            }

            dbClient.Save(ProductData.getFromBase(product));
            product.palletQty = product.getCalcPalletByQty();
            product.qtyCompUomValue = product.getCompUomValueFromQty();
            double qty = 0;
            for (ProcessingTaskItemResultData resultData :
                    product.results) {
                resultData.taskId = processingTaskData.taskId;
                resultData.productId = product.productId;
                resultData.clientLocationId = product.clientLocationId;
                resultData.compUomValue = helper.getCompUomValueFromTotal(resultData.qty);
                qty += resultData.qty;
                dbClient.Save(resultData);
            }
            product.qtyCheckResult = qty;
            product.qtyCheckResultCompUomValue = helper.getCompUomValueFromTotal(product.qtyCheckResult);
            dbClient.Save(product);
        }

        for (ProcessingTaskOperatorData operator :
                processingTaskData.lstOperator) {
            dbClient.Delete(ProcessingTaskOperatorContract.TABLE_NAME,
                    new String[]{OperatorBaseContract.Column.OPERATOR_ID, ProcessingTaskOperatorContract.Column.TASK_ID},
                    new String[]{operator.id, operator.taskId}
            );
            operator.taskId = processingTaskData.taskId;
            dbClient.Save(operator);
        }
        dbClient.Save(processingTaskData);
    }

    public ProcessingTaskData findTaskByBandedDocRef(String refId) throws Exception {
        String functionName = "FindTaskByDocRef?refUri=" + RefDocUriEnum.BANDED.getValue() + "&refId=" + refId;
        ProcessingTaskData result = restClient.get(ProcessingTaskData.class, functionName);
        deleteAllAndSaveProcessingTaskData(result);
        return result;
    }

    private void deleteAllAndSaveProcessingTaskData(ProcessingTaskData processingTaskData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(ProcessingTaskData.class);
            dbClient.DeleteAll(ProcessingTaskItemData.class);
            dbClient.DeleteAll(ProcessingTaskItemResultData.class);
            saveProcessingTaskData(dbClient, processingTaskData);
        });
    }

    public List<ProcessingTaskData> findTask() throws Exception {
        List<ProcessingTaskData> results = restClient.getList(ProcessingTaskData[].class, "FindTask");
        DbExecuteWrite(dbClient -> {
            if (results != null) {
                dbClient.DeleteAll(ProcessingTaskData.class);
                dbClient.DeleteAll(ProcessingTaskItemData.class);
                dbClient.DeleteAll(ProcessingTaskItemResultData.class);

                for (ProcessingTaskData processingTaskData :
                        results)
                    saveProcessingTaskData(dbClient,processingTaskData);
            }
        });
        return results;
    }

    public List<ProcessingTaskData> getListOfLocalProcessingTaskData() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<ProcessingTaskData> results = dbClient.Query(ProcessingTaskData.class, ProcessingTaskContract.Query.getSelectAll());
            for (ProcessingTaskData processingTaskData :
                    results) {
                processingTaskData.lstOperator = getOperators(processingTaskData.taskId);
                processingTaskData.lstProduct = getLocalProcessingTaskItems(processingTaskData.taskId);
            }
            return results;
        });
    }

    public ProcessingTaskData getLocalProcessingTaskData(String taskId) throws Exception {
        return DbExecuteRead(dbClient -> {
            ProcessingTaskData result = dbClient.Find(ProcessingTaskData.class, new String[]{taskId});
            if (result!=null) {
                result.lstOperator = getOperators(taskId);
                result.lstProduct = getLocalProcessingTaskItems(taskId);
            }
            return result;
        });
    }

    private List<ProcessingTaskItemResultData> getLocalProcessingTaskItemResults(String taskId, String productId, String clientLocationId) throws Exception {
        return DbExecuteRead(dbClient -> dbClient.Query(ProcessingTaskItemResultData.class, ProcessingTaskItemResultContract.Query.getSelectList(taskId, productId, clientLocationId)));
    }

    public ProcessingTaskItemData getLocalProcessingTaskItem(String taskId, String productId, String clientLocationId) throws Exception {
        return DbExecuteRead(dbClient -> {
            ProcessingTaskItemData result = dbClient.Find(ProcessingTaskItemData.class, new String[]{productId, clientLocationId, taskId});
            if (result != null) {
                result.setCompUom(productManager.getLocalCompUom(result.compUomId));
                result.results = getLocalProcessingTaskItemResults(taskId, productId, clientLocationId);
            }
            return result;
        });
    }

    public List<ProcessingTaskItemResultData> getProcessingTaskItemResults(String taskId, String productId, String clientLocationId) throws Exception {
        List<ProcessingTaskItemResultData> results = restClient.getList(ProcessingTaskItemResultData[].class, "GetProcessingTaskItemResults?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId);
        ProcessingTaskItemData processingTaskItemData = getLocalProcessingTaskItem(taskId, productId, clientLocationId);
        for (ProcessingTaskItemResultData resultData :
                results) {
            resultData.taskId = taskId;
            resultData.productId = productId;
            resultData.clientLocationId = clientLocationId;
            resultData.compUomValue = processingTaskItemData.getHelper().getCompUomValueFromTotal(resultData.qty);
            saveProcessingTaskItemResult(resultData);
        }
        return results;
    }

    private void saveProcessingTaskItemResult(ProcessingTaskItemResultData resultData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(resultData);
        });
    }

    public List<ProcessingTaskItemData> getLocalProcessingTaskItems(String taskId) throws Exception {
        List<ProcessingTaskItemData> results = DbExecuteRead(dbClient ->
                dbClient.Query(ProcessingTaskItemData.class, ProcessingTaskItemContract.Query.getSelectList(taskId))
        );

        for (ProcessingTaskItemData processingTaskItemData :
                results) {
            processingTaskItemData.setCompUom(productManager.getLocalCompUom(processingTaskItemData.compUomId));
            processingTaskItemData.results = getLocalProcessingTaskItemResults(taskId, processingTaskItemData.productId, processingTaskItemData.clientLocationId);
        }
        return results;
    }

    public boolean createResult(String taskId, String productId, String clientLocationId, String palletNo, Date expiredDate, double qty) throws Exception {
        ProcessingTaskItemResultData processingTaskItemResultData = DbExecuteRead(dbClient ->
                dbClient.Find(ProcessingTaskItemResultData.class, new String[]{taskId,productId,clientLocationId, palletNo})
        );

        if (processingTaskItemResultData != null)
            return false;
        else {
            ProcessingTaskItemData processingTaskItemData = getLocalProcessingTaskItem(taskId, productId, clientLocationId);
            ProcessingTaskItemResultData result = new ProcessingTaskItemResultData();
            result.taskId = taskId;
            result.productId = productId;
            result.clientLocationId = clientLocationId;
            result.palletNo = palletNo;
            result.qty = qty;
            result.expiredDate = expiredDate;
            restClient.post("createResult?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId + "&clientId=" + processingTaskItemData.clientId, result);
            result.compUomValue = processingTaskItemData.getHelper().getCompUomValueFromTotal(qty);
            DbExecuteWrite(dbClient -> {
                dbClient.Insert(result);
            });
            recalcResultQty(taskId, productId, clientLocationId);
            return true;
        }
    }

    public void recalcResultQty(String taskId, String productId, String clientLocationId) throws Exception {
        DbExecuteWrite(dbClient -> {
            ProcessingTaskItemData processingTaskItemData = getLocalProcessingTaskItem(taskId, productId, clientLocationId);
            double qty = 0;
            for (ProcessingTaskItemResultData resultData :
                    processingTaskItemData.results) {
                qty += resultData.qty;
            }
            processingTaskItemData.qtyCheckResult = qty;
            processingTaskItemData.qtyCheckResultCompUomValue = processingTaskItemData.getHelper().getCompUomValueFromTotal(processingTaskItemData.qtyCheckResult);
            saveProcessingTaskItemData(processingTaskItemData);
        });
    }

    private void saveProcessingTaskItemData(ProcessingTaskItemData processingTaskItemData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(processingTaskItemData);
        });
    }

    public List<ProcessingTaskOperatorData> getOperators(String taskId) throws Exception {
        return DbExecuteRead(dbClient ->
                dbClient.Query(ProcessingTaskOperatorData.class, ProcessingTaskOperatorContract.Query.getSelectList(taskId))
        );
    }

    public void removeOperator(String taskId, String id) throws Exception {
        DbExecuteWrite(dbClient ->
                dbClient.Execute(ProcessingTaskOperatorContract.Query.getDelete(taskId, id))
        );
    }

    public void removeOperator(String taskId) throws Exception {
        DbExecuteWrite(dbClient ->
                dbClient.Execute(ProcessingTaskOperatorContract.Query.getDeleteList(taskId))
        );
    }
}
