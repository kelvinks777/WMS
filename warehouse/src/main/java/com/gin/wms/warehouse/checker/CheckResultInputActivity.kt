package com.gin.wms.warehouse.checker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.CheckerTaskItemResultData
import com.gin.wms.manager.db.data.enums.RefDocUriEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_check_result_input.*

class CheckResultInputActivity : WarehouseActivity(), NgemartCardView.CardListener<CheckerTaskItemResultData>, SwipeRefreshLayout.OnRefreshListener {
    companion object {
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_PRODUCT_ID = "ARG_CHECKER_TASK_PRODUCT_ID"
        const val ARG_CHECKER_TASK_CLIENT_LOCATION_ID = "ARG_CHECKER_TASK_CLIENT_LOCATION_ID"
        const val ARG_CHECKER_TASK_REF_DOC_URI = "ARG_CHECKER_TASK_REF_DOC_URI"
    }

    private var checkerTaskManager: CheckerTaskManager? = null
    private var taskId:String? = null
    private var productId:String? = null
    private var refDocUri: RefDocUriEnum? = null
    private var clientLccationId:String? = null
    private var checkerTaskItemData: CheckerTaskItemData? = null
    private var listOfData: MutableList<CheckerTaskItemResultData> ? = null
    private var adapter: NgemartRecyclerViewAdapter<CheckerTaskItemResultData>? = null
    private var gridLayoutManager: GridLayoutManager? = null


    //region Override Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_check_result_input)

