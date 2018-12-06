package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.PutawayBookingOperatorContract;
import com.gin.wms.manager.db.data.PutawayBookingData;
import com.gin.wms.manager.db.data.PutawayBookingOperatorData;

import java.util.List;

/**
 * Created by manbaul on 3/20/2018.
 */

public class PutawayBookingManager extends Manager {
    public PutawayBookingManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "PutawayOperatorBooking", new UserContext(context), new ManagerSetup());
    }

    public void putawayOperatorBookingMultipleOperator(PutawayBookingData putawayBookingData) throws Exception {
        restClient.post("PutawayOperatorBookingMultipleOperator", putawayBookingData);
    }

    public void savePutawayBooking(String policeNo, String docNo, String productId, String receivingNo, String clientId, int palletQty, List<PutawayBookingOperatorData> operators) throws Exception {
        DbExecuteWrite(dbClient -> {
            PutawayBookingData putawayBookingData = getLocalPutawayBooking(policeNo, docNo, productId);
            if (putawayBookingData == null) {
                putawayBookingData = new PutawayBookingData();
                putawayBookingData.policeNo = policeNo;
                putawayBookingData.receivingNo = receivingNo;
                putawayBookingData.clientId = clientId;
                putawayBookingData.taskId = docNo;
                putawayBookingData.productId = productId;
                putawayBookingData.palletQty = palletQty;
            }

            putawayBookingData.getOperators().addAll(operators);
            dbClient.Save(putawayBookingData);
            for (PutawayBookingOperatorData operatorData :
                    putawayBookingData.getOperators()) {
                dbClient.Save(operatorData);
            }

            for (PutawayBookingOperatorData operator :
                    operators) {
                CheckerTaskManager checkerTaskManager = new CheckerTaskManager(context);
                checkerTaskManager.removeOperator(operator.taskIdBefore, operator.id);
            }
            ;
        });
    }

    public PutawayBookingData getLocalPutawayBooking(String policeNo, String docNo, String productId) throws Exception {
        return DbExecuteRead(dbClient -> {
            PutawayBookingData result = dbClient.Find(PutawayBookingData.class, new String[]{policeNo, docNo, productId});
            if (result != null)
                result.setOperators(getListOfLocalPutawayOperatorBooking(policeNo, docNo, productId));
            return result;
        });
    }

    public List<PutawayBookingOperatorData> getListOfLocalPutawayOperatorBooking(String policeNo, String docNo, String productId) throws Exception {
        return DbExecuteRead(dbClient -> dbClient.Query(PutawayBookingOperatorData.class
                , PutawayBookingOperatorContract.Query.getSelectList(policeNo, docNo, productId)));
    }

}
