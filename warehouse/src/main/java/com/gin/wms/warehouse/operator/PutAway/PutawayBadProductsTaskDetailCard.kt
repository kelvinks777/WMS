package com.gin.wms.warehouse.operator.PutAway

import android.content.Context
import android.util.AttributeSet
import com.bosnet.ngemart.libgen.Common
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.PutawayTaskItemData
import kotlinx.android.synthetic.main.card_putaway_bad_products_task_detail.view.*

/**
 * Created by manbaul on 3/14/2018.
 */
class PutawayBadProductsTaskDetailCard : NgemartCardView<PutawayTaskItemData> {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: PutawayTaskItemData?) {
        try {
            super.setData(data)
            tvProductName.text = data?.productName
            tvClientId.text = data?.clientId
            tvClientLocationId.text = data?.clientLocationId
            tvQty.text = data?.qtyCompUomValue

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }

}