package com.gin.wms.warehouse

import android.os.Bundle
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.wms.manager.OperatorManager
import com.gin.wms.manager.UserManager
import com.gin.wms.manager.db.data.enums.OperatorTypeEnum
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.checker.CheckerMainActivity
import com.gin.wms.warehouse.operator.TaskSwitcherActivity
import com.gin.wms.warehouse.security.ReceivingVehicleActivity


class SwitcherActivity : WarehouseActivity() {
    private var operatorManager: OperatorManager? = null
    private var userManager: UserManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switcher)

        initObject()
        initData()

    }

    private fun initData() {
        ThreadStart(object : NgemartActivity.ThreadHandler<OperatorTypeEnum?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), NgemartActivity.ProgressType.SPINNER)
            }

            override fun onBackground(): OperatorTypeEnum? {
                return operatorManager?.operatorType
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: OperatorTypeEnum?) {
                try {
                    when (data) {
                        OperatorTypeEnum.CHECKER -> showCheckerUi()
                        OperatorTypeEnum.UNLOADER -> showOperatorUi()
                        OperatorTypeEnum.SECURITY -> showSecurityUI()
                        else -> throw Exception("Tipe Operator tidak ada!")
                    }
                } catch (e:Exception) {
                    showErrorDialog(e)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun initObject() {
        userManager = UserManager(this)
        operatorManager = OperatorManager(this)
    }

    private fun showCheckerUi() {
        startActivity(CheckerMainActivity::class.java)
    }

    private fun showOperatorUi() {
        startActivity(TaskSwitcherActivity::class.java)
    }

    private fun showSecurityUI(){
        startActivity(ReceivingVehicleActivity::class.java)
    }

}
