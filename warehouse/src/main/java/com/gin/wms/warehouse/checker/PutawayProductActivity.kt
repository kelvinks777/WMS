package com.gin.wms.warehouse.checker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bosnet.ngemart.libgen.Common
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.CheckerTaskManager
import com.gin.wms.manager.PutawayBookingManager
import com.gin.wms.manager.db.data.PutawayBookingOperatorData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_putaway_product.*
import java.text.MessageFormat
import java.text.NumberFormat

class PutawayProductActivity : WarehouseActivity(), NgemartCardView.CardListener<PutawayBookingOperatorData> {

    companion object {
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val ARG_CHECKER_TASK_POLICE_NO = "ARG_CHECKER_TASK_POLICE_NO"
        const val ARG_CHECKER_TASK_PRODUCT_ID = "ARG_CHECKER_TASK_PRODUCT_ID"
        const val ARG_CHECKER_TASK_RECEIVING_NO = "ARG_CHECKER_TASK_RECEIVING_NO"
        const val ARG_CHECKER_TASK_CLIENT_LOCATION_ID = "ARG_CHECKER_TASK_CLIENT_LOCATION_ID"
        const val ARG_CHECKER_TASK_CLIENT_ID = "ARG_CHECKER_TASK_CLIENT_ID"
    }

    private var listOfData: MutableList<PutawayBookingOperatorData>? = null
    private var adapter: NgemartRecyclerViewAdapter<PutawayBookingOperatorData>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var docNo: String? = null
    private var policeNo: String? = null
    private var productId: String? = null
    private var receivingNo: String? = null
    private var clientId: String? = null

    private var checkerTaskManager: CheckerTaskManager? = null
    private var putawayBookingManager: PutawayBookingManager? = null

    private var onClickListener = View.OnClickListener { view ->
        try {
            when (view.id) {
                R.id.btnAddOperator -> {

                    if (edPalletTotal.text.toString().isEmpty()) {
                        showErrorDialog("Anda belum mengisi jumlah pallet")
                        return@OnClickListener
                    }

                    val intent = Intent(this, TaskActivity::class.java)
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_POLICE_NO, policeNo)
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_PRODUCT_ID, productId)
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_DOC_NO, docNo )
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_RECEIVING_NO, receivingNo)
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_CLIENT_ID, clientId)
                    intent.putExtra(TaskActivity.ARG_CHECKER_TASK_PALLET_QTY, NumberFormat.getIntegerInstance().parse(edPalletTotal.text.toString()).toInt())
                    startActivity(intent)
                }

                R.id.btnLanjut -> {
                    doPutawayProduct()
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun doPutawayProduct() {

        if (listOfData?.size == 0)
            return

        val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
            ThreadStart(object: ThreadHandler<Boolean?>{
                override fun onPrepare() {
                    startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
                }

                override fun onBackground(): Boolean? {
                    val putawayBookingData = putawayBookingManager?.getLocalPutawayBooking(policeNo, docNo, productId)
                    putawayBookingData?.palletQty = Common.GetNumberSignedWithoutFractionFormat().parse(edPalletTotal.text.toString()).toInt()
                    putawayBookingManager?.putawayOperatorBookingMultipleOperator(putawayBookingData)
                    return null
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

        if (edPalletTotal.text.length == 0) {
            showErrorDialog("Anda belum mengisi jumlah palet.")
            return
        }

        val content = MessageFormat.format("Apakah anda ingin melakukan putaway {0} pallet dengan {1} operator ?"
                , edPalletTotal.text.toString(), listOfData?.size)

        showAskDialog(getString(R.string.common_confirmation_title), content, okListener, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_putaway_product)

            initObject()
            initComponent()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onStart() {
        try {
            super.onStart()
            refreshData()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!) {
            docNo = intent?.getStringExtra(ARG_CHECKER_TASK_TASK_ID)
        }
        if (intent?.hasExtra(ARG_CHECKER_TASK_POLICE_NO)!!) {
            policeNo = intent?.getStringExtra(ARG_CHECKER_TASK_POLICE_NO)
        }
        if (intent?.hasExtra(ARG_CHECKER_TASK_PRODUCT_ID)!!) {
            productId = intent?.getStringExtra(ARG_CHECKER_TASK_PRODUCT_ID)
        }
        if (intent?.hasExtra(ARG_CHECKER_TASK_RECEIVING_NO)!!) {
            receivingNo = intent?.getStringExtra(ARG_CHECKER_TASK_RECEIVING_NO)
        }
        if (intent?.hasExtra(ARG_CHECKER_TASK_CLIENT_ID)!!) {
            clientId = intent?.getStringExtra(ARG_CHECKER_TASK_CLIENT_ID)
        }

//        if (intent?.hasExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)!!) {
//            clientLocationId = intent?.getStringExtra(ARG_CHECKER_TASK_CLIENT_LOCATION_ID)
//        }

        checkerTaskManager = CheckerTaskManager(this)
        putawayBookingManager = PutawayBookingManager(this)
    }

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        btnAddOperator.setOnClickListener(onClickListener)
        btnLanjut.setOnClickListener(onClickListener)
    }

    private fun initToolbar() {
        toolbar.setTitle("Putaway Product")
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

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_putaway_product, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    override fun onCardClick(position: Int, view: View?, data: PutawayBookingOperatorData?) {
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                listOfData?.removeAll { operatorData -> operatorData.id.equals(data?.id) }
                adapter?.notifyDataSetChanged()
            }

            showAskDialog(getString(R.string.common_confirmation_title),"Apakah anda ingin menghapus operator?", okListener, null)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun refreshData() {
        val putawayBookingData = putawayBookingManager?.getLocalPutawayBooking(policeNo, docNo, productId);
        listOfData?.clear()
        listOfData?.addAll(putawayBookingData?.operators.orEmpty())
        adapter?.notifyDataSetChanged()
    }
}
