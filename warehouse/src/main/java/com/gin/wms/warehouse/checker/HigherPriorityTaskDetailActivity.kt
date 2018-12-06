package com.gin.wms.warehouse.checker

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.HigherPriorityTaskManager
import com.gin.wms.manager.OperatorManager
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.HigherPriorityTaskItemData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_higher_priority_task_detail.*

class HigherPriorityTaskDetailActivity : WarehouseActivity() {
    companion object {
        const val ARG_CHECKER_TASK_REQUEST_CODE = 1000
        const val ARG_CHECKER_TASK_DOC_URI = "ARG_CHECKER_TASK_DOC_URI"
        const val ARG_CHECKER_TASK_DOC_NO = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"
    }

    private var listOfData: MutableList<HigherPriorityTaskItemData>? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var higherPriorityTaskManager: HigherPriorityTaskManager? = null
    private var operatorManager: OperatorManager? = null
    private var adapter: NgemartRecyclerViewAdapter<HigherPriorityTaskItemData>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var docNo: String? = null
    private var docUri: String? = null
    private var policeNo: String? = null

    private var onReassignOperatorClick = View.OnClickListener { view ->
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent()

                intent.putExtra(ARG_CHECKER_TASK_DOC_NO, docNo)
                intent.putExtra(ARG_CHECKER_TASK_DOC_URI, docUri)
                intent.putExtra(ARG_CHECKER_TASK_POLICE_NO, policeNo)

                setResult(Activity.RESULT_OK, intent)
                DismissActivity()
            }

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin melakukan reassign operator untuk order ini?", okListener, null)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private var onRejectClick = View.OnClickListener { view ->
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object: ThreadHandler<Boolean?>{
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        higherPriorityTaskManager?.rejectOrderMonitoringBasedOrderNo(docUri, docNo)
                        return true
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

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin me-reject order ini?",okListener, null)

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_higher_priority_task_detail)

            initObject()
            initComponent()
            initData()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        initButton()

    }

    private fun initButton() {
        btnReassignOperator.setOnClickListener(onReassignOperatorClick)
        btnReject.setOnClickListener(onRejectClick)
    }

    private fun initToolbar() {
        toolbar.setTitle("Higher Priority Task Detail")
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

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_higher_priority_task_detail , listOfData)
        adapter?.setHasStableIds(true)
        rvList?.adapter = adapter
    }

    private fun initObject() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_DOC_URI)!!) {
            docUri = intent?.getStringExtra(ARG_CHECKER_TASK_DOC_URI)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_DOC_NO)!!) {
            docNo = intent?.getStringExtra(ARG_CHECKER_TASK_DOC_NO)
        }

        if (intent?.hasExtra(ARG_CHECKER_TASK_POLICE_NO)!!) {
            policeNo = intent?.getStringExtra(ARG_CHECKER_TASK_POLICE_NO)
        }

        operatorManager = OperatorManager(this)
        checkerTaskManager = CheckerTaskManager(this)
        higherPriorityTaskManager = HigherPriorityTaskManager(this)
    }

    private fun initData() {

        val higherTaskData = higherPriorityTaskManager?.getLocalHigherPriorityTaskData(docUri, docNo)
        higherTaskData?.let {
            tvPoliceNo.text = it.policeNo
            tvDocking.text = it.dockingIds
            listOfData?.clear()
            listOfData?.addAll(it.lstProduct.orEmpty())
            adapter?.notifyDataSetChanged()
        }
    }
}
