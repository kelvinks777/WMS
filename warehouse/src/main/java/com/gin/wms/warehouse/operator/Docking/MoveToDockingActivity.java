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

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.wms.manager.CheckerTaskManager;
import com.gin.wms.manager.DockingTaskManager;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.ProductManager;
import com.gin.wms.manager.StockLocationManager;
import com.gin.wms.manager.db.data.CheckerTaskData;
import com.gin.wms.manager.db.data.DockingTaskData;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.StockLocationData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;
import com.gin.wms.warehouse.operator.TaskSwitcherActivity;

import java.util.ArrayList;
import java.util.List;

public class MoveToDockingActivity extends WarehouseActivity
        implements View.OnClickListener,
        SingleInputDialogFragment.InputManualInterface {
    private static int SCAN_STAGING_PALLET_ID_CODE = 1234;
    private DockingTaskManager dockingTaskManager;
    private MovingTaskManager movingTaskManager;
    private StockLocationManager stockLocationManager;
    private CheckerTaskManager checkerTaskManager;
    private ProductManager productManager;
    private NgemartRecyclerView recyclerView;
    private NgemartRecyclerViewAdapter<List<StockLocationData>> adapter;
    private final List<StockLocationData> stockLocationDataList = new ArrayList<>();
    private EditText txtPalletNo;
    private DockingTaskData dockingTaskData;
    private CheckerTaskData checkerTaskData;
    private Button btnCancel, btnProcessMoving;
    private ImageButton scanPallet;
    public static String MOVING_TASK_STATE = "movingTask";
    private static int DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET = 333;

    //region Override Functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_move_to_docking);
            initObjectManager();
            initComponent();
            initToolbar();
            initAdapter();
            initRecyclerAdapter();
            checkExistingMovingTaskThread();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null){
                if(resultCode == Activity.RESULT_OK && requestCode == SCAN_STAGING_PALLET_ID_CODE){
                    txtPalletNo.setText(result.getContents());
                    getStockLocationDataThread(result.getContents());
                }
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtPalletNo:
                DialogFragment dialogFragment;
                dialogFragment = SingleInputDialogFragment.newInstance(DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET, "Pallet", txtPalletNo.getText().toString());
                dialogFragment.show(getSupportFragmentManager(), "");
                break;

            case R.id.btnScanStagingPallet:
                LaunchBarcodeScanner(SCAN_STAGING_PALLET_ID_CODE);
                break;

            case R.id.btnProcessMoving:
                showAskDialog(getResources().getString(R.string.ask_start_moving_now_title),getResources().getString(R.string.ask_start_moving_now_body),
                        (dialog, which) -> startMovingDockingTask(),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
            case R.id.btnCancelMoving:
                showAskDialog(getResources().getString(R.string.ask_cancel_moving_now_title),getResources().getString(R.string.ask_cancel_moving_now_body),
                        (dialog, which) -> cancelMovingTaskThread(),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            if(typeCode == DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET){
                txtPalletNo.setText(inputText);
                getStockLocationDataThread(inputText);
            }else{
                throw new ClassNotFoundException("typeCode is not match !");
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }
    //endregion

    //region Init

    private void initComponent(){
        btnProcessMoving = findViewById(R.id.btnProcessMoving);
        btnCancel = findViewById(R.id.btnCancelMoving);
        scanPallet = findViewById(R.id.btnScanStagingPallet);
        txtPalletNo = findViewById(R.id.txtPalletNo);
        recyclerView = findViewById(R.id.rvMovingToDocking);
        setComponentHandler();
    }

    private void setComponentHandler(){
        btnCancel.setOnClickListener(this);
        btnProcessMoving.setOnClickListener(this);
        scanPallet.setOnClickListener(this);
        txtPalletNo.setOnClickListener(this);
    }

    private void initAdapter(){
        adapter = new NgemartRecyclerViewAdapter(this, R.layout.card_move_to_docking_item, stockLocationDataList);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerAdapter(){
        int numColumn = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.SetDefaultDecoration();
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void initObjectManager()throws Exception{
        dockingTaskManager = new DockingTaskManager(getApplicationContext());
        checkerTaskManager = new CheckerTaskManager(getApplicationContext());
        movingTaskManager = new MovingTaskManager(getApplicationContext());
        stockLocationManager = new StockLocationManager(getApplicationContext());
        productManager = new ProductManager(getApplicationContext());
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_moving_to_docking));
    }

    //endregion

    //region Threads

    private void getStockLocationDataThread(String palletNo)throws Exception{
        ThreadStart(new ThreadHandler<List<StockLocationData>>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public List<StockLocationData> onBackground() throws Exception {
                dockingTaskData = dockingTaskManager.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.getValue());
                checkerTaskData = checkerTaskManager.findTaskByReleaseDocRef(dockingTaskData.docRefId);
                List<StockLocationData> dataList = stockLocationManager.getStockLocationByBinPallet(checkerTaskData.stagingId, palletNo);
                stockLocationDataList.clear();
                stockLocationDataList.addAll(dataList);

                return dataList;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(List<StockLocationData> dataList) throws Exception {
                if(dataList.size() == 0 ){
                    String strInfo = getResources().getString(R.string.error_product_not_found_by_selected_pallet)+ "'" + palletNo.toUpperCase() + "'";
                    showInfoSnackBar(strInfo);
                } else{
                    fillRecyclerView(dataList);
                }
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            void fillRecyclerView(List<StockLocationData> stockLocationDataList)throws Exception{
                for(StockLocationData data : stockLocationDataList)
                    productManager.getProduct(data.productId);

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void checkExistingMovingTaskThread(){
        ThreadStart(new ThreadHandler<MovingTaskData>() {
            @Override
            public void onPrepare() throws Exception {

            }

            @Override
            public MovingTaskData onBackground() throws Exception {
                MovingTaskData movingTaskData = movingTaskManager.getLastMovingTaskFromLocal();
                if(movingTaskData == null)
                    movingTaskData = movingTaskManager.getMovingTaskFromServerByOperatorId();

                dockingTaskData = dockingTaskManager.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.getValue());
                checkerTaskData = checkerTaskManager.findTaskByReleaseDocRef(dockingTaskData.docRefId);

                return movingTaskData;
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(MovingTaskData data) throws Exception {
                if(data != null)
                    showMoveToDockingDetail(data);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void startMovingDockingTask(){
        if(stockLocationDataList.size() == 0)
            showErrorDialog(getResources().getString(R.string.error_pallet_not_scanned_yet));
        else
            startMovingToDockingTaskThread();
    }

    private void startMovingToDockingTaskThread(){
        ThreadStart(new ThreadHandler<MovingTaskData>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_content_creating_moving_task), ProgressType.SPINNER);
            }

            @Override
            public MovingTaskData onBackground() throws Exception {
                String stagingId = checkerTaskData.stagingId;
                String dockingNo = dockingTaskData.dockings.get(0).dockingId;
                String palletNumber = txtPalletNo.getText().toString();
                String operatorId = dockingTaskData.getChecker().id;
                String releaseOrderId = dockingTaskData.docRefId;

                MovingTaskData movingTaskData = movingTaskManager.createMovingTask(stagingId, dockingNo, palletNumber, operatorId, releaseOrderId);
                movingTaskManager.saveMovingTaskData(movingTaskData);
                return movingTaskManager.startMovingData(movingTaskData);
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(MovingTaskData data) throws Exception {
                if(data != null)
                    showMoveToDockingDetail(data);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void cancelMovingTaskThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_content_cancel_moving_task), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                movingTaskManager.cancelMovingTask();
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
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

    //endregion

    private void showMoveToDockingDetail(MovingTaskData movingTaskData){
        Intent intent = new Intent(this, MoveToDockingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MOVING_TASK_STATE, movingTaskData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void moveToTaskSwitcher(){
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
