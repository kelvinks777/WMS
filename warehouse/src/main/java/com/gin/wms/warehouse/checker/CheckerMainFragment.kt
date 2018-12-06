package com.gin.wms.warehouse.checker

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.db.data.CheckerTaskData

import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseFragment
import kotlinx.android.synthetic.main.fragment_checker_main.*

class CheckerMainFragment : WarehouseFragment() {

    private var gridLayoutManager: GridLayoutManager? = null
    private var listOfCheckerTaskTask: MutableList<CheckerTaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<CheckerTaskData>? = null
    private var checkerTaskManager: CheckerTaskManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checker_main, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            initObject()
            initComponent()
            initData()

        } catch (e: Exception) {
            fragmentActivity.showErrorDialog(e)
        }
    }

    private fun initObject() {
        checkerTaskManager = CheckerTaskManager(fragmentActivity)
    }

    private fun initComponent() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val numColumn = 2

        listOfCheckerTaskTask = mutableListOf()
        gridLayoutManager = GridLayoutManager(fragmentActivity, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(fragmentActivity, R.layout.card_checker_main, listOfCheckerTaskTask)
        adapter?.setHasStableIds(true)
        rvList?.adapter = adapter
    }

    private fun initData() {
        initTaskList()
    }

    private fun initTaskList() {
        ThreadStart(object : NgemartActivity.ThreadHandler<MutableList<CheckerTaskData>?> {
            override fun onPrepare() {
                fragmentActivity.startProgressDialog("",NgemartActivity.ProgressType.SPINNER)
            }

            override fun onBackground(): MutableList<CheckerTaskData>? {
                return checkerTaskManager?.findTask()
            }

            override fun onError(e: java.lang.Exception?) {
                fragmentActivity.showErrorDialog(e)
            }

            override fun onSuccess(data: MutableList<CheckerTaskData>?) {
                listOfCheckerTaskTask?.clear()
                listOfCheckerTaskTask?.addAll(data.orEmpty())
                adapter?.notifyDataSetChanged()
            }

            override fun onFinish() {
                fragmentActivity.dismissProgressDialog()
            }
        })
    }

    companion object {
        fun newInstance(): CheckerMainFragment {
            val fragment = CheckerMainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}