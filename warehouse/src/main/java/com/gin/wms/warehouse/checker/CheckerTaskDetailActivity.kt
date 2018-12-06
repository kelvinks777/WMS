package com.gin.wms.warehouse.checker

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.*
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.manager.db.data.CheckerTaskItemData
import com.gin.wms.manager.db.data.HigherPriorityTaskData
import com.gin.wms.manager.db.data.CheckerTaskOperatorData
import com.gin.wms.manager.db.data.enums.RefDocUriEnum
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.login.LoginActivity
import com.gin.wms.warehouse.service.NotificationTool
import kotlinx.android.synthetic.main.activity_checker_task_detail.*
import java.text.MessageFormat

class CheckerTaskDetailActivity : WarehouseActivity()
        , NgemartCardView.CardListener<CheckerTaskItemData> {

    companion object {
        const val ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID"
        const val REQ_CODE_RESULT = 101
    }

    enum class SortEnum(value: String) {
        SHOW_ALL("SHOW_ALL"),
        SHOW_CHECKED("SHOW_CHECKED"),
        SHOW_UNCHECKED("SHOW_UNCHECKED");

        val value: String = value
    }

    private var dockingTaskManager: DockingTaskManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var operatorManager: OperatorManager? = null
    private var userManager: UserManager? = null
    private var listOfData: MutableList<CheckerTaskItemData>? = null
    private var adapter: NgemartRecyclerViewAdapter<CheckerTaskItemData>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var taskId: String? = null
    private var higherPriorityTaskManager: HigherPriorityTaskManager? = null
    private var putawayManager: PutawayManager? = null
    private var checkerTaskData: CheckerTaskData? = null
    private var lastSort: String? = null

    //region Override Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_checker_task_detail)

            initObject()
            initComponent()
            initData()
            initToolbarTitle()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            val inflater = menuInflater
            checkerTaskData?.let {
                if (it.refDocUri.equals(RefDocUriEnum.RECEIVING.value))
                    inflater.inflate(R.menu.menu_checker_task_detail_receiving, menu)
                 else
                    inflater.inflate(R.menu.menu_checker_task_detail_release, menu)
            }
            return true
        } catch (e: Exception) {
            showErrorDialog(e)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        try {
            when (item?.itemId) {
                R.id.actUpdateFSidDetail -> {
                    updateFSidThread()
                }

                R.id.actMovingToDocking -> {
                    if (checkerTaskData?.hasBeenStart!!)
                        chooseOperatorForMovingToDocking()
                    else
                        showErrorDialog("Anda tidak dapat memilih moving to docking karena checker task ini belum dimulai.")

                    return true
                }

                R.id.actMovingToStaging -> {
                    if (checkerTaskData?.hasBeenStart!!)
                        startMovingToStaging()
                    else
                        showErrorDialog("Anda tidak dapat memilih moving to staging karena checker task ini belum dimulai.")

                    return true
                }
                R.id.actReleaseOperator -> {
                    releaseOperator()
                    return true
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        try {
            super.onResume()
            if (userManager?.GetProvider() != "")
                lastSort = userManager?.GetProvider()

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun chooseOperatorForMovingToDocking() {
        OperatorActivity.startActivityForResult(this, null, checkerTaskData?.policeNo, taskId,  OperatorActivity.REQ_CODE_MOVING_TO_DOCKING)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == REQ_CODE_RESULT) {
                listOfData?.clear()
                listOfData?.addAll(checkerTaskManager?.getLocalCheckerTaskItems(taskId).orEmpty())
                showSortedItemList()
            } else if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    if (it.hasExtra(OperatorActivity.ARG_CHOOSED_OPERATORS)) {
                        val operators = gsonMapper.readList(it.getStringExtra(OperatorActivity.ARG_CHOOSED_OPERATORS), Array<OperatorActivity.ChoosedTaskOperator>::class.java)
                        if (requestCode == OperatorActivity.REQ_CODE_CHOOSE_OPERATOR)
                            chooseOperators(operators)
                         else if (requestCode == OperatorActivity.REQ_CODE_MOVING_TO_DOCKING)
                            startMovingToDocking(operators.mapNotNull { choosedTaskOperator -> choosedTaskOperator.id })
                    }
                }
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onFinishCountdownService() {
        try {
            super.onFinishCountdownService()
            getNewOrderMonitoringWithHigherPriorityThread()
            getCountOfNotStartedTasksThread()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCardClick(position: Int, view: View?, data: CheckerTaskItemData?) {
        try {
            if (checkerTaskData?.hasBeenStart!!) {
                if (view?.id == R.id.btnPutaway)
                    doPutAway(data)
                else
                    doCheckResult(data)
            }
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    //endregion

    //region Functions

    private fun chooseOperators(operators: List<OperatorActivity.ChoosedTaskOperator>?) {
        try {
            if (operators?.size!! > 0)
                chooseOperatorThread(operators)

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private var onStartOrFinishClick = View.OnClickListener { view ->
        try {
            val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
                startFinishTaskThread()
            }

            var message = "Apakah anda ingin {0} task ini?"
            if (checkerTaskData?.hasBeenStart!!)
                message = MessageFormat.format(message, "mengakhiri")
            else
                message = MessageFormat.format(message, "memulai")

            showAskDialog(getString(R.string.common_confirmation_title), message, okListener, null)

        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun releaseOperator() {
        OperatorActivity.startActivityForResult(this, null, checkerTaskData?.policeNo, taskId)
    }

    private fun startMovingToDocking(operators: List<String>) {
        val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
            startMovingToDockingThread(operators)
        }

        showAskDialog(getString(R.string.common_confirmation_title), MessageFormat.format("Apakah anda ingin melakukan proses moving to docking dengan {0} operator?", operators.size), okListener, null)
    }

    private fun startMovingToStaging() {
        val okListener = DialogInterface.OnClickListener { dialogInterface, i ->
            startMovingToStagingThread()
        }

        showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin melakukan proses moving to staging?", okListener, null)
    }

    private fun resetButtonText() {
        if (checkerTaskData?.hasBeenStart!!) {
            btnStartOrFinish.text = getString(R.string.btnFinish)
            btnStartOrFinish.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
        } else {
            btnStartOrFinish.text = getString(R.string.btnStart)
            btnStartOrFinish.background.setColorFilter(ContextCompat.getColor(this, R.color.colorClickable), PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun doCheckResult(checkerTaskItemData: CheckerTaskItemData?) {
        val intent = Intent(this, CheckResultInputActivity::class.java)
        intent.putExtra(CheckResultInputActivity.ARG_CHECKER_TASK_TASK_ID, checkerTaskItemData?.taskId)
        intent.putExtra(CheckResultInputActivity.ARG_CHECKER_TASK_PRODUCT_ID, checkerTaskItemData?.productId)
        intent.putExtra(CheckResultInputActivity.ARG_CHECKER_TASK_CLIENT_LOCATION_ID, checkerTaskItemData?.clientLocationId)
        intent.putExtra(CheckResultInputActivity.ARG_CHECKER_TASK_REF_DOC_URI, checkerTaskData?.refDocUri)
        startActivityForResult(intent, REQ_CODE_RESULT)
    }

    private fun doPutAway(checkerTaskItemData: CheckerTaskItemData?) {
        val intent = Intent(this, PutawayProductActivity::class.java)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_TASK_ID, checkerTaskItemData?.taskId)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_POLICE_NO, checkerTaskItemData?.policeNo)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_PRODUCT_ID, checkerTaskItemData?.productId)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_CLIENT_LOCATION_ID, checkerTaskItemData?.clientLocationId)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_CLIENT_ID, checkerTaskItemData?.clientId)
        intent.putExtra(PutawayProductActivity.ARG_CHECKER_TASK_RECEIVING_NO, checkerTaskData?.refDocId)
        startActivity(intent)
    }

    fun showDialogForNewHighPriorityOrder(listOfHigherPriorityTask: List<HigherPriorityTaskData>?) {
        val showInfoListener = DialogInterface.OnClickListener { dialogInterface, i ->
            startActivity(HigherPriorityTaskActivity::class.java)
        }

        val rejectListener = DialogInterface.OnClickListener { dialogInterface, i ->
            rejectOrderMonitoringThread(listOfHigherPriorityTask)
        }

        showAskDialog(getString(R.string.common_confirmation_title)
                , MessageFormat.format(getString(R.string.content_new_high_priority_task), listOfHigherPriorityTask?.size)
                , showInfoListener
                , rejectListener)
    }

    //endregion

    //region Init Functions

    private fun initComponent() {
        initToolbar()
        initRecyclerView()
        initButton()
        initRadioButtonGroup()
    }

    private fun initButton() {
        btnStartOrFinish.setOnClickListener(onStartOrFinishClick)
    }

    private fun initRadioButtonGroup() {
        rbAll.setOnClickListener(onRbAllClicked)
        rbUnComplete.setOnClickListener(onRbUncheckedClicked)
        rbComplete.setOnClickListener(onRbcheckedClicked)
    }

    private var onRbAllClicked = View.OnClickListener { view ->
        try {
            lastSort = SortEnum.SHOW_ALL.value
            showSortedItemList()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private var onRbUncheckedClicked = View.OnClickListener { view ->
        try {
            lastSort = SortEnum.SHOW_UNCHECKED.value
            showSortedItemList()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private var onRbcheckedClicked = View.OnClickListener { view ->
        try {
            lastSort = SortEnum.SHOW_CHECKED.value
            showSortedItemList()
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initToolbarTitle() {
        if (checkerTaskData?.refDocUri.equals(RefDocUriEnum.RELEASE.value))
            tvToolbarTitle.text = resources.getString(R.string.checker_title_release)
        else if (checkerTaskData?.refDocUri.equals(RefDocUriEnum.RECEIVING.value))
            tvToolbarTitle.text = resources.getString(R.string.checker_title_receive)
    }

    private fun initRecyclerView() {
        val numColumn = 1

        listOfData = mutableListOf()
        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_checker_task_detail, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    private fun initObject() {
        if (intent?.hasExtra(ARG_CHECKER_TASK_TASK_ID)!!)
            taskId = intent?.getStringExtra(ARG_CHECKER_TASK_TASK_ID)

        operatorManager = OperatorManager(this)
        checkerTaskManager = CheckerTaskManager(this)
        higherPriorityTaskManager = HigherPriorityTaskManager(this)
        putawayManager = PutawayManager(this)
        dockingTaskManager = DockingTaskManager(this)
        userManager = UserManager(this)
    }

    private fun initData() {
        checkerTaskData = checkerTaskManager?.getLocalCheckerTaskData(taskId);
        tvPoliceNo.text = checkerTaskData?.policeNo
        tvDocNo.text = taskId
        tvDocking.text = checkerTaskData?.dockingIds
        tvStaging.text = checkerTaskData?.stagingId

        resetButtonText()

        listOfData?.clear()
        listOfData?.addAll(checkerTaskData?.lstProduct.orEmpty())

        setUpItemSortingLayout()
        setUpRadioGroupView()
        showSortedItemList()
    }

    private fun setUpItemSortingLayout(){
        if(checkerTaskData?.hasBeenStart!!)
            itemSortingLayout.visibility = View.VISIBLE
        else
            itemSortingLayout.visibility = View.GONE
    }

    private fun setUpRadioGroupView() {
        userManager?.SaveProvider(SortEnum.SHOW_ALL.value)
        lastSort = userManager?.GetProvider()
        uncheckAllRadioButton()

        when (lastSort) {
            SortEnum.SHOW_CHECKED.value -> rbComplete.isChecked = true
            SortEnum.SHOW_UNCHECKED.value -> rbUnComplete.isChecked = true
            else -> rbAll.isChecked = true
        }
    }

    private fun uncheckAllRadioButton() {
        rbAll.isChecked = false
        rbUnComplete.isChecked = false
        rbComplete.isChecked = false
    }

//endregion

//region Threads

    private fun chooseOperatorThread(operators: List<OperatorActivity.ChoosedTaskOperator>) {
        ThreadStart(object : ThreadHandler<Boolean?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean? {
                val listOfOperatorData = mutableListOf<CheckerTaskOperatorData>()
                operators.forEach {
                    listOfOperatorData.add(CheckerTaskOperatorData(it.taskId, it.id, it.name))
                }
                dockingTaskManager?.releaseDockingListWithInterrupt(listOfOperatorData)
                operators.forEach { checkerTaskManager?.removeOperator(it.taskId, it.id) }
                return null
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {
                listOfData?.clear()
                listOfData?.addAll(checkerTaskManager?.getLocalCheckerTaskItems(taskId).orEmpty())
                sortResultList()
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun startMovingToDockingThread(operators: List<String>) {
        ThreadStart(object : ThreadHandler<Boolean> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean {
                checkerTaskManager?.moveItemsToDocking(checkerTaskData?.refDocId, operators)
                return true
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {

            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })

    }

    private fun startMovingToStagingThread() {
        ThreadStart(object : ThreadHandler<Boolean> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean {
                checkerTaskManager?.moveItemsToStaging(checkerTaskData?.refDocId)
                return true
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {

            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        })
    }

    private fun startFinishTaskThread() {
        ThreadStart(object : ThreadHandler<Boolean?> {
            override fun onPrepare() {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER)
            }

            override fun onBackground(): Boolean? {
                if (checkerTaskData?.hasBeenStart!!) {
                    val checkerTaskData = checkerTaskManager?.getLocalCheckerTaskData(taskId)
                    checkerTaskManager?.finishCheckerTask(checkerTaskData)
                    userManager?.SaveProvider(SortEnum.SHOW_ALL.value)
                } else {
                    checkerTaskManager?.startCheckerTask(checkerTaskData?.refDocUri, checkerTaskData?.refDocId)
                }
                return null
            }

            override fun onError(e: java.lang.Exception?) {
                dismissProgressDialog()
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {

                dismissProgressDialog()
                if (checkerTaskData?.hasBeenStart!!) {
                    checkerTaskManager?.removeCheckerTask(taskId)
                    DismissActivity()
                } else {
                    checkerTaskData?.hasBeenStart = true
                }
                setUpItemSortingLayout()
                resetButtonText()
            }

            override fun onFinish() {

            }
        })
    }

    private fun getNewOrderMonitoringWithHigherPriorityThread() {
        ThreadStart(object : ThreadHandler<List<HigherPriorityTaskData>?> {
            override fun onPrepare() {

            }

            override fun onBackground(): List<HigherPriorityTaskData>? {
                val results = higherPriorityTaskManager?.newOrderMonitoringWithHigherPriorityThanRcvCheckerTasks
                return results
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: List<HigherPriorityTaskData>?) {
                if (data?.size!! > 0)
                    showDialogForNewHighPriorityOrder(data)
            }

            override fun onFinish() {

            }
        })
    }

    private fun rejectOrderMonitoringThread(listOfHigherPriorityTask: List<HigherPriorityTaskData>?) {
        ThreadStart(object : ThreadHandler<Boolean> {
            override fun onPrepare() {

            }

            override fun onBackground(): Boolean {
                listOfHigherPriorityTask?.forEach({
                    higherPriorityTaskManager?.rejectOrderMonitoringBasedOrderNo(it.refDocUri, it.refDocId)
                })
                return true
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Boolean?) {
                showInfoDialog(getString(R.string.common_information_title)
                        , MessageFormat.format(getString(R.string.content_reject_new_high_priority_task), listOfHigherPriorityTask?.size))
            }

            override fun onFinish() {

            }
        })
    }

    private fun getCountOfNotStartedTasksThread() {
        ThreadStart(object : ThreadHandler<Int?> {
            override fun onPrepare() {

            }

            override fun onBackground(): Int? {
                return checkerTaskManager?.getCountOfNotStartedTasks()
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: Int?) {
                data?.let {
                    if (it > 0)
                        NotificationTool().showNotification(this@CheckerTaskDetailActivity, "wms", LoginActivity::class.java, "WMS", MessageFormat.format(getString(R.string.notif_not_started_task), it), 9010, (resources.getDrawable(R.mipmap.ic_launcher) as BitmapDrawable).bitmap)
                }
            }

            override fun onFinish() {

            }
        })
    }

    private fun updateFSidThread() {
        ThreadStart2(object : NgemartActivity.ThreadHandler2<Boolean>(this) {
            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                checkerTaskManager?.updateFSid()
                return null
            }
        })
    }

    //endregion

    private fun showSortedItemList() {
        if (lastSort == null)
            lastSort = SortEnum.SHOW_ALL.value

        if (lastSort.equals(""))
            lastSort = SortEnum.SHOW_ALL.value

        userManager?.SaveProvider(lastSort)
        sortResultList()
    }

    private fun sortResultList() {
        val itemList = checkerTaskManager?.getLocalCheckerTaskItems(taskId).orEmpty()
        val checkedItemList = mutableListOf<CheckerTaskItemData>()
        val uncheckedItemList = mutableListOf<CheckerTaskItemData>()
        uncheckedItemList.addAll(itemList)

        for (data in itemList) {
            if (data.badQtyCheckResult + data.goodQtyCheckResult > 0.00 && data.badQtyCheckResult + data.goodQtyCheckResult >= data.qty) {
                checkedItemList.add(data)
                uncheckedItemList.remove(data)
            }
        }

        when (lastSort) {
            SortEnum.SHOW_ALL.value -> {
                listOfData?.clear()
                listOfData?.addAll(checkedItemList)
                listOfData?.addAll(uncheckedItemList)
            }
            SortEnum.SHOW_UNCHECKED.value -> {
                listOfData?.clear()
                listOfData?.addAll(uncheckedItemList)
            }
            SortEnum.SHOW_CHECKED.value -> {
                listOfData?.clear()
                listOfData?.addAll(checkedItemList)
            }
        }

        adapter?.notifyDataSetChanged()
    }
}