            initObject()
            initComponent()
            initData()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onRefresh() {
        try {
            refreshDataThread()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCardClick(position: Int, view: View?, data: CheckerTaskItemResultData?) {
        try {
            when (view?.id) {
                R.id.btnEditItem -> {
                    doEditItemResult(data)
                }
                R.id.btnRemoveItem -> {
                    doRemoveItemResult(data)
                }
            }
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            refreshData()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_checker_task_update_fsid, menu)
            return true
        } catch (e: Exception) {
            showErrorDialog(e)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        try {
            when (item?.itemId) {
                R.id.actUpdateFSidCheckerMain -> {
                    showAskDialog("Update FSid", "Apakah Anda yakin mengupdate FSid sekarang?",
                            { _, _ ->
                                updateFSidThread()
                            }) { dialog, _ ->
                        dialog.dismiss()
                    }

                    return true
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
        return super.onOptionsItemSelected(item)
    }

    //endregion

    //region Private Functions

    //region Init Functions

    private fun initObject() {
        checkerTaskManager = CheckerTaskManager(this)
        getIntentData()
    }

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        swipeRefresh.setOnRefreshListener (this)
        fabAddPallet.setOnClickListener(onAddPalletClick)
    }

    private fun initTextView() {
        tvPoliceNo.text = checkerTaskItemData?.policeNo
        tvProductId.text = checkerTaskItemData?.productId
        tvProductName.text = checkerTaskItemData?.productName
        tvClientLocationId.text = checkerTaskItemData?.clientLocationId
        tvClientId.text = checkerTaskItemData?.clientId
        tvCompUomId.text = checkerTaskItemData?.getCompUom()?.compUomId
        tvGoodQty.text = checkerTaskItemData?.goodQtyCheckResultCompUomValue
        tvBadQty.text = checkerTaskItemData?.badQtyCheckResultCompUomValue
    }

    private fun initToolbar() {
        toolbar.setTitle("Input Checker Result")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        refreshDataThread()
        initTextView()
    }

    private fun initRecyclerView() {
        listOfData = mutableListOf()

        val numColumn = 1

        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_check_result_input, listOfData)
        adapter?.setRecyclerListener (this)
        adapter?.setHasStableIds(true)
        rvList?.adapter = adapter
    }

    //endregion

    private fun getIntentData() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!)
            taskId = intent?.getStringExtra(ARG_CHECKER_TASK_TASK_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_PRODUCT_ID)!!)
            productId = intent?.getStringExtra(ARG_CHECKER_TASK_PRODUCT_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)!!)
            clientLccationId = intent?.getStringExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_REF_DOC_URI)!!)
            refDocUri = RefDocUriEnum.init(intent?.getStringExtra(ARG_CHECKER_TASK_REF_DOC_URI))

    }

    private var onAddPalletClick = View.OnClickListener { view ->
        showInputResult(CheckResultItemInputActivity.ARG_MODE_ADD, "")
    }

    private fun showInputResult(mode: String, palletNo: String) {
        val intent = Intent(this, CheckResultItemInputActivity::class.java)
        intent.putExtra(CheckResultItemInputActivity.ARG_INPUT_MODE, mode)
        intent.putExtra(CheckResultItemInputActivity.ARG_CHECKER_TASK_TASK_ID, taskId)
        intent.putExtra(CheckResultItemInputActivity.ARG_CHECKER_TASK_PRODUCT_ID, productId)
        intent.putExtra(CheckResultItemInputActivity.ARG_CHECKER_TASK_CLIENT_LOCATION_ID, clientLccationId)
        intent.putExtra(CheckResultItemInputActivity.ARG_CHECKER_TASK_PALLET_NO, palletNo)
        intent.putExtra(CheckResultItemInputActivity.ARG_CHECKER_TASK_REF_URI, refDocUri?.value)
        startActivityForResult(intent, CheckResultItemInputActivity.REQUEST_CODE)
    }

    private fun doRemoveItemResult(data: CheckerTaskItemResultData?) {
        val okListener = DialogInterface.OnClickListener { _, _ ->
            removeItemResultThread(data)
        }
        showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin menghapus item ini?",okListener, null)
    }

    private fun doEditItemResult(data: CheckerTaskItemResultData?) {
        showInputResult(CheckResultItemInputActivity.ARG_MODE_EDIT, data?.palletNo!!)
    }

    private fun refreshData() {
        listOfData?.clear()
        checkerTaskItemData = checkerTaskManager?.getLocalCheckerTaskItem(taskId, productId, clientLccationId)
        checkerTaskItemData?.let {
            listOfData?.addAll(checkerTaskItemData!!.results)
        }
        sortListOfData()

        tvGoodQty.text = checkerTaskItemData?.goodQtyCheckResultCompUomValue
        tvBadQty.text = checkerTaskItemData?.badQtyCheckResultCompUomValue

        adapter?.notifyDataSetChanged()
    }

    private fun sortListOfData() {
        val sortedData = listOfData?.sortedBy { checkerTaskItemResultData ->
            if (checkerTaskItemResultData.goodQty > 0) {
                0
            } else {
                1
            }
        }
        listOfData?.clear()
        listOfData?.addAll(sortedData?.toList().orEmpty())
    }

    //endregion

    //region Threads

    private fun updateFSidThread(){
        ThreadStart2(object : NgemartActivity.ThreadHandler2<Boolean>(this) {
            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                checkerTaskManager?.updateFSid()
                return null
            }
        })
    }

    private fun removeItemResultThread(data: CheckerTaskItemResultData?){
        ThreadStart(object:ThreadHandler<Boolean>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean {
                checkerTaskManager?.deleteResult(data?.taskId, data?.productId, data?.clientLocationId, data?.palletNo )
                return true
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(result: Boolean?) {
                refreshDataThread()
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun refreshDataThread() {
        refreshData()
        ThreadStart(object:ThreadHandler<List<CheckerTaskItemResultData>?>{
            override fun onPrepare() {
                swipeRefresh.isRefreshing=true
            }

            override fun onBackground(): List<CheckerTaskItemResultData>? {
                return checkerTaskManager?.getCheckerTaskItemResults(taskId, productId, clientLccationId)
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: List<CheckerTaskItemResultData>?) {
                listOfData?.clear()
                data?.let { listOfData?.addAll(it) }
                sortListOfData()
                adapter?.notifyDataSetChanged()
            }

            override fun onFinish() {
                swipeRefresh.isRefreshing=false
            }
        })
    }

    //endregion
}
