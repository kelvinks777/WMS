package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.bosnet.ngemart.libgen.Common
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.CheckerTaskItemResultData
import kotlinx.android.synthetic.main.card_check_result_input.view.*
import java.text.MessageFormat

/**
 * Created by manbaul on 3/14/2018.
 */
class CheckResultInputCard : NgemartCardView<CheckerTaskItemResultData> {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: CheckerTaskItemResultData?) {
        try {
            super.setData(data)
            tvPalletNo.text = data?.palletNo
            tvExpiredDate.text = MessageFormat.format("Exp : {0}", Common.getShortDateString(data?.expiredDate))

            tvGoodQty.text =  data?.goodCompUomValue
            tvBadQty.text = data?.badCompUomValue

            if (data?.goodQty!! > 0 ) {
                llGood.visibility = View.VISIBLE
                llBad.visibility = View.GONE
            } else if (data?.badQty!! > 0 ) {
                llGood.visibility = View.GONE
                llBad.visibility = View.VISIBLE
            }

            if (data?.alreadyUsed!!) {
                ivAlreadyUsed.visibility = View.VISIBLE
                btnEditItem.visibility = View.GONE
                btnRemoveItem.visibility = View.GONE
            } else {
                ivAlreadyUsed.visibility = View.GONE
                btnEditItem.visibility = View.VISIBLE
                btnRemoveItem.visibility = View.VISIBLE
                btnEditItem.setOnClickListener { view ->
                    cardListener?.onCardClick(verticalScrollbarPosition, view, data)
                }
                btnRemoveItem.setOnClickListener { view ->
                    cardListener?.onCardClick(verticalScrollbarPosition, view, data)
                }
            }

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }

}