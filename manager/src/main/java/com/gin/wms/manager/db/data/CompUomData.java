package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.CompUomContract;
import com.gin.wms.manager.db.data.helper.CompUomHelper;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/12/2018.
 */

public class CompUomData extends Data {
    public String compUomId;
    public String separator;

    public List<CompUomItemData> compUomItems = new ArrayList<>();
    public List<UomConversionData> uomConversions = new ArrayList<>();


    @Override
    public Contract getContract() throws Exception {
        return new CompUomContract();
    }
}
