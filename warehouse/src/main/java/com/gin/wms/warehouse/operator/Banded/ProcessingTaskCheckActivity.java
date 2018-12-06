package com.gin.wms.warehouse.operator.Banded;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.wms.manager.db.data.CheckerTaskItemResultData;
import com.gin.wms.manager.db.data.ProcessingTaskData;
import com.gin.wms.manager.db.data.ProcessingTaskItemResultData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.manager.*;
import com.gin.wms.manager.db.data.UserData;
import java.util.List;


public class ProcessingTaskCheckActivity extends WarehouseActivity implements View.OnClickListener {

    String ARG_CHECKER_TASK_TASK_ID = "ARG_CHECKER_TASK_TASK_ID";
    String ARG_CHECKER_TASK_QTY_NEEDED = "ARG_CHECKER_TASK_PRODUCT_ID";
    String ARG_CHECKER_TASK_QTY_REMAINING = "ARG_CHECKER_TASK_CLIENT_LOCATION_ID";
    String ARG_CHECKER_TASK_REF_DOC_URI = "ARG_CHECKER_TASK_REF_DOC_URI";

    private ProcessingTaskManager processingTaskManager;
    private ProcessingTaskData processingTaskData;
    private ProcessingTaskItemResultData processingTaskItemResultData;

    private TextView tvBandedId, tvQtyNeeded, tvQtyRemaining;
    private Button btnInfo, btnInput, btnFinishBanded;

    private OperatorManager operatorManager;
    private UserManager userManager;
    private UserData userData;

    public static final String BANDED_DATA_CODE = "bandedData";
    private NgemartRecyclerViewAdapter <CheckerTaskItemResultData> adapter;
    private NgemartRecyclerView recyclerView;
    private List<ProcessingTaskItemResultData> listOfData;
    private RecyclerView bandedInfoList;

    //region Override function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_processing_task_check);
            processingTaskManager = new ProcessingTaskManager(this);
            //initObject();
            initComponent();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btnInfoBanded:
                    ShowBandedInfo();
                    break;
                case R.id.btnInputBanded:
                    ShowInputDialog();
                    break;
                case R.id.btnFinishBanded:
                    DialogInterface.OnClickListener okListener = FinishTaskThread();
                    showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin menyimpan hasil processing task ini?", okListener, null);
                    break;
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }
    //endregion

    //region Init
    private void initComponent() {
        btnFinishBanded = findViewById(R.id.btnFinishBanded);
        btnInfo = findViewById(R.id.btnInfoBanded);
        btnInput = findViewById(R.id.btnInputBanded);
        tvBandedId = findViewById(R.id.tvBandedId);
        tvQtyNeeded = findViewById(R.id.tvQtyNeeded);
        tvQtyRemaining = findViewById(R.id.tvQtyRemaining);
        bandedInfoList = findViewById(R.id.bandedInfoList);
        btnInfo.setOnClickListener(this);
        btnInput.setOnClickListener(this);
        btnFinishBanded.setOnClickListener(this);

        initRecyclerView();
        initToolbar();
    }

    private void initObject() {
        getIntentData();
    }

    private void initToolbar() {
        android.support.v7.widget.Toolbar toolbarTitle = findViewById(R.id.toolbarBanded);
        toolbarTitle.setTitle("Check Banded Result");
        setSupportActionBar(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        //int numColumn = 1;
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.SetDefaultDecoration();
        //recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new NgemartRecyclerViewAdapter(this, R.layout.card_banded_info, listOfData);
        adapter.setHasStableIds(true);
        bandedInfoList.setAdapter(adapter);
    }
    //endregion

    //region Other Function
    private void ShowBandedInfo() throws Exception {
        ThreadStart(new ThreadHandler<Object>() {
            @Override
            public void onPrepare() throws Exception{
            }

            @Override
            public Object onBackground() throws Exception {
                return null;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) throws Exception {
                try{
                    bandedInfoList.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void FinishCheckTask() throws Exception {
        ThreadStart(new ThreadHandler<Object>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public Object onBackground() throws Exception {
                return null;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) throws Exception {
                dismissProgressDialog();
                DismissActivity();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void ShowInputDialog() throws Exception {
        ThreadStart(new ThreadHandler<Object>() {
            @Override
            public void onPrepare() {

            }

            @Override
            public Object onBackground() {
                return null;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) throws Exception {
                try{
                    showInputCheckData();
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //processingTaskResultData = (ProcessingTaskRedultData) bundle.getSerializable(ProcessingTaskInputActivity.PROCESSING_TASK_STATE);
        }

    }
    //endregion

    //region Thread
    private DialogInterface.OnClickListener FinishTaskThread(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FinishCheckTask();
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }
        };
    }
    //endregion

    private void showInputCheckData(){
        Intent intent = new Intent(this, ProcessingTaskResultInputActivity.class);
        startActivity(intent);
    }
}
