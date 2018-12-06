package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.PutawayBookingOperatorData
import kotlinx.android.synthetic.main.card_putaway_product.view.*

/**
 * Created by manbaul on 3/16/2018.
 */
class PutawayProductCard: NgemartCardView<PutawayBookingOperatorData> {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: PutawayBookingOperatorData?) {
        try {
            super.setData(data)
            tvMenuTitle.text = data?.name
            tvMenuSubTitle.text = data?.id
            btnRemoveItem.setOnClickListener{view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }
}