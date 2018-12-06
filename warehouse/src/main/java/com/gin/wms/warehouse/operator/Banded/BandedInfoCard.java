package com.gin.wms.warehouse.operator.Banded;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.bosnet.ngemart.libgen.Common;
import com.gin.ngemart.baseui.component.NgemartCardView;
import com.gin.wms.manager.db.data.ProcessingTaskItemResultData;
import com.gin.wms.warehouse.checker.CheckResultInputCard;

import java.text.MessageFormat;

public class BandedInfoCard extends NgemartCardView<ProcessingTaskItemResultData> {

    public BandedInfoCard(Context context) {
        super(context);
    }

    public BandedInfoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BandedInfoCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(ProcessingTaskItemResultData data) {
        try {
            //ListView bandedInfoList = (ListView) findViewById(R.id.bandedInfoList);
            //bandedInfoList.SetText(data);
        } catch (Exception e) {
            ShowError(e);
        }
    }
}
