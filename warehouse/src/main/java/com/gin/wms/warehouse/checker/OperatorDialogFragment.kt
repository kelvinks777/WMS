package com.gin.wms.warehouse.checker

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import com.gin.ngemart.baseui.NgemartAdapterCommon
import com.gin.ngemart.baseui.NgemartDialogFragment
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.warehouse.R
import java.io.Serializable
import java.text.MessageFormat

/**
 * Created by manbaul on 3/23/2018.
 */
class OperatorDialogFragment : NgemartDialogFragment() {
    companion object {
        const val ARG_CHOOSED_OPERATORS = "ARG_CHOOSED_OPERATORS"
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"

        fun newInstance(operators: List<ChoosedTaskOperator>?, policeNo: String?, docNo: String?): OperatorDialogFragment {
            val bundle = Bundle()
            if (operators != null)
                bundle.putSerializable(ARG_CHOOSED_OPERATORS, operators as Serializable)
            bundle.putString(ARG_CHECKER_TASK_POLICE_NO, policeNo)
            bundle.putString(ARG_CHECKER_TASK_TASK_ID, docNo)

            val newInstance = OperatorDialogFragment()
            newInstance.arguments = bundle

            return newInstance
        }
    }

    private var checkerTaskData:CheckerTaskData? = null
    private var choosedOperators: MutableList<ChoosedTaskOperator>? = null
    private var listOfData: MutableList<TaskOperator>? = null
    private var checkerTaskManager: CheckerTaskManager? = null

    private var policeNo: String? = null
    private var taskId: String? = null
    private var dialogListener: IOperatorView? = null

    private var btnCancel: Button? = null
    private var btnConfirm: Button? = null
    private var listView: ListView? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            checkerTaskManager = CheckerTaskManager(activity)
            dialogListener = activity as IOperatorView
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement IOperatorView")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getPassingArguments()
        val view = initComponent()
        val alertDialog = AlertDialog.Builder(activity)
                .setView(view)
                .create()

        alertDialog.setOnShowListener { dialogInterface ->
            initButton(alertDialog)
        }

        setBeAbleToCanceled(alertDialog, true)
        return alertDialog
    }

    private fun initComponent(): View {
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_operator, null)

        btnCancel = view.findViewById(R.id.btnCancel)
        btnConfirm  =view.findViewById(R.id.btnConfirm)

        val tvPoliceNo = view.findViewById<TextView>(R.id.tvPoliceNo)
        tvPoliceNo.text = policeNo

        val tvDocNo = view.findViewById<TextView>(R.id.tvDocNo)
        tvDocNo.text = taskId

        listView = view.findViewById(R.id.listView)
        checkerTaskData = checkerTaskManager?.getLocalCheckerTaskData(taskId)
        listOfData = mutableListOf()
        checkerTaskData?.lstOperator?.forEach {
            if (choosedOperators != null && choosedOperators!!.any { operatorData -> operatorData.id.equals(it.id) }) {
                listOfData?.add(TaskOperator(it.id, it.name, checkerTaskData?.policeNo, it.taskId, true))
            } else {
                listOfData?.add(TaskOperator(it.id, it.name, checkerTaskData?.policeNo, it.taskId, false))
            }
        }

        val adapter: NgemartAdapterCommon<TaskOperator> = NgemartAdapterCommon(activity, R.layout.dialog_operator_item, listOfData, NgemartAdapterCommon.INgemartAdapterHandler { data, detailView ->
            initDetailComponent(detailView, data)
        })

        listView?.adapter = adapter
        return view
    }


    private fun getPassingArguments() {
        choosedOperators = arguments?.getSerializable(ARG_CHOOSED_OPERATORS) as MutableList<ChoosedTaskOperator>?
        policeNo = arguments?.getString(ARG_CHECKER_TASK_POLICE_NO)
        taskId = arguments?.getString(ARG_CHECKER_TASK_TASK_ID)

    }

    private fun initDetailComponent(detailView: View?, data: TaskOperator?): View? {
        val tvMenuTitle = detailView?.findViewById<TextView>(R.id.tvMenuTitle)
        tvMenuTitle?.text = data?.name

        val tvMenuSubTitle = detailView?.findViewById<TextView>(R.id.tvMenuSubTitle)
        tvMenuSubTitle?.text = data?.id

        val swChoosed = detailView?.findViewById<Switch>(R.id.swChoosed)
        swChoosed?.isChecked = data?.isChoosed!!
        swChoosed?.setOnCheckedChangeListener { compoundButton, b ->
            data.isChoosed = b
        }

        return detailView
    }

    private fun initButton(alertDialog: AlertDialog) {

        btnCancel?.setOnClickListener { view ->
            alertDialog.dismiss()
        }

        btnConfirm?.setOnClickListener { view ->
            val operators = listOfData?.filter { it.isChoosed!! }.orEmpty()
            if (operators.size > 0
                    && (listOfData?.size!! - operators.size < checkerTaskData?.minOperator!!) ) {
                activity.showErrorDialog("Jumlah operator kurang dari minimal operator yang dibutuhkan")
                return@setOnClickListener
            }

            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                dialogListener?.chooseOperators(operators)
                alertDialog.dismiss()
            }

            activity.showAskDialog(getString(R.string.common_confirmation_title), MessageFormat.format(getString(R.string.choose_operator_for_release_content_confirmation), operators.size), okListener, null)
        }
    }

    class TaskOperator: ChoosedTaskOperator {
        var isChoosed: Boolean? = null

        constructor(id: String?, name: String?, policeNo: String?, docNo: String?, isChoosed: Boolean?) : super(id, name, policeNo, docNo) {
            this.isChoosed = isChoosed
        }

    }

    open class ChoosedTaskOperator(var id: String?, var name: String?, var policeNo: String?, var taskId: String?) : Serializable

    interface IOperatorView {
        fun chooseOperators(operators: List<ChoosedTaskOperator>?)
    }

}