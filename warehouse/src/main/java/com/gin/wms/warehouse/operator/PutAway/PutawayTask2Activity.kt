package com.gin.wms.warehouse.operator.PutAway

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.PutawayManager
import com.gin.wms.warehouse.R
import com.gin.wms.manager.db.data.PutawayTaskData
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_putaway_task2.*

class PutawayTask2Activity : WarehouseActivity(), SwipeRefreshLayout.OnRefreshListener, NgemartCardView.CardListener<PutawayTaskData> {
    private var putawayManager: PutawayManager? = null
    private var listOfData: MutableList<PutawayTaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<PutawayTaskData>? = null
    private var gridLayoutManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_task2)

            initObject()
            initComponent()
            initData()

        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        listOfData = mutableListOf()
        putawayManager = PutawayManager(this)

    }

    private fun initComponent() {
        toolbar.title = "Putaway Task"
        setSupportActionBar(toolbar)
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

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_putaway_task, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    override fun onCardClick(position: Int, view: View?, data: PutawayTaskData?) {
        try {

        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onRefresh() {
        ThreadStart(object: ThreadHandler<List<PutawayTaskData>?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): List<PutawayTaskData>? {
                return putawayManager?.findPutawayTasks()
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: List<PutawayTaskData>?) {
                listOfData?.clear()
                listOfData?.addAll(data.orEmpty())
                adapter?.notifyDataSetChanged()
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun initData() {
        onRefresh()
    }

}
