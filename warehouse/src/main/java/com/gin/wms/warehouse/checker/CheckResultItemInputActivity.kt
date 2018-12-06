package com.gin.wms.warehouse.checker

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bosnet.ngemart.libgen.Common
import com.bosnet.ngemart.libgen.DateUtil
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.CheckerTaskItemResultData
import com.gin.wms.manager.db.data.enums.RefDocUriEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.component.CompUomInputDialogFragment
import kotlinx.android.synthetic.main.activity_check_result_item_input.*
import org.joda.time.DateTime
import java.text.MessageFormat
import java.util.*

class CheckResultItemInputActivity : WarehouseActivity(){

    companion object {
        const val REQUEST_CODE = 100
        const val ARG_MODE_EDIT = "ARG_MODE_EDIT"
        const val ARG_MODE_ADD = "ARG_MODE_ADD"
        const val ARG_INPUT_MODE = "ARG_INPUT_MODE"
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_PRODUCT_ID = "ARG_CHECKER_TASK_PRODUCT_ID"
        const val ARG_CHECKER_TASK_CLIENT_LOCATION_ID = "ARG_CHECKER_TASK_CLIENT_LOCATION_ID"
        const val ARG_CHECKER_TASK_PALLET_NO = "ARG_CHECKER_TASK_PALLET_NO"
        const val ARG_CHECKER_TASK_REF_URI = "ARG_CHECKER_TASK_REF_URI"
    }

    private var datePickerDialog: DatePickerDialog? = null
    private var selectedDate: Date? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var checkerTaskItemData: CheckerTaskItemData? = null
    private var taskId: String? = ""
    private var productId: String? = ""
    private var clientLocationId: String? = ""
    private var palletNo: String? = ""
    private var mode:String? = ""

    private var refDocUri: RefDocUriEnum? = null

