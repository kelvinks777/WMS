package com.gin.wms.warehouse.checker

import android.content.Context
import android.util.AttributeSet
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.db.data.CheckerTaskData
import kotlinx.android.synthetic.main.card_task.view.*
import java.text.MessageFormat

/**
 * Created by manbaul on 3/16/2018.
 */
class TaskCard: NgemartCardView<TaskCard.TaskData> {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setData(data: TaskData?) {
        try {
            super.setData(data)
            tvMenuTitle.text = MessageFormat.format("{0} - {1}", data?.policeNo, data?.taskId)
            tvMenuSubTitle.text = MessageFormat.format("Min : {0}, Max : {1}, Multiply : {2}"
                    , data?.min, data?.max, data?.multiply)

            tvMenuSubTitle2.text = MessageFormat.format("Operator : {0}, Reassign : {1}"
                    , data?.operatorCount, data?.reassignCount)

            setOnClickListener { view ->
                cardListener?.onCardClick(verticalScrollbarPosition, view, data)
            }
        } catch (e: Exception) {
            ShowError(e)
        }
    }

    class TaskData(checkerTaskData: CheckerTaskData, var reassignCount: Int) {
        var policeNo: String = ""
        var taskId: String = ""
        var min: Int = 0
        var max: Int = 0
        var multiply: Int = 0
        var operatorCount: Int = 0

        init {
            this.policeNo = checkerTaskData.policeNo
            this.taskId = checkerTaskData.taskId
            this.min = checkerTaskData.minOperator
            this.max = checkerTaskData.maxOperator
            this.multiply = checkerTaskData.multiplyOperator
            this.operatorCount = checkerTaskData.lstOperator?.size!!
        }

        companion object {
            fun convertFrom(lists: List<CheckerTaskData>): MutableList<TaskData> {
                val results = mutableListOf<TaskData>()

                lists.forEach {
                    results.add(TaskData(it, 0))
                }

                return results
            }
        }

    }
}