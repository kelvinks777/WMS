package com.gin.wms.warehouse.operator.PutAway

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.bosnet.ngemart.libgen.Common
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator
import com.gin.wms.manager.ProductManager
import com.gin.wms.manager.PutawayManager
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.manager.db.data.PutawayTaskData
import com.gin.wms.manager.db.data.enums.PutawayTypeEnum
import com.gin.wms.warehouse.operator.TaskSwitcherActivity
import kotlinx.android.synthetic.main.activity_putaway_task_finish.*
import com.gin.wms.warehouse.warehouse_problem.WarehouseProblemActivity
import java.text.MessageFormat

class PutawayTaskFinishActivity : WarehouseActivity() {

    val SCAN_LOCATION = 1

    private var productManager: ProductManager? = null
    private var putawayManager: PutawayManager? = null
    private var putawayTaskData: PutawayTaskData? = null

    val onFinishClick = View.OnClickListener { view ->
        try {
            if (!validateData())
                return@OnClickListener


            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object: ThreadHandler<Boolean?> {
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {

                        if (putawayManager?.putawayType == PutawayTypeEnum.PUTAWAY || putawayManager?.putawayType == PutawayTypeEnum.PUTAWAY_PER_PRODUCT)
                            putawayManager?.finishPutaway(putawayTaskData)
                        else
                            putawayManager?.finishMovingToStaging(putawayTaskData)

                        return true
                    }

                    override fun onError(e: java.lang.Exception?) {
                        dismissProgressDialog()
                        showErrorDialog(e)
                    }

                    override fun onSuccess(data: Boolean?) {
                        dismissProgressDialog()
                        var intent:Intent? = null

                        intent = Intent(this@PutawayTaskFinishActivity, TaskSwitcherActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFinish() {

                    }
                })
            }

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin menyelesaikan putaway task?", okListener, null)
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private fun validateData(): Boolean {
        if (!edDestination.text.toString().equals(tvDestination.text)) {
            showErrorDialog(MessageFormat.format("Destination location [{0}] salah.", edDestination.text.toString()))
            return false
        }

        return true;
    }

    val onFinishAllClick = View.OnClickListener { view ->
        try {
            if (!validateData())
                return@OnClickListener

            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                ThreadStart(object: ThreadHandler<Boolean?> {
                    override fun onPrepare() {
                        startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                    }

                    override fun onBackground(): Boolean? {
                        putawayManager?.finishMovingToStagingAndBackToUnloading(putawayTaskData)
                        return true
                    }

                    override fun onError(e: java.lang.Exception?) {
                        dismissProgressDialog()
                        showErrorDialog(e)
                    }

                    override fun onSuccess(data: Boolean?) {
                        dismissProgressDialog()
                        val intent = Intent(this@PutawayTaskFinishActivity, TaskSwitcherActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFinish() { }
                })
            }

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin menyelesaikan putaway task?", okListener, null)
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    val onScanDestClick = View.OnClickListener { view ->
        try {
            LaunchBarcodeScanner(SCAN_LOCATION)
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    val onReportClick=View.OnClickListener { view ->
        try {
            val intentToReport= Intent(this, WarehouseProblemActivity::class.java);
            startActivity(intentToReport);
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_task_finish)

            initObject()
            initComponent()
            initData()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        putawayManager = PutawayManager(this)
        productManager = ProductManager(this)
    }

    private fun initComponent() {
        initToolbar()
        initButton()
        setExternalBarcodeActive(true)
    }

    private fun initButton() {
        btnFinish.setOnClickListener(onFinishClick)
        if (putawayManager?.putawayType == PutawayTypeEnum.MOVING_TO_STAGING) {
            btnFinishAll.visibility = VISIBLE
            btnFinishAll.setOnClickListener(onFinishAllClick)
        }
        else
        {
            btnFinishAll.visibility = GONE
        }
        btnScanDestination.setOnClickListener(onScanDestClick)
        btnReport.setOnClickListener(onReportClick)
    }

    private fun initToolbar() {
        if (putawayManager?.putawayType == PutawayTypeEnum.PUTAWAY) toolbar.setTitle("Putaway")
        else if (putawayManager?.putawayType == PutawayTypeEnum.MOVING_TO_STAGING) toolbar.setTitle("Moving To Staging")
        else if (putawayManager?.putawayType == PutawayTypeEnum.PUTAWAY_PER_PRODUCT) toolbar.setTitle("Putaway Per Product")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun initData() {
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                return putawayManager?.findPutawayTask()
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                putawayTaskData = data

                var item = putawayTaskData?.items?.get(0)
                tvSource.text = item?.sourceId
                tvPalletNo.text = item?.palletNo
                var productData = productManager?.getProduct(item?.productId)
                tvProductName.text = productData?.productName
                tvExpiredDate.text = Common.getShortDateString(item?.expiredDate)
                tvQty.text = item?.qtyCompUomValue
                tvDestination.text = putawayTaskData?.destinationId

                if (putawayTaskData?.items?.size!! > 1) {
                    cvItem2.visibility = VISIBLE
                    item = putawayTaskData?.items?.get(1)
                    tvSource2.text = item?.sourceId
                    tvPalletNo2.text = item?.palletNo

                    productData = productManager?.getProduct(item?.productId)
                    tvProductName2.text = productData?.productName
                    tvExpiredDate2.text = Common.getShortDateString(item?.expiredDate)
                    tvQty2.text = item?.qtyCompUomValue

                } else
                    cvItem2.visibility = GONE            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK ) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null && result.contents != null) {
                    val barcodeId = result.contents
                    if (requestCode == SCAN_LOCATION) {
                        if (putawayTaskData?.destinationId != barcodeId) {
                            showErrorDialog(MessageFormat.format("Destination location [{0}] salah.", barcodeId))
                        } else {
                            edDestination.setText(barcodeId)
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
