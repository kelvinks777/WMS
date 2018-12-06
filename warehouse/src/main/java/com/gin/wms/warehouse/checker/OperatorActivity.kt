package com.gin.wms.warehouse.checker

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.manager.db.data.CheckerTaskOperatorData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_operator.*
import java.io.Serializable
import java.text.MessageFormat

class OperatorActivity : WarehouseActivity() {

    class TaskOperator: ChoosedTaskOperator {
        var isChoosed: Boolean? = null

        constructor(id: String?, name: String?, policeNo: String?, taskId: String?, isChoosed: Boolean?) : super(id, name, policeNo, taskId) {
            this.isChoosed = isChoosed
        }

    }

    open class ChoosedTaskOperator(var id: String?, var name: String?, var policeNo: String?, var taskId: String?) : Serializable

    companion object {
        const val REQ_CODE_CHOOSE_OPERATOR = 1000
        const val REQ_CODE_MOVING_TO_DOCKING = 102
        const val ARG_CHOOSED_OPERATORS = "ARG_CHOOSED_OPERATORS"
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"
        const val ARG_REQ_CODE_CHOOSE_OPERATOR = "ARG_REQ_CODE_CHOOSE_OPERATOR"

        fun startActivityForResult(warehouseActivity: WarehouseActivity, operators: List<ChoosedTaskOperator>?, policeNo: String?, taskId: String?) {
            startActivityForResult(warehouseActivity, operators, policeNo, taskId, REQ_CODE_CHOOSE_OPERATOR)
        }

        fun startActivityForResult(warehouseActivity: WarehouseActivity, operators: List<ChoosedTaskOperator>?, policeNo: String?, taskId: String?, requestCode: Int) {
            val intent = Intent(warehouseActivity, OperatorActivity::class.java)
            intent.putExtra(OperatorActivity.ARG_CHECKER_TASK_POLICE_NO, policeNo)
            intent.putExtra(OperatorActivity.ARG_CHECKER_TASK_TASK_ID, taskId)
            intent.putExtra(OperatorActivity.ARG_REQ_CODE_CHOOSE_OPERATOR, requestCode)
            operators?.let {
                if (it.size > 0)
                    intent.putExtra(OperatorActivity.ARG_CHOOSED_OPERATORS, warehouseActivity.gsonMapper.write(operators))
            }
            warehouseActivity.startActivityForResult(intent, requestCode)
        }

    }

    private var checkerTaskData: CheckerTaskData? = null
    private var choosedOperators: MutableList<ChoosedTaskOperator>? = null
    private var listOfData: MutableList<TaskOperator>? = null
    private var checkerTaskManager: CheckerTaskManager? = null

    private var policeNo: String? = null
    private var taskId: String? = null
    private var requestCode: Int? = null

    private var adapter: NgemartRecyclerViewAdapter<TaskOperator>? = null
    private var gridLayoutManager: GridLayoutManager? = null


    private val onChooseOperator = View.OnClickListener { view ->
        try {
            val operators = listOfData?.filter { it.isChoosed!! }.orEmpty()

            if (requestCode != REQ_CODE_MOVING_TO_DOCKING) {
                if (operators.size > 0
                        && (listOfData?.size!! - operators.size < checkerTaskData?.minOperator!!)) {

                    showErrorDialog("Jumlah operator kurang dari minimal operator yang dibutuhkan")
                    return@OnClickListener
                }
            }

            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent()
                intent.putExtra(ARG_CHOOSED_OPERATORS, gsonMapper.write(operators))
                setResult(Activity.RESULT_OK, intent)
                DismissActivity()
            }

            if (requestCode == REQ_CODE_MOVING_TO_DOCKING){
                showAskDialog(getString(R.string.common_confirmation_title), MessageFormat.format(getString(R.string.choose_operator_for_moving_to_docking_content_confirmation), operators.size), okListener, null)
            }
            else {
                showAskDialog(getString(R.string.common_confirmation_title), MessageFormat.format(getString(R.string.choose_operator_for_release_content_confirmation), operators.size), okListener, null)
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_operator)

            initObject()
            initComponent()
            initData()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun getPassingArguments() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!) {
            taskId = intent?.getStringExtra(ARG_CHECKER_TASK_TASK_ID)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_POLICE_NO)!!) {
            policeNo = intent?.getStringExtra(ARG_CHECKER_TASK_POLICE_NO)
        }

        if (intent?.hasExtra(ARG_REQ_CODE_CHOOSE_OPERATOR)!!) {
            requestCode = intent?.getIntExtra(ARG_REQ_CODE_CHOOSE_OPERATOR, 0)
        }

        choosedOperators = mutableListOf()
        if (intent?.hasExtra(ARG_CHOOSED_OPERATORS)!!) {
            val strData = intent?.getStringExtra(ARG_CHOOSED_OPERATORS)
            if (!strData.isNullOrEmpty()) {
                choosedOperators = gsonMapper.readList(strData, Array<ChoosedTaskOperator>::class.java)
            }
        }
    }

    private fun initObject() {
        getPassingArguments()
        checkerTaskManager = CheckerTaskManager(this)
    }

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        btnChooseOperator.setOnClickListener(onChooseOperator)
    }

    private fun initToolbar() {
        toolbar.setTitle("Operator List")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecyclerView() {
        val numColumn = 1

        listOfData = mutableListOf();
        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_operator, listOfData)
        adapter?.setHasStableIds(true)
        rvList?.adapter = adapter
    }

    private fun initData() {
        checkerTaskData = checkerTaskManager?.getLocalCheckerTaskData(taskId)

        tvDocNo.text = taskId
        tvPoliceNo.text = policeNo
        tvDocking.text = checkerTaskData?.dockingIds
        tvStaging.text = checkerTaskData?.stagingId

        checkerTaskData?.lstOperator?.forEach {
            if (choosedOperators != null && choosedOperators!!.any { operatorData -> operatorData.id.equals(it.id) }) {
                listOfData?.add(TaskOperator(it.id, it.name, checkerTaskData?.policeNo, it.taskId, true))
            } else {
                listOfData?.add(TaskOperator(it.id, it.name, checkerTaskData?.policeNo, it.taskId, false))
            }
        }

        adapter?.notifyDataSetChanged()


    }

}
