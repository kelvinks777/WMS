package com.gin.wms.warehouse.checker

import com.gin.ngemart.baseui.component.NgemartCardView
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.manager.db.data.enums.RefDocUriEnum
import com.gin.wms.manager.db.data.enums.TaskTypeEnum
import com.gin.wms.warehouse.R
import kotlinx.android.synthetic.main.card_checker_main.view.*


/**
 * Created by manbaul on 3/14/2018.
 */

class CheckerMainCard : NgemartCardView<CheckerTaskData> {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: CheckerTaskData?) {
        try {
            super.setData(data)
            setUpView(data)
            setUpIcon(data)

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }

    private fun setUpView(data : CheckerTaskData?){
        tvMenuSubTitle.text = "Doc No : " + data?.taskId
        if (data?.hasBeenStart!!)
            tvMenuTitle.text = data?.policeNo + " (On Progress)"
        else
            tvMenuTitle.text = data?.policeNo
    }

    private fun setUpIcon(data : CheckerTaskData?){
        if(data?.refDocUri.equals(RefDocUriEnum.RELEASE.value))
            imgCheckerType.setImageDrawable(ContextCompat.getDrawable(cardActivity, R.drawable.ic_loading))
        else
            imgCheckerType.setImageDrawable(ContextCompat.getDrawable(cardActivity, R.drawable.ic_unloading))
    }
}