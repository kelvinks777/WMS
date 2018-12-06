package com.gin.wms.warehouse.checker

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bosnet.ngemart.libgen.Common
import com.gin.ngemart.baseui.NgemartActivity
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter
import com.gin.ngemart.baseui.component.NgemartCardView
import com.gin.wms.manager.*
import com.gin.wms.manager.db.data.CheckerTaskData
import com.gin.wms.manager.db.data.HigherPriorityTaskData
import com.gin.wms.manager.db.data.UserData
import com.gin.wms.warehouse.R
import com.gin.wms.warehouse.base.WarehouseActivity
import com.gin.wms.warehouse.component.LeftMenuModule
import com.gin.wms.warehouse.login.LoginActivity
import com.gin.wms.warehouse.service.CountdownService
import kotlinx.android.synthetic.main.app_bar_checker_main.*
import kotlinx.android.synthetic.main.content_checker_main.*
import java.text.MessageFormat

class CheckerMainActivity : WarehouseActivity(), LeftMenuModule.ILeftMenu, DialogInterface.OnClickListener, NgemartCardView.CardListener<CheckerTaskData>, SwipeRefreshLayout.OnRefreshListener {

    private var leftMenuModule: LeftMenuModule? = null
    private var tokenLocalManager: TokenLocalManager? = null
    private var operatorManager: OperatorManager? = null
    private var checkerTaskManager: CheckerTaskManager? = null
    private var higherPriorityTaskManager: HigherPriorityTaskManager? = null
    private var userManager: UserManager? = null
    private var userData: UserData? = null
    private var listOfData: MutableList<CheckerTaskData>? = null
    private var adapter: NgemartRecyclerViewAdapter<CheckerTaskData>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var isPressedTwice: Boolean = false

