package com.gin.wms.warehouse.operator.Docking

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.gin.wms.manager.DockingTaskManager
import com.gin.wms.manager.db.data.DockingTaskData
import com.gin.wms.manager.db.data.enums.RefDocUriEnum
import com.gin.wms.manager.db.data.enums.TaskStatusEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import kotlinx.android.synthetic.main.activity_docking_task.*
import java.text.MessageFormat

/**
 * Created by manbaul on 4/7/2018.
 */
class DockingTaskActivity : WarehouseActivity() {

    companion object {
        const val ARG_REF_DOC_URI = "ARG_REF_DOC_URI"
    }

    private var dockingTaskManager: DockingTaskManager? = null
    private var onStartListener = View.OnClickListener { view ->
        try {
            if (dockingTaskData?.getStatus() == TaskStatusEnum.NEW) {
                val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                    ThreadStart(object : ThreadHandler<Boolean?>{
                        override fun onPrepare() {
                            startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
                        }

                        override fun onBackground(): Boolean? {
                            dockingTaskManager?.startDockingTask();
                            return true;
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

                showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin memulai unloading?", okListener, null)
            } else {
                DismissActivity()
            }

        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private var dockingTaskData: DockingTaskData? = null
    private var refDocUriEnum: RefDocUriEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_docking_task)

            initObject()
            initComponent()
            initData()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        if (intent?.hasExtra(ARG_REF_DOC_URI)!!)
            refDocUriEnum = RefDocUriEnum.init(intent?.getStringExtra(ARG_REF_DOC_URI))

        dockingTaskManager = DockingTaskManager(this)

    }

    private fun initComponent() {
        initToolbar()
        btnContinue.setOnClickListener (onStartListener)
    }

    private fun initToolbar() {
        if (refDocUriEnum == RefDocUriEnum.RELEASE)
            toolbar.setTitle("Loading Task")
        else
            toolbar.setTitle("Unloading Task")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun initData() {
        ThreadStart(object : ThreadHandler<DockingTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): DockingTaskData? {
                return dockingTaskManager?.getDockingTask(refDocUriEnum)
            }

            override fun onError(e: java.lang.Exception?) {
                btnContinue.visibility = View.GONE
                showErrorDialog(e)
            }

            override fun onSuccess(data: DockingTaskData?) {
                dockingTaskData = data
                btnContinue.visibility = View.VISIBLE

                var docking = "";

                data?.let {
                    it.dockings?.forEach {
                        docking = it.dockingId + ", " + docking
                    }

                    if (!docking.isEmpty())
                        docking = docking.substring(0, docking.length - 2)

                    edtpoliceno.setText(it.policeNo)
                    edtchecker.setText(MessageFormat.format("{0} - {1}", it.checker?.id , it.checker?.name))
                    if (it.getStatus() == TaskStatusEnum.PROGRESS) {
                        btnContinue.text = getString(R.string.btnContinue)
                        btnContinue.background.setColorFilter(ContextCompat.getColor(this@DockingTaskActivity, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
                    } else {
                        btnContinue.text = getString(R.string.btnStart)
                        btnContinue.background.setColorFilter(ContextCompat.getColor(this@DockingTaskActivity, R.color.colorClickable), PorterDuff.Mode.MULTIPLY)
                    }
                }
                edtdocking.setText(docking)

            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })

    }

    override fun onBackPressed() {
        onHandleBackPressed()
    }

    private var isPressedTwice: Boolean = false
    fun onHandleBackPressed() {
        if (isPressedTwice) {
            val homeIntent = Intent(ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(homeIntent)
        }
        isPressedTwice = true
        Toast.makeText(this, getString(R.string.common_double_tap_for_close), Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { isPressedTwice = false }, 2000)
    }

}