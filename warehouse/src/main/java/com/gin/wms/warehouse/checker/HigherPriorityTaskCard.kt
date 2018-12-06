package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.HigherPriorityTaskData
import kotlinx.android.synthetic.main.card_higher_priority_task.view.*
import java.text.MessageFormat

/**
 * Created by manbaul on 3/14/2018.
 */

class HigherPriorityTaskCard : NgemartCardView<HigherPriorityTaskData> {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) 

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: HigherPriorityTaskData?) {
        try {
            super.setData(data)
            tvMenuSubTitle.text = MessageFormat.format("Operator : {0}, Min : {1}, Max : {2}, Multiply : {3}"
                    , data?.lstOperator?.size, data?.minOperator, data?.maxOperator, data?.multiplyOperator)

            tvMenuTitle.text = data?.policeNo

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }

        } catch (e: Exception) {
            ShowError(e)
        }

    }
}