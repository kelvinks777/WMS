package com.gin.wms.warehouse.operator.PutAway

import android.content.Context
import android.util.AttributeSet
import com.bosnet.ngemart.libgen.Common
import com.gin.wms.manager.db.data.PutawayTaskData
import com.gin.ngemart.baseui.component.NgemartCardView
import kotlinx.android.synthetic.main.card_putaway_task.view.*

/**
 * Created by manbaul on 3/14/2018.
 */
class PutawayTaskCard : NgemartCardView<PutawayTaskData> {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: PutawayTaskData?) {
        try {
            super.setData(data)
//            tvProductName.text = data?.productId
//            tvClientLocationId.text = data?.sourceId
//            tvQty.text = Common.GetNumberWithoutFractionFormat().format(data?.qty)

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }

}