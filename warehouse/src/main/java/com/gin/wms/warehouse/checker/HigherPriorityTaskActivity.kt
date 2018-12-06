package com.gin.wms.warehouse.checker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.HigherPriorityTaskManager
import com.gin.wms.manager.db.data.HigherPriorityTaskData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.checker.HigherPriorityTaskDetailActivity.Companion.ARG_CHECKER_TASK_DOC_NO
import com.gin.wms.warehouse.checker.HigherPriorityTaskDetailActivity.Companion.ARG_CHECKER_TASK_DOC_URI
import com.gin.wms.warehouse.checker.HigherPriorityTaskDetailActivity.Companion.ARG_CHECKER_TASK_POLICE_NO
import com.gin.wms.warehouse.checker.HigherPriorityTaskDetailActivity.Companion.ARG_CHECKER_TASK_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_higher_priority_task.*

class HigherPriorityTaskActivity : WarehouseActivity(), NgemartCardView.CardListener<HigherPriorityTaskData>, SwipeRefreshLayout.OnRefreshListener {
    private var checkerTaskManager: CheckerTaskManager? = null
    private var higherPriorityTaskManager: HigherPriorityTaskManager? = null
    private var listOfData: MutableList<HigherPriorityTaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<HigherPriorityTaskData>? = null
    private var gridLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_higher_priority_task)
            initObject()
            initComponent()
            initData()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        checkerTaskManager = CheckerTaskManager(this)
        higherPriorityTaskManager = HigherPriorityTaskManager(this)
    }

    private fun initComponent() {
        toolbar.title = "Higher Priority Task"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initRecyclerView()
        swipeRefresh.setOnRefreshListener(this)
    }

    private fun initRecyclerView() {
        val numColumn = 1

        listOfData = mutableListOf();
        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_higher_priority_task, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    override fun onCardClick(position: Int, view: View?, data: HigherPriorityTaskData?) {
        val intent = Intent(this, HigherPriorityTaskDetailActivity::class.java)

        intent.putExtra(ARG_CHECKER_TASK_POLICE_NO, data?.policeNo)
        intent.putExtra(ARG_CHECKER_TASK_DOC_URI, data?.refDocUri)
        intent.putExtra(ARG_CHECKER_TASK_DOC_NO, data?.refDocId)

        startActivityForResult(intent, ARG_CHECKER_TASK_REQUEST_CODE)
    }

    override fun onRefresh() {
        refreshData()
    }

    private fun initData() {
        listOfData?.clear()
        listOfData?.addAll(higherPriorityTaskManager?.listOfLocalHigherPriorityTaskData.orEmpty())
        adapter?.notifyDataSetChanged()

        refreshData()
    }

    private fun refreshData() {
        ThreadStart(object : ThreadHandler<List<HigherPriorityTaskData>?> {
            override fun onPrepare() {
                swipeRefresh.isRefreshing = true
            }

            override fun onBackground(): List<HigherPriorityTaskData>? {
                return higherPriorityTaskManager?.getNewOrderMonitoringWithHigherPriorityThanRcvCheckerTasks()
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: List<HigherPriorityTaskData>?) {
                listOfData?.clear()
                listOfData?.addAll(data.orEmpty())
                adapter?.notifyDataSetChanged()
            }

            override fun onFinish() {
                swipeRefresh.isRefreshing = false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode.equals(Activity.RESULT_OK)
                    && requestCode.equals(ARG_CHECKER_TASK_REQUEST_CODE)) {

                val policeNo = data?.getStringExtra(ARG_CHECKER_TASK_POLICE_NO)
                val refDocUri = data?.getStringExtra(ARG_CHECKER_TASK_DOC_URI)
                val refDocId = data?.getStringExtra(ARG_CHECKER_TASK_DOC_NO)

                val higherPriorityTaskData = higherPriorityTaskManager?.getLocalHigherPriorityTaskData(refDocUri, refDocId)

                val intent = Intent(this, ReassignTaskActivity::class.java)
                intent.putExtra(ReassignTaskActivity.ARG_CHECKER_TASK_POLICE_NO, policeNo)
                intent.putExtra(ReassignTaskActivity.ARG_CHECKER_TASK_DOC_NO, higherPriorityTaskData?.refDocId)
                intent.putExtra(ReassignTaskActivity.ARG_CHECKER_TASK_DOC_URI, higherPriorityTaskData?.refDocUri)
                intent.putExtra(ReassignTaskActivity.ARG_CHECKER_TASK_MIN_OPERATOR, higherPriorityTaskData?.minOperator)

                startActivityForResult(intent, ReassignTaskActivity.ARG_CHECKER_TASK_REQUEST_CODE)
            } else {
                refreshData()
            }

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }
}
