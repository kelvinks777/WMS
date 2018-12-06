package com.gin.wms.manager.config;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.HigherPriorityTaskOperatorContract;
import com.gin.wms.manager.db.contract.MovingTaskContract;
import com.gin.wms.manager.db.contract.MovingTaskDestItemContract;
import com.gin.wms.manager.db.contract.MovingTaskSourceItemContract;
import com.gin.wms.manager.db.contract.PickingTaskContract;
import com.gin.wms.manager.db.contract.PickingTaskDestItemContract;
import com.gin.wms.manager.db.contract.PickingTaskSourceItemContract;
import com.gin.wms.manager.db.contract.PutawayTaskItemContract;
import com.gin.wms.manager.db.contract.StockCountingOrderContract;
import com.gin.wms.manager.db.contract.StockCountingOrderItemContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemContract;
import com.gin.wms.manager.db.contract.CompUomContract;
import com.gin.wms.manager.db.contract.CompUomItemContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemResultContract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskOperatorContract;
import com.gin.wms.manager.db.contract.ProductContract;
import com.gin.wms.manager.db.contract.PutawayBookingContract;
import com.gin.wms.manager.db.contract.PutawayBookingOperatorContract;
import com.gin.wms.manager.db.contract.PutawayTaskContract;
import com.gin.wms.manager.db.contract.TokenLocalContract;
import com.gin.wms.manager.db.contract.DockingTaskItemContract;
import com.gin.wms.manager.db.contract.DockingTaskContract;
import com.gin.wms.manager.db.contract.UomConversionContract;
import com.gin.wms.manager.db.contract.UserContract;
import com.gin.wms.manager.db.contract.VehicleCategoryContract;
import com.gin.wms.manager.db.contract.VehicleContract;
import com.gin.wms.manager.db.contract.VehicleTypeContract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskItemContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskItemContract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskOperatorContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 2/19/2018.
 */

public class ContractManager {
    private static List<Contract> contractList = new ArrayList<>();

    public static List<Contract> getListContract() throws Exception {
        if (contractList.size() == 0) {
            contractList.add(new CompUomContract());
            contractList.add(new CompUomItemContract());
            contractList.add(new HigherPriorityTaskContract());
            contractList.add(new HigherPriorityTaskItemContract());
            contractList.add(new HigherPriorityTaskOperatorContract());
            contractList.add(new CheckerTaskContract());
            contractList.add(new CheckerTaskItemContract());
            contractList.add(new CheckerTaskOperatorContract());
            contractList.add(new UomConversionContract());
            contractList.add(new UserContract());
            contractList.add(new TokenLocalContract());
            contractList.add(new PutawayBookingContract());
            contractList.add(new PutawayBookingOperatorContract());
            contractList.add(new ProductContract());
            contractList.add(new VehicleContract());
            contractList.add(new VehicleCategoryContract());
            contractList.add(new VehicleTypeContract());
            contractList.add(new DockingTaskContract());
            contractList.add(new DockingTaskItemContract());
            contractList.add(new PutawayTaskContract());
            contractList.add(new PutawayTaskItemContract());
            contractList.add(new CheckerTaskItemResultContract());
            contractList.add(new PickingTaskContract());
            contractList.add(new PickingTaskSourceItemContract());
            contractList.add(new PickingTaskDestItemContract());
            contractList.add(new MovingTaskContract());
            contractList.add(new MovingTaskSourceItemContract());
            contractList.add(new MovingTaskDestItemContract());
            contractList.add(new ProcessingTaskContract());
            contractList.add(new ProcessingTaskItemContract());
            contractList.add(new ProcessingTaskOperatorContract());
            contractList.add(new StockCountingOrderContract());
            contractList.add(new StockCountingOrderItemContract());
        }
        return contractList;
    }
}
