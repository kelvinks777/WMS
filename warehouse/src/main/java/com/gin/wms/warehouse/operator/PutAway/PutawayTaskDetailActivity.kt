package com.gin.wms.warehouse.operator.PutAway

import com.gin.wms.manager.db.data.PutawayTaskData
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import com.bosnet.ngemart.libgen.Common
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.ProductManager
import com.gin.wms.manager.PutawayManager
import com.gin.wms.manager.db.data.PutawayTaskItemData
import com.gin.wms.manager.db.data.enums.PutawayTypeEnum
import com.gin.wms.manager.db.data.enums.TaskStatusEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.operator.TaskSwitcherActivity
import com.gin.wms.warehouse.warehouse_problem.WarehouseProblemActivity
import kotlinx.android.synthetic.main.activity_putaway_task_detail.*
import java.text.MessageFormat

class PutawayTaskDetailActivity : WarehouseActivity() {

    companion object {
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        val SCAN_LOCATION = 1
        val SCAN_LOCATION2 = 2
        val SCAN_PALLET = 3
        val SCAN_PALLET2 = 4
    }

    private var putawayTaskData: PutawayTaskData? = null
    private var productManager: ProductManager? = null
    private var putawayTaskManager: PutawayManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var checkerTaskId: String = ""

    val onStartClickListener = View.OnClickListener { view ->
        try {
            if (putawayTaskData?.hasBeenStart!!)
                doNextProcess()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onReportClickListener = View.OnClickListener { view ->
        try {
            val intentToReport= Intent(this, WarehouseProblemActivity::class.java);
            startActivity(intentToReport);

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }


    val onSearchClickListener = View.OnClickListener { view ->
        try {
            if (edPalletNo.text.toString().isEmpty()) {
                showErrorDialog("Anda belum mengisi pallet no.")
                return@OnClickListener
            }

            getPutawayTaskItemDataByPalletNoThread(edPalletNo.text.toString())
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun setUpUiIfProcessHasStarted() {
        if (putawayTaskData?.hasBeenStart!!) {
            btnStart.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
            btnStart.text = getString(R.string.next)
            lockUi(false)
        }else{
            lockUi(true)
        }
    }

    private fun lockUi(b: Boolean) {
        btnScanPallet.isEnabled = !b
        btnScanPallet2.isEnabled = !b
        btnScanSource.isEnabled = !b
        btnScanSource2.isEnabled = !b
        edPalletNo.isEnabled = !b
        edPalletNo2.isEnabled = !b
        edSource.isEnabled = !b
        edSource2.isEnabled = !b
        btnSearch.isEnabled = !b
    }

    private fun doNextProcess() {
        if (!validateData())
            return

        val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
            ThreadStart(object : ThreadHandler<Boolean?> {
                override fun onPrepare() {
                    startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                }

                override fun onBackground(): Boolean? {
                    if (putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY || putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY_PER_PRODUCT) {
                        putawayTaskManager?.startPutaway(putawayTaskData);
                    } else {
                        putawayTaskManager?.startMovingToStaging(putawayTaskData);
                    }

                    return true
                }

                override fun onError(e: java.lang.Exception?) {
                    dismissProgressDialog()
                    showErrorDialog(e)
                }

                override fun onSuccess(data: Boolean?) {
                    dismissProgressDialog()
                    startActivity(PutawayTaskFinishActivity::class.java)
                }

                override fun onFinish() {

                }
            })
        }

        showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin memulai proses ini?", okListener, null)
    }

    private fun validateData(): Boolean {
        if (!edPalletNo.text.toString().equals(tvPalletNo.text)) {
            showErrorDialog(MessageFormat.format("No palet [{0}] salah.", edPalletNo.text.toString()))
            return false
        }

        if (!edSource.text.toString().equals(tvSource.text)) {
            showErrorDialog(MessageFormat.format("Source location [{0}] salah.", edSource.text.toString()))
            return false
        }

        if (putawayTaskData?.items?.size!! > 1) {
            if (!edPalletNo2.text.toString().equals(tvPalletNo2.text)) {
                showErrorDialog(MessageFormat.format("No palet [{0}] salah.", edPalletNo2.text.toString()))
                return false
            }

            if (!edSource2.text.toString().equals(tvSource2.text)) {
                showErrorDialog(MessageFormat.format("Source location [{0}] salah.", edSource2.text.toString()))
                return false
            }
        }

        return true
    }

    val onScanSourceClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_LOCATION)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onScanSource2ClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_LOCATION2)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onScanPalletClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_PALLET)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onScanPallet2ClickListener = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_PALLET2)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    val onCancelClickListener = View.OnClickListener { view ->
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object: ThreadHandler<Boolean?>{
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        putawayTaskManager?.cancelMovingToStagingAndBackToUnloading()
                        return true
                    }

                    override fun onError(e: java.lang.Exception?) {
                        dismissProgressDialog()
                        showErrorDialog(e)
                    }

                    override fun onSuccess(data: Boolean?) {
                        dismissProgressDialog()
                        val intent = Intent(this@PutawayTaskDetailActivity, TaskSwitcherActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFinish() { }
                })

            }
            showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin membatalkan task ini?", okListener, null)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_task_detail)

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
        setExternalBarcodeActive(true)
    }

    private fun initToolbar() {
        if (putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY) toolbar.setTitle("Putaway")
        else if (putawayTaskManager?.putawayType == PutawayTypeEnum.MOVING_TO_STAGING) toolbar.setTitle("Moving To Staging")
        else if (putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY_PER_PRODUCT) toolbar.setTitle("Putaway Per Product")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun initButton() {
        btnStart.setOnClickListener(onStartClickListener)
        btnScanSource.setOnClickListener(onScanSourceClickListener)
        btnScanSource2.setOnClickListener(onScanSource2ClickListener)
        btnScanPallet.setOnClickListener(onScanPalletClickListener)
        btnScanPallet2.setOnClickListener(onScanPallet2ClickListener)
        btnReport.setOnClickListener(onReportClickListener)


        if (putawayTaskManager?.putawayType == PutawayTypeEnum.MOVING_TO_STAGING) {
            btnCancel.visibility = VISIBLE
            btnCancel.setOnClickListener(onCancelClickListener)
            btnSearch.visibility = VISIBLE
            btnSearch.setOnClickListener(onSearchClickListener)
        } else {
            btnCancel.visibility = GONE
            btnSearch.visibility = GONE
        }
    }

    private fun initData() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!) {
            checkerTaskId = intent.getStringExtra(ARG_CHECKER_TASK_TASK_ID)
        }

        putawayTaskData = putawayTaskManager?.getLocalPutawayTask()
        if (putawayTaskData != null) {
            if (putawayTaskData?.getStatus() == TaskStatusEnum.PROGRESS) {
                startActivity(PutawayTaskFinishActivity::class.java)
                return
            }

            showItemDataUi(putawayTaskData?.items?.get(0)!!)

            if (putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY && putawayTaskData?.items?.size!! > 1)
                showNextItemDataUi()
            else
                visibleNextItem(false)

            setUpUiIfProcessHasStarted()
        } else {
            putawayTaskData = PutawayTaskData()
            val putawayTaskItemData = PutawayTaskItemData()
            putawayTaskData?.items?.add(putawayTaskItemData)
            visibleNextItem(false)
            lockUi(true)
        }
    }

    fun visibleNextItem(b: Boolean) {
        if (b) {
            tvItem2.visibility = VISIBLE
            cvItem2.visibility = VISIBLE
        } else {
            tvItem2.visibility = GONE
            cvItem2.visibility = GONE
        }
    }

    fun showNextItemDataUi() {
        val item = putawayTaskData?.items?.get(1)
        tvSource2.setText(item?.sourceId)
        tvProductName2.text = item?.productName
        tvPalletNo2.setText(item?.palletNo)
        tvQty2.text = item?.qtyCompUomValue
        tvExpiredDate2.text = Common.getShortDateString(item?.expiredDate)
    }

    fun showItemDataUi(item: PutawayTaskItemData) {
        tvSource.setText(item.sourceId)
        tvProductName.text = item.productName
        tvPalletNo.setText(item.palletNo)
        tvQty.text = item.qtyCompUomValue
        tvExpiredDate.text = Common.getShortDateString(item.expiredDate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                val barcodeId = result.contents
                if (requestCode == SCAN_PALLET) {
                    if (putawayTaskData?.items?.size!! > 0) {
                        if (putawayTaskManager?.putawayType == PutawayTypeEnum.MOVING_TO_STAGING) {
                            getPutawayTaskItemDataByPalletNoThread(barcodeId)
                        } else if (putawayTaskManager?.putawayType == PutawayTypeEnum.PUTAWAY) {
                            validatePalletNo(edPalletNo, putawayTaskData?.items?.get(0)?.palletNo!!, barcodeId)
                        }
                    }
                } else if (requestCode == SCAN_PALLET2) {
                    validatePalletNo(edPalletNo, putawayTaskData?.items?.get(1)?.palletNo!!, barcodeId)
                } else if (requestCode == SCAN_LOCATION) {
                    validateSource(edSource, putawayTaskData?.items?.get(0)?.sourceId!!, barcodeId)
                } else if (requestCode == SCAN_LOCATION2) {
                    validateSource(edSource2, putawayTaskData?.items?.get(1)?.sourceId!!, barcodeId)
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    fun validateSource(editText: EditText, sourceId: String, barcodeId: String) {
        if (sourceId != barcodeId) {
            showErrorDialog(MessageFormat.format("Source Location [{0}] salah.", barcodeId))
        } else {
            editText.setText(barcodeId)
        }
    }

    fun validatePalletNo(editText: EditText, palletNo: String, barcodeId: String) {
        if (palletNo != barcodeId) {
            showErrorDialog(MessageFormat.format("No palet [{0}] salah.", barcodeId))
        } else {
            editText.setText(barcodeId)
        }
    }

    fun getPutawayTaskItemDataByPalletNoThread(palletNo: String) {
        ThreadStart(object : ThreadHandler<PutawayTaskItemData?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskItemData? {
                return putawayTaskManager?.getPutawayTaskItemDataByPalletNo(checkerTaskId, palletNo)
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskItemData?) {
                putawayTaskData?.items?.clear()
                if (data == null) {
                    showErrorDialog(MessageFormat.format("No palet [{0}] salah.", palletNo))
                } else {
                    putawayTaskData?.items?.add(data)
                    edPalletNo.setText(palletNo)
                    showItemDataUi(data)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    override fun onBackPressed() {
        showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.warning_must_complete_the_task))
    }

}
