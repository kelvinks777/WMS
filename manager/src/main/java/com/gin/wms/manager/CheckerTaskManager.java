package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.base.OperatorBaseContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemResultContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskOperatorContract;
import com.gin.wms.manager.db.data.CheckerTaskData;
import com.gin.wms.manager.db.data.CheckerTaskItemData;
import com.gin.wms.manager.db.data.CheckerTaskItemResultData;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.CheckerTaskOperatorData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.UomConversionData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.manager.db.data.helper.CompUomHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by manbaul on 3/7/2018.
 */

public class CheckerTaskManager extends Manager {

    private ProductManager productManager;

    public CheckerTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "checker", new UserContext(context), new ManagerSetup());
        productManager = new ProductManager(context);
    }

    public CheckerTaskItemResultData getReleaseCheckResultFromStockLocation(String taskId, String palletNo, String productId) throws Exception {
        return restClient.get(CheckerTaskItemResultData.class,  "GetReleaseCheckResultFromStockLocation?taskId=" + taskId + "&palletNo=" + palletNo + "&productId=" + productId);
    }

    public void moveItemsToStaging(String refDocId) throws Exception {
        restClient.post("moveItemsToStaging?id=" + refDocId);
    }

    public void moveItemsToDocking(String refDocId, List<String> operators) throws Exception {
        restClient.post("moveItemsToDocking?id=" + refDocId, operators);
    }

    public int getCountOfNotStartedTasks() throws Exception {
        return restClient.get(int.class, "GetCountOfNotStartedTasks");
    }

    public void startCheckerTask(String refDocUri, String refDocId) throws Exception {
        restClient.post("startCheckerTask?refUri=" + refDocUri + "&refId=" + refDocId);
    }

    public void finishCheckerTask(CheckerTaskData checkerTaskData) throws Exception {
        restClient.post("FinishCheckerTask?refDocUri=" + checkerTaskData.refDocUri + "&refDocId=" + checkerTaskData.refDocId);
    }

    public void updateFSid()throws Exception{
        restClient.post("UpdateFSid");
    }

    public void removeCheckerTask(String taskId) throws Exception {
        DbExecuteWrite(dbClient -> {
            removeOperator(taskId);
            dbClient.Execute(CheckerTaskItemContract.Query.getDeleteList(taskId));
            dbClient.Execute(CheckerTaskContract.Query.getDeleteList(taskId));
        });
    }

    private void saveCheckerTaskData(DbClient dbClient, CheckerTaskData checkerTaskData) throws Exception {

        for (CheckerTaskItemData product :
                checkerTaskData.lstProduct) {

            CompUomData compUomData = product.getCompUom();
            CompUomHelper helper = new CompUomHelper(product.getCompUom());

            product.taskId = checkerTaskData.taskId;

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
            double badQty = 0;
            double goodQty = 0;
            for (CheckerTaskItemResultData resultData :
                    product.results) {
                resultData.taskId = checkerTaskData.taskId;
                resultData.productId = product.productId;
                resultData.clientLocationId = product.clientLocationId;
                resultData.badCompUomValue = helper.getCompUomValueFromTotal(resultData.badQty);
                resultData.goodCompUomValue = helper.getCompUomValueFromTotal(resultData.goodQty);
                badQty += resultData.badQty;
                goodQty += resultData.goodQty;
                dbClient.Save(resultData);
            }
            product.goodQtyCheckResult = goodQty;
            product.badQtyCheckResult = badQty;
            product.goodQtyCheckResultCompUomValue = helper.getCompUomValueFromTotal(product.goodQtyCheckResult);
            product.badQtyCheckResultCompUomValue = helper.getCompUomValueFromTotal(product.badQtyCheckResult);
            dbClient.Save(product);
        }

        for (CheckerTaskOperatorData operator :
                checkerTaskData.lstOperator) {
            dbClient.Delete(CheckerTaskOperatorContract.TABLE_NAME,
                    new String[]{OperatorBaseContract.Column.OPERATOR_ID, CheckerTaskOperatorContract.Column.TASK_ID},
                    new String[]{operator.id, operator.taskId}
            );
            operator.taskId = checkerTaskData.taskId;
            dbClient.Save(operator);
        }
        dbClient.Save(checkerTaskData);
    }

    public CheckerTaskData getTaskByUnloader() throws Exception {
        CheckerTaskData result = restClient.get(CheckerTaskData.class, "getTaskByUnloader");
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(CheckerTaskData.class);
            dbClient.DeleteAll(CheckerTaskItemData.class);
            dbClient.DeleteAll(CheckerTaskItemResultData.class);
            saveCheckerTaskData(dbClient, result);
        });
        return result;
    }

    public CheckerTaskData getLocalTaskByUnloader() throws Exception {
        List<CheckerTaskData> results = getListOfLocalCheckerTaskData();
        if (results.size() == 0)
            return null;
        return results.get(0);
    }

    public List<CheckerTaskItemData> getLocalTaskItemByUnloader() throws Exception {
        CheckerTaskData checkerTaskData = getLocalTaskByUnloader();
        return checkerTaskData.lstProduct;
    }

    public CheckerTaskData findTaskByReleaseDocRef(String refId) throws Exception {
        String functionName = "FindTaskByDocRef?refUri=" + RefDocUriEnum.RELEASE.getValue() + "&refId=" + refId;
        CheckerTaskData result = restClient.get(CheckerTaskData.class, functionName);
        deleteAllAndSaveCheckerTaskData(result);
        return result;
    }

    private void deleteAllAndSaveCheckerTaskData(CheckerTaskData checkerTaskData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(CheckerTaskData.class);
            dbClient.DeleteAll(CheckerTaskItemData.class);
            dbClient.DeleteAll(CheckerTaskItemResultData.class);
            saveCheckerTaskData(dbClient, checkerTaskData);
        });
    }

    public CheckerTaskData findTaskByReceivingDocRef(String refId) throws Exception {
        String functionName = "FindTaskByDocRef?refUri=" + RefDocUriEnum.RECEIVING.getValue() + "&refId=" + refId;
        CheckerTaskData result = restClient.get(CheckerTaskData.class, functionName);
        deleteAllAndSaveCheckerTaskData(result);
        return result;
    }

    public List<CheckerTaskData> findTask() throws Exception {
        List<CheckerTaskData> results = restClient.getList(CheckerTaskData[].class, "FindTask");
        DbExecuteWrite(dbClient -> {
            if (results != null) {
                dbClient.DeleteAll(CheckerTaskData.class);
                dbClient.DeleteAll(CheckerTaskData.class);
                dbClient.DeleteAll(CheckerTaskItemData.class);
                dbClient.DeleteAll(CheckerTaskItemResultData.class);

                for (CheckerTaskData checkerTaskData :
                        results)
                    saveCheckerTaskData(dbClient, checkerTaskData);
            }
        });
        return results;
    }

    public List<CheckerTaskData> getListOfLocalCheckerTaskData() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<CheckerTaskData> results = dbClient.Query(CheckerTaskData.class, CheckerTaskContract.Query.getSelectAll());
            for (CheckerTaskData checkerTaskData :
                    results) {
                checkerTaskData.lstOperator = getOperators(checkerTaskData.taskId);
                checkerTaskData.lstProduct = getLocalCheckerTaskItems(checkerTaskData.taskId);
            }
            return results;
        });
    }

    public CheckerTaskData getLocalCheckerTaskData(String taskId) throws Exception {
        return DbExecuteRead(dbClient -> {
            CheckerTaskData result = dbClient.Find(CheckerTaskData.class, new String[]{taskId});
            if (result!=null) {
                result.lstOperator = getOperators(taskId);
                result.lstProduct = getLocalCheckerTaskItems(taskId);
            }
            return result;
        });
    }

    private List<CheckerTaskItemResultData> getLocalCheckerTaskItemResults(String taskId, String productId, String clientLocationId) throws Exception {
        return DbExecuteRead(dbClient -> dbClient.Query(CheckerTaskItemResultData.class, CheckerTaskItemResultContract.Query.getSelectList(taskId, productId, clientLocationId)));
    }

    public CheckerTaskItemData getLocalCheckerTaskItem(String taskId, String productId, String clientLocationId) throws Exception {
        return DbExecuteRead(dbClient -> {
            CheckerTaskItemData result = dbClient.Find(CheckerTaskItemData.class, new String[]{productId, clientLocationId, taskId});
            if (result != null) {
                result.setCompUom(productManager.getLocalCompUom(result.compUomId));
                result.results = getLocalCheckerTaskItemResults(taskId, productId, clientLocationId);
            }
            return result;
        });
    }

    public List<CheckerTaskItemResultData> getCheckerTaskItemResults(String taskId, String productId, String clientLocationId) throws Exception {
        List<CheckerTaskItemResultData> results = restClient.getList(CheckerTaskItemResultData[].class, "GetCheckerTaskItemResults?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId);
        CheckerTaskItemData checkerTaskItemData = getLocalCheckerTaskItem(taskId, productId, clientLocationId);
        for (CheckerTaskItemResultData resultData :
                results) {
            resultData.taskId = taskId;
            resultData.productId = productId;
            resultData.clientLocationId = clientLocationId;
            resultData.badCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(resultData.badQty);
            resultData.goodCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(resultData.goodQty);
            saveCheckerTaskItemResult(resultData);
        }
        return results;
    }

    private void saveCheckerTaskItemResult(CheckerTaskItemResultData resultData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(resultData);
        });
    }

    public List<CheckerTaskItemData> getLocalCheckerTaskItems(String taskId) throws Exception {
        List<CheckerTaskItemData> results = DbExecuteRead(dbClient ->
                dbClient.Query(CheckerTaskItemData.class, CheckerTaskItemContract.Query.getSelectList(taskId))
        );

        for (CheckerTaskItemData checkerTaskItemData :
                results) {
            checkerTaskItemData.setCompUom(productManager.getLocalCompUom(checkerTaskItemData.compUomId));
            checkerTaskItemData.results = getLocalCheckerTaskItemResults(taskId, checkerTaskItemData.productId, checkerTaskItemData.clientLocationId);
        }
        return results;
    }

    public void deleteResult(String taskId, String productId, String clientLocationId, String palletNo) throws Exception {
        String functionName = "deleteResult?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId + "&palletNo=" + palletNo;
        restClient.delete(functionName);
        DbExecuteWrite(dbClient -> dbClient.Delete(CheckerTaskItemResultContract.TABLE_NAME
                , new String[]{CheckerTaskItemResultContract.Column.TASK_ID, CheckerTaskItemResultContract.Column.PRODUCT_ID, CheckerTaskItemResultContract.Column.CLIENT_LOCATION_ID, CheckerTaskItemResultContract.Column.PALLET_NO}
                , new String[]{taskId, productId, clientLocationId, palletNo}
                ));
        recalcResultQty(taskId, productId, clientLocationId);
    }

    public boolean updateResult(String taskId, String productId, String clientLocationId, String palletNo, Date expiredDate, double goodQty, double badQty) throws Exception {
        CheckerTaskItemResultData checkerTaskItemResultData = DbExecuteRead(dbClient ->
                dbClient.Find(CheckerTaskItemResultData.class, new String[]{taskId,productId,clientLocationId, palletNo})
        );

        if (checkerTaskItemResultData == null)
            return false;
        else {
            CheckerTaskItemData checkerTaskItemData = getLocalCheckerTaskItem(taskId, productId, clientLocationId);
            CheckerTaskItemResultData result = new CheckerTaskItemResultData();
            result.taskId = taskId;
            result.productId = productId;
            result.clientLocationId = clientLocationId;
            result.palletNo = palletNo;
            result.badQty = badQty;
            result.goodQty = goodQty;
            result.expiredDate = expiredDate;


            restClient.put("updateResult?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId + "&clientId=" + checkerTaskItemData.clientId, result);

            DbExecuteWrite(dbClient -> {
                result.goodCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(goodQty);
                result.badCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(badQty);
                dbClient.Save(result);
            });
            recalcResultQty(taskId, productId, clientLocationId);
            return true;
        }
    }

    public boolean createResult(String taskId, String productId, String clientLocationId, String palletNo, Date expiredDate, double goodQty, double badQty) throws Exception {
        CheckerTaskItemResultData checkerTaskItemResultData = DbExecuteRead(dbClient ->
                dbClient.Find(CheckerTaskItemResultData.class, new String[]{taskId,productId,clientLocationId, palletNo})
        );

        if (checkerTaskItemResultData != null)
            return false;
        else {
            CheckerTaskItemData checkerTaskItemData = getLocalCheckerTaskItem(taskId, productId, clientLocationId);
            CheckerTaskItemResultData result = new CheckerTaskItemResultData();
            result.taskId = taskId;
            result.productId = productId;
            result.clientLocationId = clientLocationId;
            result.palletNo = palletNo;
            result.badQty = badQty;
            result.goodQty = goodQty;
            result.expiredDate = expiredDate;
            restClient.post("createResult?taskId=" + taskId + "&productId=" + productId + "&clientLocationId=" + clientLocationId + "&clientId=" + checkerTaskItemData.clientId, result);
            result.goodCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(goodQty);
            result.badCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(badQty);
            DbExecuteWrite(dbClient -> {
                dbClient.Insert(result);
            });
            recalcResultQty(taskId, productId, clientLocationId);
            return true;
        }
    }

    public void recalcResultQty(String taskId, String productId, String clientLocationId) throws Exception {
        DbExecuteWrite(dbClient -> {
            CheckerTaskItemData checkerTaskItemData = getLocalCheckerTaskItem(taskId, productId, clientLocationId);
            double badQty = 0;
            double goodQty = 0;
            for (CheckerTaskItemResultData resultData :
                    checkerTaskItemData.results) {
                badQty += resultData.badQty;
                goodQty += resultData.goodQty;
            }
            checkerTaskItemData.goodQtyCheckResult = goodQty;
            checkerTaskItemData.goodQtyCheckResultCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(checkerTaskItemData.goodQtyCheckResult);
            checkerTaskItemData.badQtyCheckResult = badQty;
            checkerTaskItemData.badQtyCheckResultCompUomValue = checkerTaskItemData.getHelper().getCompUomValueFromTotal(checkerTaskItemData.badQtyCheckResult);
            saveCheckerTaskItemData(checkerTaskItemData);
        });
    }

    private void saveCheckerTaskItemData(CheckerTaskItemData checkerTaskItemData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(checkerTaskItemData);
        });
    }

    public List<CheckerTaskOperatorData> getOperators(String taskId) throws Exception {
        return DbExecuteRead(dbClient ->
                dbClient.Query(CheckerTaskOperatorData.class, CheckerTaskOperatorContract.Query.getSelectList(taskId))
        );
    }

    public void removeOperator(String taskId, String id) throws Exception {
        DbExecuteWrite(dbClient ->
                dbClient.Execute(CheckerTaskOperatorContract.Query.getDelete(taskId, id))
        );
    }

    public void removeOperator(String taskId) throws Exception {
        DbExecuteWrite(dbClient ->
                dbClient.Execute(CheckerTaskOperatorContract.Query.getDeleteList(taskId))
        );
    }
}
