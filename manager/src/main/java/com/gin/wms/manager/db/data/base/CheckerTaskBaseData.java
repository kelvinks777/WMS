package com.gin.wms.manager.db.data.base;

import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.data.CheckerTaskOperatorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/23/2018.
 */

public abstract class CheckerTaskBaseData extends Data {
    public String refDocUri;
    public String refDocId;
    public String policeNo;
    public int minOperator;
    public int maxOperator;
    public String stagingId;
    public String dockingIds;
    public boolean hasBeenStart;
    public int multiplyOperator;
    public List<CheckerTaskOperatorData> lstOperator = new ArrayList<>();
}
