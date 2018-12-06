package com.gin.wms.warehouse.operator

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.bosnet.ngemart.libgen.Common
import com.gin.wms.manager.*
import com.gin.wms.manager.db.data.*
import com.gin.wms.manager.db.data.enums.*
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.component.LeftMenuModule
import com.gin.wms.warehouse.component.SingleInputDialogFragment
import com.gin.wms.warehouse.login.LoginActivity
import com.gin.wms.warehouse.operator.Banded.ProcessingTaskCheckActivity
import com.gin.wms.warehouse.operator.Counting.StockCountingOrderActivity
import com.gin.wms.warehouse.operator.Docking.DockingTaskActivity
import com.gin.wms.warehouse.operator.Docking.MoveToDockingActivity
import com.gin.wms.warehouse.operator.Docking.MoveToDockingDetailActivity
import com.gin.wms.warehouse.operator.Docking.MoveToDockingInputPalletActivity
import com.gin.wms.warehouse.operator.Order.ManualMutationActivity
import com.gin.wms.warehouse.operator.Picking.PickingTaskActivity
import com.gin.wms.warehouse.operator.PutAway.PutawayBadProductsTaskDetailActivity
import com.gin.wms.warehouse.operator.PutAway.PutawayTaskDetailActivity
import com.gin.wms.warehouse.operator.PutAway.PutawayTaskFinishActivity
import com.gin.wms.warehouse.operator.Moving.MovingStartActivity
import com.gin.wms.warehouse.service.CountdownService
import kotlinx.android.synthetic.main.app_bar_task_switcher_main.*
import kotlinx.android.synthetic.main.content_task_switcher_main.*
import java.text.MessageFormat


