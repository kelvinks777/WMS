package com.gin.wms.warehouse.checker

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.*
import com.gin.wms.manager.db.data.PutawayBookingOperatorData
import com.gin.wms.manager.db.data.ReassignOperatorData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_reassign_task.*
import java.text.MessageFormat

class ReassignTaskActivity : WarehouseActivity(), NgemartCardView.CardListener<TaskCard.TaskData> {

    companion object {
        const val ARG_CHECKER_TASK_REQUEST_CODE = 1000
        const val ARG_CHECKER_TASK_DOC_URI = "ARG_CHECKER_TASK_DOC_URI"
        const val ARG_CHECKER_TASK_RECEIVING_ORDER_NO = "ARG_CHECKER_TASK_RECEIVING_ORDER_NO"
        const val ARG_CHECKER_TASK_DOC_NO = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"
        const val ARG_CHECKER_TASK_MIN_OPERATOR = "ARG_CHECKER_TASK_MIN_OPERATOR"
    }

    private var listOfChoosedOperator: MutableList<PutawayBookingOperatorData>? = null
    private var listOfData: MutableList<TaskCard.TaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<TaskCard.TaskData>? = null
    private var gridLayoutManager: GridLayoutManager? = null

    private var userManager: UserManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var putawayBookingManager: PutawayBookingManager? = null
    private var dockingTaskManager: DockingTaskManager? = null
    private var releaseNewDocManager: ReleaseNewDocManager? = null
    private var higherPriorityTaskManager: HigherPriorityTaskManager? = null

    private var docUri: String? = ""
    private var receivingOrderNo: String? = ""
    private var docNo: String? = ""
    private var policeNo: String? = ""
    private var minOperator: Int? = 0

    private val onReleaseOperatorClick = View.OnClickListener { view ->
        try {

            if (listOfChoosedOperator?.size!! < minOperator!!) {
                showErrorDialog(getString(R.string.reassign_not_enough_operator_information))
                return@OnClickListener;
            }

            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object : ThreadHandler<Boolean?> {
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        val operators = mutableListOf<ReassignOperatorData>()
                        listOfChoosedOperator?.forEach {
                            operators.add(ReassignOperatorData(docUri, docNo, it.id, receivingOrderNo, it.policeNoBefore, it.taskIdBefore))
                        }
                        releaseNewDocManager?.reassignOperators(operators)
                        higherPriorityTaskManager?.removeHigherPriorityTask(policeNo, docNo)
                        return null
                    }

                    override fun onError(e: java.lang.Exception?) {
                        dismissProgressDialog()
                        showErrorDialog(e)
                    }

                    override fun onSuccess(data: Boolean?) {
                        dismissProgressDialog()
                        DismissActivity()
                    }

                    override fun onFinish() {
                    }

                })
            }
            showAskDialog(getString(R.string.common_confirmation_title), MessageFormat.format("Apakah anda ingin mengalihkan {0} operator yang sudah dipilih ke {1}?", listOfChoosedOperator?.size!!, policeNo), okListener, null)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_reassign_task)

            initObject()
            initComponent()
            initData()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {

        if (intent?.hasExtra(ARG_CHECKER_TASK_DOC_NO)!!) {
            docNo = intent?.getStringExtra(ARG_CHECKER_TASK_DOC_NO)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_POLICE_NO)!!) {
            policeNo = intent?.getStringExtra(ARG_CHECKER_TASK_POLICE_NO)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_RECEIVING_ORDER_NO)!!) {
            receivingOrderNo = intent?.getStringExtra(ARG_CHECKER_TASK_RECEIVING_ORDER_NO)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_DOC_URI)!!) {
            docUri = intent?.getStringExtra(ARG_CHECKER_TASK_DOC_URI)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_MIN_OPERATOR)!!) {
            minOperator = intent?.getIntExtra(ARG_CHECKER_TASK_MIN_OPERATOR, 0)
        }

        listOfChoosedOperator = mutableListOf()
        checkerTaskManager = CheckerTaskManager(this)
        userManager = UserManager(this)
        putawayBookingManager = PutawayBookingManager(this)
        dockingTaskManager = DockingTaskManager(this)
        releaseNewDocManager = ReleaseNewDocManager(this)
        higherPriorityTaskManager = HigherPriorityTaskManager(this)
    }

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        btnChooseOperator.setOnClickListener(onReleaseOperatorClick)
    }

    private fun initToolbar() {
        toolbar.setTitle("Reassign Operator " + policeNo)
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

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_task, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    override fun onCardClick(position: Int, view: View?, data: TaskCard.TaskData?) {
        val choosedOperators = listOfChoosedOperator?.
                filter { putawayBookingOperatorData ->
                    putawayBookingOperatorData.taskIdBefore.equals(data?.taskId)
                            && putawayBookingOperatorData.policeNoBefore.equals(data?.policeNo)
                }.orEmpty()
        val operators = mutableListOf<OperatorActivity.ChoosedTaskOperator>()
        choosedOperators.forEach { operators.add(OperatorActivity.ChoosedTaskOperator(it.id, it.name, data?.policeNo, data?.taskId)) }

        OperatorActivity.startActivityForResult(this, operators, data?.policeNo, data?.taskId)
    }

    private fun initData() {
        listOfData?.clear()
        listOfData?.addAll(TaskCard.TaskData.convertFrom(checkerTaskManager?.getListOfLocalCheckerTaskData().orEmpty()))
        adapter?.notifyDataSetChanged()
        tvTotal.text = MessageFormat.format(getString(R.string.total_reassigned_operators), listOfChoosedOperator?.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == OperatorActivity.REQ_CODE_CHOOSE_OPERATOR) {
                data?.let {
                    if (it.hasExtra(OperatorActivity.ARG_CHOOSED_OPERATORS)) {
                        val operators = gsonMapper.readList(it.getStringExtra(OperatorActivity.ARG_CHOOSED_OPERATORS), Array<OperatorActivity.ChoosedTaskOperator>::class.java)
                        operators?.forEach {
                            val existing = listOfChoosedOperator?.firstOrNull { putawayBookingOperatorData ->
                                putawayBookingOperatorData.id.equals(it.id)
                                        && putawayBookingOperatorData.policeNo.equals(policeNo)
                                        && putawayBookingOperatorData.taskId.equals(docNo)
                            }

                            if (existing != null)
                                listOfChoosedOperator?.remove(existing)

                            listOfChoosedOperator?.add(PutawayBookingOperatorData(policeNo
                                    , docNo
                                    , ""
                                    , it.id
                                    , it.name
                                    , it.policeNo
                                    , it.taskId)
                            )
                        }

                        val groups = listOfChoosedOperator?.groupingBy { putaway -> putaway.taskIdBefore }?.eachCount()
                        for (groupData in groups?.entries!!) {
                            listOfData?.filter { taskData -> taskData.taskId == groupData.key }?.firstOrNull()?.reassignCount = groupData.value
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    tvTotal.text = MessageFormat.format(getString(R.string.total_reassigned_operators), listOfChoosedOperator?.size)
                }
            }
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

}