    private val onChooseDateClickListener = View.OnClickListener { _ ->
        try {
            datePickerDialog?.setTitle("")
            datePickerDialog?.setCancelable(false)
            datePickerDialog?.getDatePicker()?.setMinDate(DateUtil.GetMinDate())
            datePickerDialog?.show()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    //region Override Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_check_result_item_input)

            initObject()
            initComponent()
            initData()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    val barcodeId = result.contents
                    edPalletNo.setText(barcodeId)
                    refDocUri?.let {
                        if (it.equals(RefDocUriEnum.RELEASE)) {
                            searchPalletThread(barcodeId)
                        }
                    }
                }
            }
        } catch (e: Exception) {
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

    //region Functions

    //region Init Functions

    private fun initObject() {
        checkerTaskManager = CheckerTaskManager(this)
        getIntentData()
    }

    private fun initComponent() {
        initToolbar()
        initDatePicker()
        setExternalBarcodeActive(true)
        btnScanPallet.setOnClickListener(onScanPalletClick)
        btnSearchPallet.setOnClickListener(onSearchPalletClick)
        btnSave.setOnClickListener(onSaveClick)
    }

    private fun initDatePicker() {
        btnChooseDate.setOnClickListener(onChooseDateClickListener)

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val dateTime = DateTime(year, monthOfYear + 1, dayOfMonth,0,0)
            selectedDate = dateTime.toDate()
            tvExpiredDate.text = Common.getShortDateString(selectedDate)
        }

        datePickerDialog = DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

    }

    private fun initToolbar() {
        if (mode!! == ARG_MODE_ADD)
            toolbar.setTitle("Add Checker Task Result")
        else
            toolbar.setTitle("Edit Checker Task Result")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        checkerTaskItemData = checkerTaskManager?.getLocalCheckerTaskItem(taskId, productId, clientLocationId)

        compUomInterpreter.setCompUomData(this.checkerTaskItemData?.compUom)
        compUomInterpreter.palletConversion = checkerTaskItemData?.palletConversionValue!!

        clearUi()
        if (mode!! == ARG_MODE_EDIT)
            showDataToUi(checkerTaskItemData?.results?.firstOrNull { checkerTaskItemResultData -> checkerTaskItemResultData.palletNo == palletNo })
    }
    //endregion

    //region UI Functions

    private fun clearUi() {
        edPalletNo.setText("")
        compUomInterpreter.qty = checkerTaskItemData?.palletConversionValue!!
        HideSoftInputKeyboard()
	refDocUri?.let {
            if (it == RefDocUriEnum.RECEIVING) {
                btnSearchPallet.visibility = View.GONE
                btnChooseDate.visibility = View.VISIBLE
                rdoBad.visibility = View.VISIBLE
            } else if (it == RefDocUriEnum.RELEASE) {
                btnSearchPallet.visibility = View.VISIBLE
                btnChooseDate.visibility = View.GONE
                rdoBad.visibility = View.GONE
            }
        }    }

    private fun showDataToUi(data: CheckerTaskItemResultData?) {
        data?.let {
            edPalletNo.setText(it.palletNo)
            edPalletNo.isEnabled = false
            if (it.badQty > 0 ) {
                rdoBad.isChecked = true
                compUomInterpreter.qty = it.badQty
            } else if (it.goodQty > 0) {
                rdoGood.isChecked = true
                compUomInterpreter.qty = it.goodQty
            }
            tvExpiredDate.text = Common.getShortDateString(it.expiredDate)
            btnSave.text = getString(R.string.btnSave)
            selectedDate = it.expiredDate
            btnScanPallet.isEnabled= false
        }
    }
    //endregion

    //region Other Functions

    private val onScanPalletClick = View.OnClickListener { _ ->
        try {
            LaunchBarcodeScanner()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private val onSearchPalletClick = View.OnClickListener { _ ->
        try {
            if (edPalletNo.text.isEmpty()) {
                showErrorDialog("Pallet no tidak boleh kosong")
                return@OnClickListener
            }

            searchPalletThread(edPalletNo.text.toString())
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private val onSaveClick = View.OnClickListener { _ ->
        try {
            if (edPalletNo.text.isEmpty()) {
                showErrorDialog("Pallet no tidak boleh kosong")
                return@OnClickListener
            }

            if (tvExpiredDate.text.isEmpty()) {
                showErrorDialog("Expired date tidak boleh kosong")
                return@OnClickListener
            }

            if (compUomInterpreter.qty == 0.0) {
                showErrorDialog("Anda belum mengisi Qty")
                return@OnClickListener
            }

            if (compUomInterpreter.qty >  compUomInterpreter.palletConversion) {
                val okListener = DialogInterface.OnClickListener { _, _ ->
                    saveCheckResultThreadConfirmation()
                }
                showAskDialog(getString(R.string.common_confirmation_title),"Jumlah barang lebih dari nilai per palet.\nApakah anda ingin melanjutkan?", okListener, null)
                return@OnClickListener
            } else {
                saveCheckResultThreadConfirmation()
            }
            
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun saveCheckResultThreadConfirmation() {
        val okListener : DialogInterface.OnClickListener?
        if (mode!! == ARG_MODE_ADD) {
            okListener = DialogInterface.OnClickListener { _, _ ->
                checkResultAddThread()
            }
        } else {
            okListener = DialogInterface.OnClickListener { _, _ ->
                checkResultOtherOptionThread()
            }
        }

        showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin menyimpan data pallet ini?", okListener, null)
    }

    private fun searchPalletThread(palletNo: String) {
        ThreadStart(object:ThreadHandler<CheckerTaskItemResultData?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): CheckerTaskItemResultData? {
                return checkerTaskManager?.getReleaseCheckResultFromStockLocation(taskId, palletNo, productId)
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: CheckerTaskItemResultData?) {
                if (data != null) {
                    edPalletNo.setText(data.palletNo)
                    tvExpiredDate.text = Common.getShortDateString(data.expiredDate)
                    selectedDate = data.expiredDate
                    compUomInterpreter.qty = data.goodQty
                } else {
                    val message = getString(R.string.wrong_pallet)
                    showErrorDialog(MessageFormat.format(message, edPalletNo.text.toString()))
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }

        })
    }

    private fun getIntentData() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!)
            taskId = intent?.getStringExtra(ARG_CHECKER_TASK_TASK_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_PRODUCT_ID)!!)
            productId = intent?.getStringExtra(ARG_CHECKER_TASK_PRODUCT_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)!!)
            clientLocationId = intent?.getStringExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)

        if (intent?.hasExtra(ARG_CHECKER_TASK_PALLET_NO)!!)
            palletNo = intent?.getStringExtra(ARG_CHECKER_TASK_PALLET_NO)

        if (intent?.hasExtra(ARG_INPUT_MODE)!!)
            mode = intent?.getStringExtra(ARG_INPUT_MODE)

        if (intent?.hasExtra(ARG_CHECKER_TASK_REF_URI)!!)
            refDocUri = RefDocUriEnum.init (intent?.getStringExtra(ARG_CHECKER_TASK_REF_URI)!!)

    }

    //endregion

    //endregion

    //region Threads

    private fun checkResultAddThread(){
        ThreadStart(object: ThreadHandler<Boolean>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean {
                var goodQty = 0.0
                var badQty = 0.0

                val rdoId = rgQty.checkedRadioButtonId

                if (rdoId == rdoGood.id)
                    goodQty = compUomInterpreter.qty
                else
                    badQty = compUomInterpreter.qty

                return checkerTaskManager?.createResult(taskId, productId, clientLocationId, edPalletNo.text.toString(), selectedDate, goodQty, badQty)!!
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {
                if (data!!)
                    clearUi()
                else
                    showErrorDialog("No pallet sudah ada")
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun checkResultOtherOptionThread(){
        ThreadStart(object: ThreadHandler<Boolean>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean {
                var goodQty = 0.0
                var badQty = 0.0

                val rdoId = rgQty.checkedRadioButtonId

                if (rdoId == rdoGood.id)
                    goodQty = compUomInterpreter.qty
                else
                    badQty = compUomInterpreter.qty

                return checkerTaskManager?.updateResult(taskId, productId, clientLocationId, edPalletNo.text.toString(), selectedDate, goodQty, badQty)!!
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {
                if (data!!)
                    DismissActivity()
                else
                    showErrorDialog("No pallet tidak ada")
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun updateFSidThread(){
        ThreadStart2(object : NgemartActivity.ThreadHandler2<Boolean>(this) {
            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                checkerTaskManager?.updateFSid()
                return null
            }
        })
    }

    //endregion
}
