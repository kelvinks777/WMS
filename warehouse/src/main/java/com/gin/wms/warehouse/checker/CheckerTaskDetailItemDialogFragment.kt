package com.gin.wms.warehouse.checker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import com.gin.ngemart.baseui.NgemartDialogFragment
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.warehouse.R

/**
 * Created by manbaul on 4/17/2018.
 */
class CheckerTaskDetailItemDialogFragment: NgemartDialogFragment() {
    companion object {
        const val ARG_CHECKER_TASK_ITEM = "ARG_CHECKER_TASK_ITEM"

        fun newInstance(checkerTaskItemData: CheckerTaskItemData?): CheckerTaskDetailItemDialogFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARG_CHECKER_TASK_ITEM, checkerTaskItemData)

            val newInstance = CheckerTaskDetailItemDialogFragment()
            newInstance.arguments = bundle

            return newInstance
        }
    }

    private var dialogListener: ICheckerTaskDetailItemDialog? = null
    private var checkerTaskItemData: CheckerTaskItemData? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            dialogListener = activity as ICheckerTaskDetailItemDialog
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement ICheckerTaskDetailItemDialog")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_checker_task_detail_item, null)

        checkerTaskItemData = arguments?.getSerializable(ARG_CHECKER_TASK_ITEM) as CheckerTaskItemData?

        val btnPutaway = view.findViewById<Button>(R.id.btnPutaway)
        val btnInputCheckResult = view.findViewById<Button>(R.id.btnInputCheckResult)


        val alertDialog = AlertDialog.Builder(activity)
                .setView(view)
                .create()

        alertDialog.setOnShowListener { dialogInterface ->
            btnPutaway.setOnClickListener({
                dialogListener?.doPutaway(checkerTaskItemData)
                alertDialog.dismiss()
            })

            btnInputCheckResult.setOnClickListener({
                dialogListener?.doCheckResult(checkerTaskItemData)
                alertDialog.dismiss()
            })

        }

        setBeAbleToCanceled(alertDialog, true)
        return alertDialog
    }

    interface ICheckerTaskDetailItemDialog {
        fun doCheckResult(checkerTaskItemData: CheckerTaskItemData?)
        fun doPutaway(checkerTaskItemData: CheckerTaskItemData?)
    }
}