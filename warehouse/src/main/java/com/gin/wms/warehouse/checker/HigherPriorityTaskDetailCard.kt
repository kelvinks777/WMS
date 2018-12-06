package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import com.bosnet.ngemart.libgen.Common
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.HigherPriorityTaskItemData
import kotlinx.android.synthetic.main.card_higher_priority_task_detail.view.*

/**
 * Created by manbaul on 3/14/2018.
 */
class HigherPriorityTaskDetailCard : NgemartCardView<HigherPriorityTaskItemData> {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: HigherPriorityTaskItemData?) {
        try {
            super.setData(data)
            data?.let {
                tvProductName.text = it.productName
                tvClientId.text = it.clientId
                tvClientLocationId.text = it.clientLocationId
                tvQty.text = it.qtyCompUomValue
                tvPalletQty.text = Common.GetNumberWithoutFractionFormat().format(it.palletQty)
                tvPalletConversion.text = Common.GetNumberWithoutFractionFormat().format(it.palletConversionValue)
                tvCompUom.text = it.compUomId
            }

        } catch (e: Exception) {
            ShowError(e)
        }
    }

}