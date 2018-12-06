package com.gin.wms.warehouse.operator.Docking;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.StockLocationManager;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.StockLocationData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;
import com.gin.wms.warehouse.operator.TaskSwitcherActivity;

import java.util.ArrayList;
import java.util.List;

public class MoveToDockingDetailActivity extends WarehouseActivity
        implements View.OnClickListener, SingleInputDialogFragment.InputManualInterface{
    private NgemartRecyclerView recyclerView;
    private NgemartRecyclerViewAdapter<List<StockLocationData>> adapter;
    private final List<StockLocationData> stockLocationDataList = new ArrayList<>();
    private MovingTaskManager movingTaskManager;
    private StockLocationManager stockLocationManager;
    private ImageButton btnScanDestDocking;
    private Button btnFinishTaskAndBackToDocking;
    private TextView tvStagingId, tvDockingId, tvPalletNumber;
    private EditText txtDestDocking;
    private MovingTaskData movingTaskData;
    private static int SCAN_STAGING_DEST_DOCKING_ID_CODE = 4321;
    private static int DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_move_to_docking_detail);
            init();
            showDataToUiThread();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null){
                if(resultCode == Activity.RESULT_OK && requestCode == SCAN_STAGING_DEST_DOCKING_ID_CODE)
                    txtDestDocking.setText(result.getContents());
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtDestDocking:
                DialogFragment dialogFragment;
                dialogFragment = SingleInputDialogFragment.newInstance(DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET,"Docking", txtDestDocking.getText().toString());
                dialogFragment.show(getSupportFragmentManager(), "");
                break;

            case R.id.btnScanDestDocking:
                LaunchBarcodeScanner(SCAN_STAGING_DEST_DOCKING_ID_CODE);
                break;

            case R.id.btnFinishAndBackToDocking:
                validateToFinishTaskAndBackToDocking();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }
    //region Init

    private void initObjectManager()throws Exception{
        movingTaskManager = new MovingTaskManager(getApplicationContext());
        stockLocationManager = new StockLocationManager(getApplicationContext());
    }

    private void init()throws Exception{
        initObjectManager();
        initComponent();
        initToolbar();
        initComponentHandler();
    }

    private void initComponent(){
        btnScanDestDocking = findViewById(R.id.btnScanDestDocking);
        btnFinishTaskAndBackToDocking = findViewById(R.id.btnFinishAndBackToDocking);
        txtDestDocking = findViewById(R.id.txtDestDocking);
        recyclerView = findViewById(R.id.rvFinishDocking);
        tvStagingId = findViewById(R.id.tvStagingId);
        tvDockingId = findViewById(R.id.tvDockingId);
        tvPalletNumber = findViewById(R.id.tvPalletNumber);
    }

    private void initToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarMoveToDocking);
        toolbar.setTitle(getResources().getString(R.string.title_moving_to_docking));
    }

    private void initComponentHandler(){
        btnScanDestDocking.setOnClickListener(this);
        btnFinishTaskAndBackToDocking.setOnClickListener(this);
        txtDestDocking.setOnClickListener(this);
    }

    //endregion

    private void validateToFinishTaskAndBackToDocking(){
        String dockingId = txtDestDocking.getText().toString();
        if(dockingId.equals("")){
            showErrorDialog(getResources().getString(R.string.error_dest_docking_cannot_empty));
        }else{
            if(!validateDockingId(dockingId))
                showErrorDialog(getResources().getString(R.string.error_destination_docking_not_match));
            else
                showFinishTaskAndBackToDockingTaskDialog();
        }
    }

    private void getDataFromIntent()throws Exception{
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            movingTaskData = (MovingTaskData) bundle.getSerializable(MoveToDockingActivity.MOVING_TASK_STATE);
        }
    }

    private Boolean validateDockingId(String dockingLocation){
        String dockingId = movingTaskData.dockingId.toLowerCase();
        String dockingIdFromInput = dockingLocation.toLowerCase();

        return dockingId.toLowerCase().equals(dockingIdFromInput.toLowerCase());
        //return !movingTaskData.dockingId.toLowerCase().equals(dockingLocation.toLowerCase());
    }

    private void showFinishTaskAndBackToDockingTaskDialog(){
        showAskDialog(getResources().getString(R.string.ask_finish_moving_now_title),getResources().getString(R.string.ask_finish_moving_and_back_to_docking_body),
                (dialog, which) -> finishTaskAndBackToDockingTaskThread(movingTaskData.movingId),
                (dialog, which) -> dialog.dismiss()
        );
    }

    private void showDataToUiThread(){
        ThreadStart(new ThreadHandler<List<StockLocationData>>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_content_load), ProgressType.SPINNER);
            }

            @Override
            public List<StockLocationData> onBackground() throws Exception {
                List<StockLocationData> stockList = new ArrayList<>();
                getDataFromIntent();
                if(movingTaskData != null)
                    stockList = stockLocationManager.getStockLocationByBinPallet(movingTaskData.stagingBinId, movingTaskData.palletNo);

                return stockList;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(List<StockLocationData> resultList) throws Exception {
                showMovingTaskToUi();
                showStockLocationToUi(resultList);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            private void showStockLocationToUi(List<StockLocationData> resultList){
                stockLocationDataList.clear();
                stockLocationDataList.addAll(resultList);
                initAdapter();
                initRecyclerView();
                adapter.notifyDataSetChanged();
            }

            private void showMovingTaskToUi(){
                tvStagingId.setText(movingTaskData.stagingBinId);
                tvDockingId.setText(movingTaskData.dockingId);
                tvPalletNumber.setText(movingTaskData.palletNo);
            }
        });
    }

    private void finishTaskAndBackToDockingTaskThread(String movingTaskId){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_finish_moving_task), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                movingTaskManager.finishMovingTaskAndBackToMovingToDockingTask(movingTaskId);
                movingTaskManager.deleteLocalMovingTask();
                return true;
            }

            @Override
            public void onError(Exception e) {
                try{
                    movingTaskManager.saveMovingTaskData(movingTaskData);
                    dismissProgressDialog();
                    showErrorDialog(e);
                }catch (Exception ex){
                    showErrorDialog(ex);
                }
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                moveToTaskSwitcher();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void moveToTaskSwitcher(){
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initAdapter(){
        adapter = new NgemartRecyclerViewAdapter(this, R.layout.card_move_to_docking_item, stockLocationDataList);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView(){
        int numColumn = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.SetDefaultDecoration();
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            if(typeCode == DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET)
                txtDestDocking.setText(inputText);
            else
                throw new ClassNotFoundException("typeCode is not match !");
        }catch (Exception e){
            showErrorDialog(e);
        }
    }
}