class TaskSwitcherActivity : WarehouseActivity(),
        LeftMenuModule.ILeftMenu,
        DialogInterface.OnClickListener,
        SingleInputDialogFragment.InputManualInterface {
    private var tokenLocalManager: TokenLocalManager? = null
    private var operatorManager: OperatorManager? = null
    private var dockingTaskTaskManager: DockingTaskManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var putawayTaskManager: PutawayManager? = null
    private var pickingTaskManager: PickingTaskManager? = null
    private var movingTaskManager: MovingTaskManager? = null
    private var dockingTaskManager: DockingTaskManager? = null
    private var stockLocationManager: StockLocationManager? = null
    private var leftMenuModule: LeftMenuModule? = null
    private var userManager: UserManager? = null
    private var userData: UserData? = null
    private var isPressedTwice: Boolean = false

    private val MOVE_TO_DOCKING_DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET = 333

    //region override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_operator_task_switcher)
            initObject()
            initComponent()
            switchTaskForOperatorThread()
            startService(Intent(this, CountdownService::class.java))
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        ThreadStart<Boolean>(object : ThreadHandler<Boolean> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_close_app_progress_content), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                tokenLocalManager?.SaveNgToken("", "")
                userManager?.SignOut()
                return true
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: Boolean?) {
                dismissProgressDialog()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            override fun onFinish() {}
        })
    }

    override fun onHandleBackPressed() {
        if (isPressedTwice) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(homeIntent)
        }
        isPressedTwice = true
        Toast.makeText(this, getString(R.string.common_double_tap_for_close), Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { isPressedTwice = false }, 2000)
    }

    override fun onMenuItemSelected(itemId: Int): Boolean {
        when (itemId) {
            R.id.nav_logout -> showAskDialog(getString(R.string.common_confirmation_title), getString(R.string.common_close_app_confirmation_content), this, null)
            else -> throw Exception("Menu not found")
        }
        return true
    }

    override fun onBackPressed() {
        leftMenuModule?.closeDrawer()
    }

    override fun onDestroy() {
        stopService(Intent(this, CountdownService::class.java))
        super.onDestroy()
    }

    override fun onFinishCountdownService() {
        try {
            super.onFinishCountdownService()
            switchTaskForOperatorThread()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onTextInput(typeCode: Int, inputText: String?) {
        if(typeCode == MOVE_TO_DOCKING_DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET)
            showConfirmationToStartMovingTaskDialog(inputText)
    }

    //endregion

    //region Init
    private fun initComponent() {
        toolbar.title = "Operator Task"
        setSupportActionBar(toolbar)
        initLeftMenuModule()
        initButtonListener()
    }

    private fun initButtonListener() {
        btnStartTask.setOnClickListener(startTaskListener)
        btnCancelTask.setOnClickListener(cancelTaskListener)
    }

    private var startTaskListener = View.OnClickListener { _ ->
        try {
            val operatorStatusEnum = operatorManager?.operatorStatusFromLocal
            when(operatorStatusEnum) {
                OperatorStatusEnum.UNLOADING -> showDockingUiThread(RefDocUriEnum.RECEIVING)
                OperatorStatusEnum.LOADING -> showDockingUiThread(RefDocUriEnum.RELEASE)
                OperatorStatusEnum.PUT_AWAY -> showPutAwayUiThread()
                OperatorStatusEnum.MOVING_TO_STAGING -> showMovingToStagingUiThread(PutawayTypeEnum.MOVING_TO_STAGING)
                OperatorStatusEnum.PUT_AWAY_BAD_PRODUCT -> showPutAwayBadProductUiThread()
                OperatorStatusEnum.PUT_AWAY_PER_PRODUCT -> showPutAwayPerProductUiThread()
                OperatorStatusEnum.PICKING -> getPickingTaskThread()
                OperatorStatusEnum.MOVING_TO_DOCKING -> showMovingToDockingUi()
                OperatorStatusEnum.REPLENISHMENT -> showReplenishmentUI()
                OperatorStatusEnum.DESTRUCTING -> showDestructionUI()
                OperatorStatusEnum.MUTATION -> showMutationUI()
                OperatorStatusEnum.BANDED ->showBandedUI()
                OperatorStatusEnum.MANUAL_MUTATION_ORDER ->showOrderToManualInternalMutation()
                OperatorStatusEnum.STOCK_COUNTING_ORDER -> showOrderToManualStockCounting()
                else -> {
                    showErrorDialog("Tipe tugas tidak ditemukan!")
                    setNoTaskAvailableToUi()
                }
            }
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private var cancelTaskListener = View.OnClickListener { _ ->
        try {
            val operatorStatusEnum = operatorManager?.operatorStatusFromLocal
            if(operatorStatusEnum == OperatorStatusEnum.MOVING_TO_DOCKING){
                showCancelMoveToDockingDialog()
            }
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    private fun initObject() {
        userManager = UserManager(this)
        userData = userManager?.GetUserDataFromLocal()
        tokenLocalManager = TokenLocalManager(this)
        operatorManager = OperatorManager(this)
        dockingTaskTaskManager = DockingTaskManager(this)
        checkerTaskManager = CheckerTaskManager(this)
        putawayTaskManager = PutawayManager(this)
        pickingTaskManager = PickingTaskManager(this)
        movingTaskManager = MovingTaskManager(this)
        dockingTaskManager = DockingTaskManager(this)
        stockLocationManager = StockLocationManager(this)
    }

    private fun initLeftMenuModule() {
        leftMenuModule = LeftMenuModule(this, toolbar)
        leftMenuModule?.setLeftMenuListener(this)
        leftMenuModule?.setAppVersion(Common.GetVersionInfo(this))
        leftMenuModule?.setProfileInfo(userData)
        setProfileImageThread()
    }
    //endregion

    //region Set Up Ui

    private fun setProfileImageThread() {
        if (!userData?.profileImage.equals("")) {
            leftMenuModule?.setProfileImage(userData?.profileImage)
        } else {
            ThreadStart(object : ThreadHandler<String> {
                override fun onPrepare() {}

                override fun onBackground(): String? {
                    while (userData?.profileImage.equals("")) {
                        Thread.sleep(1000)
                        userData = userManager?.GetUserDataFromLocal()
                    }
                    return userData?.profileImage
                }

                override fun onError(e: Exception) {
                    Toast.makeText(this@TaskSwitcherActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(data: String) {
                    leftMenuModule?.setProfileImage(userData?.profileImage)
                }

                override fun onFinish() {}
            })
        }
    }

    private fun switchTaskForOperatorThread() {
        ThreadStart(object: ThreadHandler<OperatorStatusEnum?>{
            override fun onPrepare() {
            }

            override fun onBackground(): OperatorStatusEnum? {
                return operatorManager?.operatorStatus
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: OperatorStatusEnum?) {
                setUpUi(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun setNoTaskAvailableToUi(){
        noTaskLayout.visibility = View.VISIBLE
        availableTaskLayout.visibility = View.GONE
        btnStartTask.visibility = View.GONE
    }

    private fun setUpAvailableTask(statusType: OperatorStatusEnum?){
        noTaskLayout.visibility = View.VISIBLE
        btnStartTask.visibility = View.VISIBLE
        availableTaskLayout.visibility = View.GONE
        var strToDoTask = "Anda mempunyai tugas "

        when(statusType) {
            OperatorStatusEnum.UNLOADING -> strToDoTask += "bongkar muat"
            OperatorStatusEnum.LOADING -> strToDoTask += "memuat barang"
            OperatorStatusEnum.PUT_AWAY -> strToDoTask += "taruh barang ke rak"
            OperatorStatusEnum.MOVING_TO_STAGING -> strToDoTask += "memindahkan barang ke staging"
            OperatorStatusEnum.PUT_AWAY_BAD_PRODUCT -> strToDoTask += "memindahkan barang rusak"
            OperatorStatusEnum.PUT_AWAY_PER_PRODUCT -> strToDoTask += "memindahkan barang per produk"
            OperatorStatusEnum.PICKING -> strToDoTask += "ambil barang dari rak"
            OperatorStatusEnum.MOVING_TO_DOCKING -> strToDoTask += "memindahkan barang ke docking"
            OperatorStatusEnum.REPLENISHMENT -> strToDoTask += "menambahkan barang ke rak kosong"
            OperatorStatusEnum.DESTRUCTING -> strToDoTask += "memusnahkan barang"
            OperatorStatusEnum.MUTATION -> strToDoTask += "mutasi barang"
            OperatorStatusEnum.BANDED -> strToDoTask += "banded barang"
            OperatorStatusEnum.MANUAL_MUTATION_ORDER -> strToDoTask +="order mutasi manual"
            OperatorStatusEnum.STOCK_COUNTING_ORDER -> strToDoTask +="stockcounting order manual"
            else -> {
                showErrorDialog("Tipe tugas tidak ditemukan!")
                setNoTaskAvailableToUi()
                return
            }
        }

        noTaskLayout.visibility = View.GONE
        availableTaskLayout.visibility = View.VISIBLE
        tvTaskAvailable.text = strToDoTask
        setUpViewForLoadingTask(statusType)
        setUpIcon(statusType)
    }

    private fun setUpViewForLoadingTask(statusType: OperatorStatusEnum?){
        if(statusType == OperatorStatusEnum.UNLOADING || statusType == OperatorStatusEnum.LOADING)
            btnStartTask.text = resources.getString(R.string.btnDetail)

        if(statusType == OperatorStatusEnum.MOVING_TO_DOCKING)
            btnCancelTask.visibility = View.VISIBLE
        else
            btnCancelTask.visibility = View.GONE
    }

    private fun setUpIcon(statusType: OperatorStatusEnum?){
        when(statusType) {
            OperatorStatusEnum.UNLOADING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_loading))
            OperatorStatusEnum.LOADING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unloading))
            OperatorStatusEnum.PUT_AWAY ->imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_putaway_black_48))
            OperatorStatusEnum.MOVING_TO_STAGING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_move_to_staging))
            OperatorStatusEnum.PUT_AWAY_BAD_PRODUCT -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_put_away_bad))
            OperatorStatusEnum.PUT_AWAY_PER_PRODUCT -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_put_away_good))
            OperatorStatusEnum.PICKING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_picking))
            OperatorStatusEnum.MOVING_TO_DOCKING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_move_to_docking))
            OperatorStatusEnum.REPLENISHMENT -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_replenish))
            OperatorStatusEnum.DESTRUCTING -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_put_away_bad))
            OperatorStatusEnum.MUTATION -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_put_away))
            OperatorStatusEnum.BANDED -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkertask_black_48))
            OperatorStatusEnum.MANUAL_MUTATION_ORDER -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_put_away))
            OperatorStatusEnum.STOCK_COUNTING_ORDER -> imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_replenish))

            else -> {
                showErrorDialog("Tipe tugas tidak ditemukan!")
                imgTask.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_task))
                return
            }
        }
    }

    private fun setUpUi(statusType: OperatorStatusEnum?){
        when(statusType) {
            OperatorStatusEnum.NA -> setNoTaskAvailableToUi()
            OperatorStatusEnum.UNLOADING,
            OperatorStatusEnum.LOADING,
            OperatorStatusEnum.PUT_AWAY,
            OperatorStatusEnum.MOVING_TO_STAGING,
            OperatorStatusEnum.PUT_AWAY_BAD_PRODUCT,
            OperatorStatusEnum.PUT_AWAY_PER_PRODUCT,
            OperatorStatusEnum.PICKING,
            OperatorStatusEnum.MOVING_TO_DOCKING,
            OperatorStatusEnum.REPLENISHMENT,
            OperatorStatusEnum.DESTRUCTING,
            OperatorStatusEnum.MUTATION,
            OperatorStatusEnum.BANDED,
            OperatorStatusEnum.MANUAL_MUTATION_ORDER,
            OperatorStatusEnum.STOCK_COUNTING_ORDER
            -> setUpAvailableTask(statusType)
            else -> {
                showErrorDialog("Tipe tugas tidak ditemukan!")
                setNoTaskAvailableToUi()
                return
            }
        }
    }

    //endregion

    //region Move To Staging

    private fun showMovingToStagingUiThread(putawayType: PutawayTypeEnum) {
        ThreadStart(object: ThreadHandler<CheckerTaskData?>{
            var putawayTaskData: PutawayTaskData? = null

            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): CheckerTaskData? {
                putawayTaskManager?.putawayType = putawayType
                val checkerTaskData = checkerTaskManager?.getTaskByUnloader()
                if (checkerTaskData != null)
                    putawayTaskData = putawayTaskManager?.getOnProgressPutawayTaskByRefId(checkerTaskData.refDocId)
                return checkerTaskData
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: CheckerTaskData?) {
                if (data == null)
                    showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.switcher_no_data_to_process))

                if (putawayTaskData != null)
                    startActivity(PutawayTaskFinishActivity::class.java)
                else {
                    val intent = Intent(applicationContext, PutawayTaskDetailActivity::class.java)
                    intent.putExtra(PutawayTaskDetailActivity.ARG_CHECKER_TASK_TASK_ID, data?.refDocId)
                    startActivity(intent)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    //endregion

    //region Docking

    private fun showDockingUiThread(refDocUriEnum: RefDocUriEnum) {
        ThreadStart(object: ThreadHandler<DockingTaskData?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): DockingTaskData? {
                return dockingTaskTaskManager?.getDockingTask(refDocUriEnum)
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: DockingTaskData?) {
                if (data?.getStatus() == TaskStatusEnum.NEW || data?.getStatus() == TaskStatusEnum.PROGRESS) {
                    val intent = Intent(this@TaskSwitcherActivity, DockingTaskActivity::class.java)
                    intent.putExtra(DockingTaskActivity.ARG_REF_DOC_URI, refDocUriEnum.value)
                    startActivity(intent)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    //endregion

    //region Put Away

    private fun showPutAwayPerProductUiThread() {
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                val putawayTaskData = putawayTaskManager?.findPutawayTask()
                putawayTaskManager?.putawayType = putawayTaskData?.getTaskType()
                checkerTaskManager?.findTaskByReceivingDocRef(putawayTaskData?.receivingNo)
                return putawayTaskData
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                if (data == null){
                    showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.switcher_no_data_to_process))
                    return
                }else{
                    showAskStartPutAwayPerProductDialog(data)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }

            private fun showAskStartPutAwayPerProductDialog(data: PutawayTaskData?) {
                val okListener = DialogInterface.OnClickListener { _, _ ->
                    startPutAwayTaskActivityThread(data)
                }

                showAskDialog(getString(
                        R.string.common_confirmation_title),
                        resources.getString(R.string.ask_to_start_put_away_per_product),
                        okListener, null
                )
            }
        })
    }

    private fun showPutAwayBadProductUiThread() {
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                val putawayTaskData = putawayTaskManager?.findPutawayTask()
                putawayTaskManager?.putawayType = putawayTaskData?.getTaskType()
                checkerTaskManager?.findTaskByReceivingDocRef(putawayTaskData?.receivingNo)
                return putawayTaskData
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                if (data == null) {
                    showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.switcher_no_data_to_process))
                    return
                }else{
                    showAskStartPutAwayBadProductDialog(data)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }

            private fun showAskStartPutAwayBadProductDialog(data: PutawayTaskData?) {
                val okListener = DialogInterface.OnClickListener { _, _ ->
                    startPutAwayBadProductActivityThread(data)
                }

                showAskDialog(getString(
                        R.string.common_confirmation_title),
                        resources.getString(R.string.ask_to_start_put_away_bad_product),
                        okListener, null
                )
            }
        })
    }

    private fun showPutAwayUiThread() {
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                val putawayTaskData = putawayTaskManager?.findPutawayTask()
                putawayTaskManager?.putawayType = putawayTaskData?.getTaskType()
                checkerTaskManager?.findTaskByReceivingDocRef(putawayTaskData?.receivingNo)
                return putawayTaskData
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                if (data == null) {
                    showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.switcher_no_data_to_process))
                    return
                }else{
                    showAskStartPutAwayTaskDialog(data)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }

            private fun showAskStartPutAwayTaskDialog(data: PutawayTaskData?) {
                val okListener = DialogInterface.OnClickListener { _, _ ->
                    startPutAwayTaskActivityThread(data)
                }

                showAskDialog(getString(
                        R.string.common_confirmation_title),
                        resources.getString(R.string.ask_to_start_put_away),
                        okListener, null
                )
            }
        })
    }

    private fun startPutAwayBadProductActivityThread(data: PutawayTaskData?){
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                data?.hasBeenStart = true
                putawayTaskManager?.savePutawayTaskData(data)

                return data
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                startActivity(PutawayBadProductsTaskDetailActivity::class.java)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun startPutAwayTaskActivityThread(data: PutawayTaskData?){
        ThreadStart(object: ThreadHandler<PutawayTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PutawayTaskData? {
                data?.hasBeenStart = true
                putawayTaskManager?.savePutawayTaskData(data)

                return data
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PutawayTaskData?) {
                startActivity(PutawayTaskDetailActivity::class.java)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }



    //endregion

    //region Replenishment

    private fun showReplenishmentUI(){
        findAvailableMovingDataThread()
    }

    private fun findAvailableMovingDataThread() {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_replenishData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.replenishData
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                if(data != null)
                    showAskStartReplenishDialog(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showAskStartReplenishDialog(data: MovingTaskData?) {
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startReplenishTaskThread(data)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_replenish),
                okListener, null
        )
    }

    private fun startReplenishTaskThread(data : MovingTaskData?) {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_replenishData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.startMovingData(data)
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                showStartReplenishActivity(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showStartReplenishActivity(data : MovingTaskData?){
        val intent = Intent(this, MovingStartActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(MovingStartActivity.REPLENISH_DATA_CODE, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    //endregion

    //region Picking

    private fun getPickingTaskThread(){
        ThreadStart(object: ThreadHandler<PickingTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PickingTaskData? {
                return pickingTaskManager?.findAvailablePickingTask()
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PickingTaskData?) {
                if (data == null) {
                    showInfoDialog(resources.getString(R.string.common_information_title), resources.getString(R.string.switcher_no_data_to_process))
                    return
                }

                if(data.getStatus() == TaskStatusEnum.NEW)
                    showStartPickingDialog(data)
                else
                    showPickingUi(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showStartPickingDialog(data: PickingTaskData?){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startPickingTaskThread(data)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.info_start_task),
                okListener, null
        )
    }

    private fun startPickingTaskThread(data : PickingTaskData?){
        ThreadStart(object: ThreadHandler<PickingTaskData?>{
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): PickingTaskData? {
                pickingTaskManager?.startPickingTask(data?.pickingTaskId)
                return pickingTaskManager?.findAvailablePickingTask()
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: PickingTaskData?) {
                showPickingUi(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showPickingUi(data : PickingTaskData?){
        val intent = Intent(this, PickingTaskActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(PickingTaskActivity.PICKING_TASK_ID, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    //endregion

    //region Move To Docking

    private fun showMovingToDockingUi(){
        showStartMoveToDockingDialog()
    }

    private fun showMoveToDockingDetail(movingTaskData: MovingTaskData) {
        val intent = Intent(this, MoveToDockingDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(MoveToDockingActivity.MOVING_TASK_STATE, movingTaskData)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun showStartMoveToDockingDialog(){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startActivity(MoveToDockingInputPalletActivity::class.java)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_start_move_to_docking_task),
                okListener, null, null, null)
    }

    private fun showCancelMoveToDockingDialog(){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            cancelMoveToDockingThread()
        }

        showAskDialog(getString(
                R.string.ask_cancel_moving_now_title),
                resources.getString(R.string.ask_cancel_moving_now_body),
                okListener, null
        )
    }

    private fun showConfirmationToStartMovingTaskDialog(palletNo : String?){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            checkAvailableProductOnStagingLocationThread(palletNo)
        }

        val cancelListener = DialogInterface.OnClickListener { _, _ ->
            showInfoDialog(getString(R.string.common_information_title),getString(R.string.info_moving_task_declined))
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                MessageFormat.format(getString(R.string.ask_to_update_pallet_number), palletNo),
                okListener, cancelListener
        )
    }

    private fun cancelMoveToDockingThread(){
        ThreadStart(object : ThreadHandler<Boolean> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_content_cancel_moving_task), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                movingTaskManager?.cancelMovingTask()
                return true
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: Boolean?) {
                reloadSelfActivity()
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun reloadSelfActivity(){
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun checkAvailableProductOnStagingLocationThread(palletNo : String?){
        ThreadStart(object : ThreadHandler<List<StockLocationData>> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): List<StockLocationData> {
                var stockLocationDataList: List<StockLocationData>?
                val dockingTaskData = dockingTaskManager?.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.value)
                val checkerTaskData = checkerTaskManager?.findTaskByReleaseDocRef(dockingTaskData?.docRefId)
                stockLocationDataList = stockLocationManager?.getStockLocationByBinPallet(checkerTaskData?.stagingId, palletNo)

                if(stockLocationDataList == null)
                    stockLocationDataList = listOf()

                return stockLocationDataList
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(dataList: List<StockLocationData>) {
                if (dataList.isEmpty()) {
                    val strInfo = resources.getString(
                            R.string.error_product_not_found_by_selected_pallet) + "'" + palletNo?.toUpperCase() + "'" + "\n\n" + getString(R.string.info_moving_task_declined)
                    showInfoDialog(getString(R.string.common_information_title), strInfo)
                } else {
                    justStartMovingToDockingTaskThread(palletNo)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun justStartMovingToDockingTaskThread(palletNo : String?) {
        ThreadStart(object : ThreadHandler<MovingTaskData> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_content_creating_moving_task), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                var movingTaskData = movingTaskManager?.movingTaskFromServerByOperatorId

                if (movingTaskData == null)
                    movingTaskData = createMovingTaskData()

                return movingTaskData
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                if (data != null)
                    showMoveToDockingDetail(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }

            fun createMovingTaskData(): MovingTaskData? {
                val movingTaskData : MovingTaskData?
                val dockingTaskData = dockingTaskManager?.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.value)
                val checkerTaskData = checkerTaskManager?.findTaskByReleaseDocRef(dockingTaskData?.docRefId)
                val stagingId = checkerTaskData?.stagingId
                val dockingNo = dockingTaskData?.dockings?.get(0)?.dockingId
                val operatorId = dockingTaskData?.checker?.id
                val releaseOrderId = dockingTaskData?.docRefId
                movingTaskData = movingTaskManager?.createMovingTask(stagingId, dockingNo, palletNo, operatorId, releaseOrderId)
                movingTaskManager?.saveMovingTaskData(movingTaskData)

                return movingTaskManager?.startMovingData(movingTaskData)
            }
        })
    }

    //endregion

    //region Destruction
    private fun showDestructionUI(){
        findAvailableMovingDataThreads()
    }

    private fun findAvailableMovingDataThreads() {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_destructionData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.destructionData
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                if(data != null)
                    showAskStartDestructionDialog(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showAskStartDestructionDialog(data: MovingTaskData?) {
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startDestructionTaskThread(data)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_destruction),
                okListener, null
        )
    }

    private fun startDestructionTaskThread(data : MovingTaskData?) {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_destructionData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.startMovingData(data)
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                showStartDestructionActivity(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showStartDestructionActivity(data : MovingTaskData?){
        val intent = Intent(this, MovingStartActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(MovingStartActivity.DESTRUCTION_DATA_CODE, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    //endregion

    //region Mutation
    private fun showMutationUI(){
        findAvailableMovingDataForMutationThread()
    }

    private fun findAvailableMovingDataForMutationThread() {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_mutationData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.mutationData
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                if(data != null) {
                    showAskStartMutationDialog(data)
                }
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showAskStartMutationDialog(data: MovingTaskData?) {
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startMutationTaskThread(data)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_mutation),
                okListener, null
        )
    }

    private fun startMutationTaskThread(data : MovingTaskData?) {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_mutationData), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return movingTaskManager?.startMovingData(data)
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                showStartMutationActivity(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }


    private fun showStartMutationActivity(data : MovingTaskData?){
        val intent = Intent(this, MovingStartActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(MovingStartActivity.MUTATION_DATA_CODE, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    //endregion

    //region Banded
    private fun showBandedUI(){
        findAvailableBandedDataThreads()
    }

    private fun findAvailableBandedDataThreads() {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_banded), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return null//movingTaskManager?.bandedData
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                //if(data != null)
                    showAskStartBandedDialog(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showAskStartBandedDialog(data: MovingTaskData?) {
        val okListener = DialogInterface.OnClickListener { _, _ ->
            startBandedTaskThread(data)
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_banded),
                okListener, null
        )
    }

    private fun startBandedTaskThread(data : MovingTaskData?) {
        ThreadStart(object : ThreadHandler<MovingTaskData?> {
            @Throws(Exception::class)
            override fun onPrepare() {
                startProgressDialog(getString(R.string.progress_get_banded), ProgressType.SPINNER)
            }

            @Throws(Exception::class)
            override fun onBackground(): MovingTaskData? {
                return null //movingTaskManager?.startMovingData(data)
            }

            override fun onError(e: Exception) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: MovingTaskData?) {
                showStartBandedActivity(data)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun showStartBandedActivity(data : MovingTaskData?){
        val intent = Intent(this, ProcessingTaskCheckActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(ProcessingTaskCheckActivity.BANDED_DATA_CODE, data)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    //endregion

    //region  Manual Mutation Order

    private fun showOrderToManualInternalMutation(){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            createOrderDataForManualMutation()
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_mutation_manual),
                okListener, null
        )
    }

    private fun createOrderDataForManualMutation(){
        val intent = Intent(this, ManualMutationActivity::class.java)
        startActivity(intent)
    }

    //endregion

    //region Stock Counting Order

    private fun showOrderToManualStockCounting(){
        val okListener = DialogInterface.OnClickListener { _, _ ->
            createOrderDataForManualStockCounting()
        }

        showAskDialog(getString(
                R.string.common_confirmation_title),
                resources.getString(R.string.ask_to_start_stock_counting_order_manual),
                okListener, null
        )
    }

    private fun createOrderDataForManualStockCounting(){
        val intent = Intent(this, StockCountingOrderActivity::class.java)
        startActivity(intent)
    }
    //endregion

}
