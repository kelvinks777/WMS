package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import com.gin.ngemart.baseui.component.NgemartCardView
import kotlinx.android.synthetic.main.card_operator.view.*


/**
 * Created by manbaul on 3/16/2018.
 */
class OperatorCard : NgemartCardView<OperatorActivity.TaskOperator> {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: OperatorActivity.TaskOperator?) {
        try {
            super.setData(data)
            tvMenuTitle.text = data?.name
            tvMenuSubTitle.text = data?.id
            swChoosed.isChecked = data?.isChoosed !!
            swChoosed.setOnCheckedChangeListener { compoundButton, b ->
                data.isChoosed = b
            }

        } catch (e: Exception) {
            ShowError(e)
        }
    }


}