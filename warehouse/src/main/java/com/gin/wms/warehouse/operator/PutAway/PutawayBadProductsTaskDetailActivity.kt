package com.gin.wms.warehouse.operator.PutAway

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.ProductManager
import com.gin.wms.manager.PutawayManager
import com.gin.wms.manager.db.data.PutawayTaskData
import com.gin.wms.manager.db.data.PutawayTaskItemData
import com.gin.wms.manager.db.data.enums.TaskStatusEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_putaway_bad_products_task_detail.*
import java.text.MessageFormat

class PutawayBadProductsTaskDetailActivity : WarehouseActivity() {

    companion object {
        val SCAN_PALLET = 1
        val SCAN_SOURCE = 2
    }

    private var putawayTaskData: PutawayTaskData? = null
    private var productManager: ProductManager? = null
    private var putawayTaskManager: PutawayManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null

    private var listOfData: MutableList<PutawayTaskItemData>? = null
    private var adapter: NgemartRecyclerViewAdapter<PutawayTaskItemData>? = null
    private var gridLayoutManager: GridLayoutManager? = null


    val onStartClickListener = View.OnClickListener { view ->
        try {
            if (putawayTaskData?.hasBeenStart!!) {
                doNextProcess()
            } else {
                doStartProcess()
            }

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun doStartProcess() {
        if (!putawayTaskData?.hasBeenStart !!) {
            btnStart.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
            putawayTaskData?.hasBeenStart = true
            btnStart.text = getString(R.string.btnContinue)
        }
        lockUi(false)

    }

    private fun lockUi(b: Boolean) {
        edPalletNo.isEnabled = !b
        edSource.isEnabled = !b
        btnScanPallet.isEnabled = !b
    }

    private fun doNextProcess() {
        if (tvPalletNo.text != edPalletNo.text.toString()) {
            showErrorDialog(MessageFormat.format(getString(R.string.wrong_pallet), edPalletNo.text.toString()))
            return
        }
        if (tvSource.text != edSource.text.toString()) {
            showErrorDialog(MessageFormat.format(getString(R.string.wrong_source), edSource.text.toString()))
            return
        }

        val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
            ThreadStart(object: ThreadHandler<Boolean?> {
                override fun onPrepare() {
                    startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                }

                override fun onBackground(): Boolean? {
                    putawayTaskManager?.startPutaway(putawayTaskData);
                    return true
                }

                override fun onError(e: java.lang.Exception?) {
                    dismissProgressDialog()
                    showErrorDialog(e)
                }

                override fun onSuccess(data: Boolean?) {
                    dismissProgressDialog()
                    startActivity(PutawayBadProductsTaskFinishActivity::class.java)
                }

                override fun onFinish() {

                }

            })
        }

        showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin memulai proses ini?", okListener, null)

    }

    val onScanPalletClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_PALLET)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }
    val onScanSourceClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_SOURCE)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_bad_products_task_detail)

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
        btnStart.setOnClickListener(onStartClickListener)
        btnScanPallet.setOnClickListener(onScanPalletClickListener)
        btnScanSource.setOnClickListener(onScanSourceClickListener)
    }

    private fun initData() {
        putawayTaskData = putawayTaskManager?.getLocalPutawayTask()
        if (putawayTaskData != null) {
            if (putawayTaskData?.getStatus() == TaskStatusEnum.PROGRESS) {
                startActivity(PutawayBadProductsTaskFinishActivity::class.java)
                return
            } else {
                tvPalletNo.text= putawayTaskData?.items?.get(0)?.palletNo
                tvSource.text= putawayTaskData?.items?.get(0)?.sourceId
                listOfData?.clear()
                listOfData?.addAll(putawayTaskData?.items.orEmpty())
                adapter?.notifyDataSetChanged()
            }
        }

        setUpUiIfProcessHasStarted()
    }

    private fun setUpUiIfProcessHasStarted(){
        if (putawayTaskData?.hasBeenStart !!) {
            btnStart.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
            btnStart.text = getString(R.string.btnContinue)
            lockUi(false)
        }else{
            lockUi(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null && result.contents != null) {
                    val barcodeId = result.contents
                    if (requestCode == SCAN_PALLET) {
                        if (putawayTaskData?.items?.get(0)?.palletNo != barcodeId) {
                            showErrorDialog(MessageFormat.format(getString(R.string.wrong_pallet), barcodeId))
                        } else {
                            edPalletNo.setText(barcodeId)
                        }
                    } else if (requestCode == SCAN_SOURCE) {
                        if (putawayTaskData?.items?.get(0)?.sourceId != barcodeId) {
                            showErrorDialog(MessageFormat.format(getString(R.string.wrong_source), barcodeId))
                        } else {
                            edSource.setText(barcodeId)
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