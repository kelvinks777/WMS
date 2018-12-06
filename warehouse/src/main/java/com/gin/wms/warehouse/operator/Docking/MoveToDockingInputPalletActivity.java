package com.gin.wms.warehouse.operator.Docking;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.CheckerTaskManager;
import com.gin.wms.manager.DockingTaskManager;
import com.gin.wms.manager.MovingTaskManager;
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

import java.util.List;

import static com.gin.wms.warehouse.operator.PutAway.PutawayTaskData.Pallet.palletNo;

public class MoveToDockingInputPalletActivity extends WarehouseActivity implements View.OnClickListener, SingleInputDialogFragment.InputManualInterface {
    private static final int REQUEST_BARCODE_SCANNER_CODE = 1122;
    private static final int REQUEST_INPUT_FRAGMENT_CODE = 1122;
    private DockingTaskManager dockingTaskManager;
    private CheckerTaskManager checkerTaskManager;
    private StockLocationManager stockLocationManager;
    private MovingTaskManager movingTaskManager;
    private Button btnStartMovingTask;
    private EditText txtInputPallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_move_to_docking_input_pallet);
            init();
            getPreviousMovingTask();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btnScanPallet:
                    LaunchBarcodeScanner(REQUEST_BARCODE_SCANNER_CODE);
                    break;
                case R.id.txtInputPallet:
                    String customTitle = "Masukkan Nomor Pallet Sebelum Memulai Task Ini";
                    DialogFragment dialogFragment;
                    dialogFragment = SingleInputDialogFragment.newInstanceWithCustomTitle(REQUEST_INPUT_FRAGMENT_CODE, customTitle, "");
                    dialogFragment.setCancelable(false);
                    dialogFragment.show(getSupportFragmentManager(), "");
                    break;
                case R.id.btnStartMovingTask:
                    String palletNo = txtInputPallet.getText().toString();
                    justStartMovingToDockingTaskThread(palletNo);
                    break;
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_BARCODE_SCANNER_CODE))
                    checkAvailableProductOnStagingLocationThread(result.getContents());
            }
        }catch (Exception ex){
            showErrorDialog(ex);
        }
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        if(typeCode == REQUEST_BARCODE_SCANNER_CODE)
            checkAvailableProductOnStagingLocationThread(inputText);
    }

    @Override
    public void onBackPressed() {
        moveBackToTaskSwitcherActivity();
        super.onBackPressed();
    }

    private void moveBackToTaskSwitcherActivity(){
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void init()throws Exception{
        initObject();
        initComponent();
        initToolbar();
    }

    private void getPreviousMovingTask()throws Exception{
        ThreadStart(new ThreadHandler<MovingTaskData>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_content_start_moving_task), ProgressType.SPINNER);
            }

            @Override
            public MovingTaskData onBackground() throws Exception {
                return movingTaskManager.getMovingTaskFromServerByOperatorId();

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

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Input Pallet Move to Docking");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initObject()throws Exception{
        dockingTaskManager = new DockingTaskManager(getApplicationContext());
        checkerTaskManager = new CheckerTaskManager(getApplicationContext());
        stockLocationManager = new StockLocationManager(getApplicationContext());
        movingTaskManager = new MovingTaskManager(getApplicationContext());
    }

    private void initComponent(){
        ImageButton imgBarcodeScanner = findViewById(R.id.imgBtnPalletNumber);
        txtInputPallet = findViewById(R.id.txtInputPallet);
        btnStartMovingTask = findViewById(R.id.btnStartMovingTask);
        imgBarcodeScanner.setOnClickListener(this);
        btnStartMovingTask.setOnClickListener(this);
        txtInputPallet.setOnClickListener(this);
        btnStartMovingTask.setEnabled(false);
    }

    private void checkAvailableProductOnStagingLocationThread(String palletNo){
        ThreadStart(new ThreadHandler<List<StockLocationData>>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public List<StockLocationData> onBackground() throws Exception {
                DockingTaskData dockingTaskData = dockingTaskManager.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.getValue());
                CheckerTaskData checkerTaskData = checkerTaskManager.findTaskByReleaseDocRef(dockingTaskData.docRefId);
                return stockLocationManager.getStockLocationByBinPallet(checkerTaskData.stagingId, palletNo.toUpperCase());
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(List<StockLocationData> dataList) throws Exception {
                startMovingTask(dataList);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            private void startMovingTask(List<StockLocationData> dataList){
                String strNoStockLocationFound = getResources().getString(R.string.error_product_not_found_by_selected_pallet)
                        + " '"
                        + palletNo.toUpperCase()
                        + "'"
                        + "\n"
                        + getString(R.string.info_moving_task_declined);

                if(dataList == null){
                    txtInputPallet.setText("");
                    btnStartMovingTask.setEnabled(false);
                    showErrorDialog(strNoStockLocationFound);
                }else{
                    if(dataList.size() == 0){
                        txtInputPallet.setText("");
                        btnStartMovingTask.setEnabled(false);
                        showErrorDialog(strNoStockLocationFound);
                    }else{
                        txtInputPallet.setText(palletNo);
                        btnStartMovingTask.setEnabled(true);
                    }
                }
            }
        });
    }

    private void justStartMovingToDockingTaskThread(String palletNo){
        ThreadStart(new ThreadHandler<MovingTaskData>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_content_creating_moving_task), ProgressType.SPINNER);
            }

            @Override
            public MovingTaskData onBackground() throws Exception {
                MovingTaskData movingTaskData = movingTaskManager.getMovingTaskFromServerByOperatorId();
                if (movingTaskData == null)
                    movingTaskData = createMovingTaskData();

                return movingTaskData;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(MovingTaskData data) throws Exception {
                if (data != null)
                    showMoveToDockingDetail(data);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            MovingTaskData createMovingTaskData()throws Exception{
                MovingTaskData movingTaskData;
                DockingTaskData dockingTaskData = dockingTaskManager.getDockingTaskDataByOperator(RefDocUriEnum.RELEASE.getValue());
                CheckerTaskData checkerTaskData = checkerTaskManager.findTaskByReleaseDocRef(dockingTaskData.docRefId);
                String stagingId = checkerTaskData.stagingId;
                String dockingNo = dockingTaskData.dockings.get(0).dockingId;
                String operatorId = dockingTaskData.getChecker().id;
                String releaseOrderId = dockingTaskData.docRefId;

                movingTaskData = movingTaskManager.createMovingTask(stagingId, dockingNo, palletNo.toUpperCase(), operatorId, releaseOrderId);
                movingTaskManager.saveMovingTaskData(movingTaskData);

                return movingTaskManager.startMovingData(movingTaskData);
            }
        });
    }

    private void showMoveToDockingDetail(MovingTaskData movingTaskData) {
        Intent intent = new Intent(this, MoveToDockingDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MoveToDockingActivity.MOVING_TASK_STATE, movingTaskData);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
