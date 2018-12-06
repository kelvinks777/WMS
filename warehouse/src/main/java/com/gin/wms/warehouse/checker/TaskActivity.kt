package com.gin.wms.warehouse.checker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.PutawayBookingManager
import com.gin.wms.manager.DockingTaskManager
import com.gin.wms.manager.UserManager
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.manager.db.data.CheckerTaskOperatorData
import com.gin.wms.manager.db.data.PutawayBookingOperatorData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_task.*
import java.text.MessageFormat

class TaskActivity : WarehouseActivity(), NgemartCardView.CardListener<CheckerTaskData>, OperatorDialogFragment.IOperatorView {

    companion object {
        const val ARG_CHECKER_TASK_DOC_NO = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"
        const val ARG_CHECKER_TASK_PRODUCT_ID = "ARG_CHECKER_TASK_PRODUCT_ID"
        const val ARG_CHECKER_TASK_RECEIVING_NO = "ARG_CHECKER_TASK_RECEIVING_NO"
        const val ARG_CHECKER_TASK_CLIENT_ID = "ARG_CHECKER_TASK_CLIENT_LOCATION_ID"
        const val ARG_CHECKER_TASK_PALLET_QTY = "ARG_CHECKER_TASK_PALLET_QTY"
    }

    private var listOfChoosedOperator: MutableList<PutawayBookingOperatorData>? = null
    private var listOfData: MutableList<TaskCard.TaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<TaskCard.TaskData>? = null
    private var gridLayoutManager: GridLayoutManager? = null

    private var userManager: UserManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var putawayBookingManager: PutawayBookingManager? = null
    private var dockingTaskManager: DockingTaskManager? = null

    private var docNo: String? = ""
    private var policeNo: String? = ""
    private var productId: String? = ""
    private var receivingNo: String? = ""
    private var clientId: String? = ""
    private var palletQty: Int? = 0

    private val onReleaseOperatorClick = View.OnClickListener { view ->
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object : ThreadHandler<Boolean?> {
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        val operators = mutableListOf<CheckerTaskOperatorData>()
                        listOfChoosedOperator?.forEach {
                            operators.add(CheckerTaskOperatorData(it.taskIdBefore, it.id, it.name))
                        }

                        dockingTaskManager?.releaseDockingListWithInterrupt(operators)
                        putawayBookingManager?.savePutawayBooking(policeNo, docNo, productId, receivingNo, clientId, palletQty!!, listOfChoosedOperator)
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
            showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin me-rilis operator yang sudah dipilih?", okListener, null)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_task)

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

        if (intent?.hasExtra(ARG_CHECKER_TASK_PRODUCT_ID)!!) {
            productId = intent?.getStringExtra(ARG_CHECKER_TASK_PRODUCT_ID)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_RECEIVING_NO)!!) {
            receivingNo = intent?.getStringExtra(ARG_CHECKER_TASK_RECEIVING_NO)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_CLIENT_ID)!!) {
            clientId = intent?.getStringExtra(ARG_CHECKER_TASK_CLIENT_ID)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_PALLET_QTY)!!) {
            palletQty = intent?.getIntExtra(ARG_CHECKER_TASK_PALLET_QTY, 0)
        }

        listOfChoosedOperator = mutableListOf()
        checkerTaskManager = CheckerTaskManager(this)
        userManager = UserManager(this)
        putawayBookingManager = PutawayBookingManager(this)
        dockingTaskManager = DockingTaskManager(this)
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

    override fun onCardClick(position: Int, view: View?, data: CheckerTaskData?) {
        val choosedOperators = listOfChoosedOperator?.
                filter { putawayBookingOperatorData ->
                    putawayBookingOperatorData.taskIdBefore.equals(data?.taskId)
                            && putawayBookingOperatorData.policeNoBefore.equals(data?.policeNo)
                }.orEmpty()
        val operators = mutableListOf<OperatorDialogFragment.ChoosedTaskOperator>()
        choosedOperators.forEach { operators.add(OperatorDialogFragment.ChoosedTaskOperator(it.id, it.name, data?.policeNo, data?.taskId)) }

        val dialogFragment = OperatorDialogFragment.newInstance(operators, data?.policeNo, data?.taskId)
        dialogFragment.show(supportFragmentManager, "dialog");
    }

    override fun chooseOperators(operators: List<OperatorDialogFragment.ChoosedTaskOperator>?) {
        try {
            operators?.forEach {
                val existing = listOfChoosedOperator?.firstOrNull { putawayBookingOperatorData ->
                    putawayBookingOperatorData.id.equals(it.id)
                            && putawayBookingOperatorData.policeNo.equals(policeNo)
                            && putawayBookingOperatorData.taskId.equals(docNo)
                            && putawayBookingOperatorData.productId.equals(productId)
                }

                if (existing != null)
                    listOfChoosedOperator?.remove(existing)

                listOfChoosedOperator?.add(PutawayBookingOperatorData(policeNo
                        , docNo
                        , productId
                        , it.id
                        , it.name
                        , it.policeNo
                        , it.taskId)
                )
            }
            btnChooseOperator.text = MessageFormat.format(getString(R.string.button_release_n_operator), listOfChoosedOperator?.size)
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private fun initData() {
        listOfData?.clear()
        listOfData?.addAll(TaskCard.TaskData.convertFrom(checkerTaskManager?.getListOfLocalCheckerTaskData().orEmpty()))
        adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)


        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }


}
