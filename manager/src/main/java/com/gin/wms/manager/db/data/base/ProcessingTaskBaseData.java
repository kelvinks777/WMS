package com.gin.wms.manager.db.data.base;

import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.data.ProcessingTaskOperatorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernandes on 10/05/2018.
 */

public abstract class ProcessingTaskBaseData extends Data {
    public String refDocUri;
    public String refDocId;
    public String policeNo;
    public int minOperator;
    public int maxOperator;
    public boolean hasBeenStart;
    public int multiplyOperator;
    public List<ProcessingTaskOperatorData> lstOperator = new ArrayList<>();
}