    //region Override Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_checker_main)
            initObject()
            initComponent()
            initData()
            startService(Intent(this, CountdownService::class.java))
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            findNewCheckerTasksThread()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, CountdownService::class.java))
        super.onDestroy()
    }

    override fun onRefresh() {
        findNewCheckerTasksThread()
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

    override fun onFinishCountdownService() {
        try {
            super.onFinishCountdownService()
            findNewCheckerTasksThread()
            getNewOrderMonitoringWithHigherPriorityThread()
        } catch (e:Exception) {
            showErrorDialog(e)
        }
    }

    override fun onCardClick(position: Int, view: View?, data: CheckerTaskData?) {
        try {
            val intent = Intent(this, CheckerTaskDetailActivity::class.java)
            intent.putExtra(CheckerTaskDetailActivity.ARG_CHECKER_TASK_TASK_ID, data?.taskId)
            startActivity(intent)
        } catch (e: Exception) {
            showErrorDialog(e)
        }
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        ThreadStart<Boolean>(object : NgemartActivity.ThreadHandler<Boolean> {
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
                progressDialog.dismiss()
                showErrorDialog(e)
            }

            @Throws(Exception::class)
            override fun onSuccess(data: Boolean?) {
                progressDialog.dismiss()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            override fun onFinish() {}
        })
    }

    override fun onMenuItemSelected(itemId: Int): Boolean {
        when (itemId) {
            R.id.nav_logout -> showAskDialog(getString(R.string.common_confirmation_title), getString(R.string.common_close_app_confirmation_content), this, null)
            R.id.navigation_high_priority_list -> startActivity(HigherPriorityTaskActivity::class.java)
            else -> throw Exception("Menu not found")
        }
        return true
    }

    override fun onBackPressed() {
        leftMenuModule?.closeDrawer()
    }

    override fun onHandleBackPressed() {
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

    //endregion

    //region Private Functions

    private fun initData() {
        findNewCheckerTasksThread()
    }

    private fun initObject() {
        userManager = UserManager(this)
        userData = userManager?.GetUserDataFromLocal()
        operatorManager = OperatorManager(this)
        checkerTaskManager = CheckerTaskManager(this)
        higherPriorityTaskManager = HigherPriorityTaskManager(this)
    }

    private fun initComponent() {
        toolbar.title = "Checker Task"
        setSupportActionBar(toolbar)
        initLeftMenuModule()
        initRecyclerView()
        swipeRefresh.setOnRefreshListener(this)
    }

    private fun initRecyclerView() {
        val numColumn = 1

        listOfData = mutableListOf()
        gridLayoutManager = GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false)
        rvList?.setHasFixedSize(true)
        rvList?.SetDefaultDecoration()
        rvList?.layoutManager = gridLayoutManager

        adapter = NgemartRecyclerViewAdapter(this, R.layout.card_checker_main, listOfData)
        adapter?.setHasStableIds(true)
        adapter?.setRecyclerListener(this)
        rvList?.adapter = adapter
    }

    private fun initLeftMenuModule() {
        leftMenuModule = LeftMenuModule(this, toolbar)
        leftMenuModule?.setLeftMenuListener(this)
        leftMenuModule?.setAppVersion(Common.GetVersionInfo(this))
        leftMenuModule?.setProfileInfo(userData)
        setProfileImageThread()
    }

    //endregion

    //region Threads

    private fun updateFSidThread(){
        ThreadStart2(object : NgemartActivity.ThreadHandler2<Boolean>(this) {
            @Throws(Exception::class)
            override fun onBackground(): Boolean? {
                checkerTaskManager?.updateFSid()
                return null
            }
        })
    }

    private fun findNewCheckerTasksThread() {
        ThreadStart(object : ThreadHandler<List<CheckerTaskData>?>{
            override fun onPrepare() {
                swipeRefresh.isRefreshing = true
            }

            override fun onBackground(): List<CheckerTaskData>? {
                return checkerTaskManager?.findTask()
            }

            override fun onError(e: java.lang.Exception?) {
                showErrorDialog(e)
            }

            override fun onSuccess(data: List<CheckerTaskData>?) {
                listOfData?.clear()
                listOfData?.addAll(data.orEmpty())
                adapter?.notifyDataSetChanged()
            }

            override fun onFinish() {
                swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun setProfileImageThread() {
        if (!userData?.profileImage.equals("")) {
            leftMenuModule?.setProfileImage(userData?.profileImage)
        } else {
            ThreadStart(object : NgemartActivity.ThreadHandler<String> {

                override fun onPrepare() {

                }

                override fun onBackground(): String? {
                    while (userData?.profileImage.equals("")) {
                        Thread.sleep(1000)
                        userData = userManager?.GetUserDataFromLocal()
                    }
                    return userData?.profileImage
                }

                override fun onError(e: Exception) {
                    Toast.makeText(this@CheckerMainActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(data: String) {
                    leftMenuModule?.setProfileImage(userData?.profileImage)
                }

                override fun onFinish() {

                }
            })
        }
    }

    private fun getNewOrderMonitoringWithHigherPriorityThread() {
        ThreadStart(object : ThreadHandler<List<HigherPriorityTaskData>?>{
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
                if (data?.size!! > 0) {
                    showDialogForNewHighPriorityOrder(data)
                }
            }

            override fun onFinish() {

            }
        })
    }

    fun showDialogForNewHighPriorityOrder(listOfHigherPriorityTask: List<HigherPriorityTaskData>?) {
        val showInfoListener = DialogInterface.OnClickListener { dialogInterface, i ->
            startActivity(HigherPriorityTaskActivity::class.java)
        }

//        Hanya menampilkan informasi, tidak langsung me-reject
        val rejectListener = DialogInterface.OnClickListener { dialogInterface, i ->
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

        showAskDialog(getString(R.string.common_confirmation_title)
                ,MessageFormat.format(getString(R.string.content_new_high_priority_task), listOfHigherPriorityTask?.size)
                ,showInfoListener
                ,null)
    }

    //endregion
}
