package com.gin.wms.warehouse.operator.PutAway

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.ProductManager
import com.gin.wms.manager.PutawayManager
import com.gin.wms.manager.db.data.PutawayTaskData
import com.gin.wms.manager.db.data.PutawayTaskItemData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.operator.TaskSwitcherActivity
import kotlinx.android.synthetic.main.activity_putaway_bad_products_task_finish.*
import java.text.MessageFormat

class PutawayBadProductsTaskFinishActivity : WarehouseActivity() {

    companion object {
        val SCAN_DESTINATION = 1
    }

    private var putawayTaskData: PutawayTaskData? = null
    private var productManager: ProductManager? = null
    private var putawayTaskManager: PutawayManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null

    private var listOfData: MutableList<PutawayTaskItemData>? = null
    private var adapter: NgemartRecyclerViewAdapter<PutawayTaskItemData>? = null
    private var gridLayoutManager: GridLayoutManager? = null


    val onStartClickListener = View.OnClickListener { view ->
        if (tvDestination.text != edDestination.text.toString()) {
            showErrorDialog(MessageFormat.format(getString(R.string.wrong_destination), edDestination.text.toString()))
            return@OnClickListener
        }


        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object: ThreadHandler<Boolean?> {
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        putawayTaskManager?.finishPutaway(putawayTaskData);
                        return true
                    }

                    override fun onError(e: java.lang.Exception?) {
                        dismissProgressDialog()
                        showErrorDialog(e)
                    }

                    override fun onSuccess(data: Boolean?) {
                        dismissProgressDialog()
                        val intent = Intent(this@PutawayBadProductsTaskFinishActivity, TaskSwitcherActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFinish() {

                    }

                })
            }

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin menyelesaikan putaway task?", okListener, null)

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onScanDestinationClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_DESTINATION)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_bad_products_task_finish)

            initObject()
            initComponent()
            initData()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        productManager = ProductManager(this)
        checkerTaskManager = CheckerTaskManager(this)
        putawayTaskManager = PutawayManager(this)
    }

    private fun initComponent() {
        initButton()
        initToolbar()
        initRecyclerView()
        setExternalBarcodeActive(true)
    }

    private fun initRecyclerView() {
        val numColumn = 1

        listOfData = mutableListOf();
        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_putaway_bad_products_task_detail, listOfData)
        adapter?.setHasStableIds(true)
        rvList?.adapter = adapter
    }

    private fun initToolbar() {
        toolbar.setTitle("Putaway Bad Products")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun initButton() {
        btnFinish.setOnClickListener(onStartClickListener)
        btnScanDestination.setOnClickListener(onScanDestinationClickListener)
    }

    private fun initData() {
        putawayTaskData = putawayTaskManager?.getLocalPutawayTask()
        if (putawayTaskData != null) {
            tvPalletNo.text = putawayTaskData?.items?.get(0)?.palletNo
            tvSource.text = putawayTaskData?.items?.get(0)?.sourceId
            tvDestination.text = putawayTaskData?.destinationId
            listOfData?.clear()
            listOfData?.addAll(putawayTaskData?.items.orEmpty())
            adapter?.notifyDataSetChanged()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents != null) {
                        val barcodeId = result.contents
                        if (requestCode == SCAN_DESTINATION) {
                            if (putawayTaskData?.destinationId != barcodeId) {
                                showErrorDialog(MessageFormat.format(getString(R.string.wrong_destination), barcodeId))
                            } else {
                                edDestination.setText(barcodeId)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }

    }

    override fun onBackPressed() {
        showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.warning_must_complete_the_task))
    }

}