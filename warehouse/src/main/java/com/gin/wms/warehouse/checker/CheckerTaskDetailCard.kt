package com.gin.wms.warehouse.checker

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import com.bosnet.ngemart.libgen.Common.GetNumberWithoutFractionFormat
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.CheckerTaskItemResultData
import com.gin.wms.warehouse.R
import kotlinx.android.synthetic.main.card_checker_task_detail.view.*

/**
 * Created by manbaul on 3/14/2018.
 */
class CheckerTaskDetailCard: NgemartCardView<CheckerTaskItemData> {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: CheckerTaskItemData?) {
        try {
            super.setData(data)
            setDataToUi()
            setOnclickListener()
        } catch (e: Exception) {
            ShowError(e)
        }
    }

    private fun setDataToUi(){
        data?.let {
            tvProductName.text = it.productName
            tvClientId.text = it.clientId
            tvClientLocationId.text = it.clientLocationId
            tvQty.text = it.compUomValueFromQty
            tvPalletQty.text = GetNumberWithoutFractionFormat().format(it.palletQty)
            tvGoodQty.text = it.compUomValueFromGoodCheckResultQty
            tvBadQty.text = it.compUomValueFromBadCheckResultQty
            tvPalletConversion.text = GetNumberWithoutFractionFormat().format(it.palletConversionValue)
            tvCompUom.text = it.compUomId
            tvPalletResult.text = GetNumberWithoutFractionFormat()
                    .format(it.results
                            .groupBy { checkerTaskItemResultData: CheckerTaskItemResultData? -> checkerTaskItemResultData?.palletNo }
                            .keys.size)
        }

        checkDataIsAlreadyChecked()
    }

    private fun checkDataIsAlreadyChecked(){
        if(data != null){
            if (data.badQtyCheckResult + data.goodQtyCheckResult > 0.00 && data.badQtyCheckResult + data.goodQtyCheckResult >= data.qty){
                tvCheckedInfo.visibility = View.VISIBLE
                tvCheckedInfo.text = "Checked"
                tvCheckedInfo.setBackgroundColor(ContextCompat.getColor(cardActivity, R.color.colorClickable))
            } else{
                tvCheckedInfo.visibility = View.GONE
                tvCheckedInfo.text = ""
                tvCheckedInfo.setBackgroundColor(ContextCompat.getColor(cardActivity, R.color.white))
            }
        }
    }

    private fun setOnclickListener(){
        setOnClickListener { view ->
            cardListener?.onCardClick(verticalScrollbarPosition, view, data)
        }

        btnPutaway.setOnClickListener {
            cardListener?.onCardClick(verticalScrollbarPosition, btnPutaway, data)
        }
    }

}