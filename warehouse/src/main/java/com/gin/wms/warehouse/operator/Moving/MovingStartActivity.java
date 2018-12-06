package com.gin.wms.warehouse.operator.Moving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.MovingTaskSourceItemData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;
import com.gin.wms.manager.db.data.enums.TaskTypeEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;
import com.gin.wms.warehouse.operator.Picking.PickingTaskActivity;
import com.gin.wms.warehouse.warehouse_problem.WarehouseProblemActivity;

import java.util.ArrayList;
import java.util.List;

public class MovingStartActivity extends WarehouseActivity
        implements View.OnClickListener, SingleInputDialogFragment.InputManualInterface {
    private MovingTaskManager movingTaskManager;
    private MovingTaskData movingTaskData;
    private EditText txtPalletNo, txtSourceNo;
    private static int SCAN_PALLET_ID_CODE = 2345;
    private static int SCAN_SOURCE_ID_CODE = 5432;
    private NgemartRecyclerView recyclerView;
    private NgemartRecyclerViewAdapter<List<MovingTaskSourceItemData>> adapter;
    private final List<MovingTaskSourceItemData> sourceItemDataList = new ArrayList<>();
    public static final String REPLENISH_DATA_CODE = "replenishData";
    public static final String DESTRUCTION_DATA_CODE = "destructionData";
    private Toolbar toolbar;
    public static final String MUTATION_DATA_CODE = "mutationData";
    public static final String MUTATION_ORDER_DATA_CODE = "mutationOrderData";
    private Button btnReport;

    //region override functions

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_moving_start);
            init();
            getDataFromIntent();
            showDataToUi(movingTaskData);
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null){
                if(resultCode == Activity.RESULT_OK)
                    validateInputAndShowToUi(requestCode, result.getContents());
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtPalletNo:
                String PALLET_TITLE_DIALOG = getResources().getString(R.string.pallet_id);
                showInputFragment(SCAN_PALLET_ID_CODE, PALLET_TITLE_DIALOG, txtPalletNo.getText().toString());
                break;
            case R.id.txtSourceNo:
                String SOURCE_TITLE_DIALOG = getResources().getString(R.string.source_id);
                showInputFragment(SCAN_SOURCE_ID_CODE, SOURCE_TITLE_DIALOG, txtSourceNo.getText().toString());
                break;
            case R.id.btnScanPallet:
                LaunchBarcodeScanner(SCAN_PALLET_ID_CODE);
                break;
            case R.id.btnScanSource:
                LaunchBarcodeScanner(SCAN_SOURCE_ID_CODE);
                break;
            case R.id.btnMoveToDestination:
                if(validateInputTextAlreadyFilled())
                    showConfirmationToStartTask();
                break;
            case R.id.btnReport:
                Intent intentToReport=new Intent(MovingStartActivity.this, WarehouseProblemActivity.class);
                startActivity(intentToReport);
                break;

        }
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            validateInputAndShowToUi(typeCode, inputText);
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }

    //endregion

    //region init

    private void init()throws Exception{
        initObjectManager();
        initComponent();
        initToolbar();
        initAdapter();
        initRecyclerAdapter();
    }

    private void initObjectManager()throws Exception{
        movingTaskManager = new MovingTaskManager(getApplicationContext());
    }

    private void initComponent(){
        Button btnProcess = findViewById(R.id.btnMoveToDestination);
        ImageButton btnScanPallet = findViewById(R.id.btnScanPallet);
        ImageButton btnScanSource = findViewById(R.id.btnScanSource);
        btnReport=findViewById(R.id.btnReport);

        txtPalletNo = findViewById(R.id.txtPalletNo);
        txtSourceNo = findViewById(R.id.txtSourceNo);
        recyclerView = findViewById(R.id.rvReplenishStart);

        btnProcess.setOnClickListener(this);
        btnScanPallet.setOnClickListener(this);
        btnScanSource.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        txtPalletNo.setOnClickListener(this);
        txtSourceNo.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarReplenishStart);
        toolbar.setTitle(getResources().getString(R.string.title_replenish_start));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAdapter(){
        adapter = new NgemartRecyclerViewAdapter(this, R.layout.card_moving_source_item, sourceItemDataList);
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

    //endregion

    //region threads

    private void showDataToUi(MovingTaskData data){
        if(data != null){
            if(data.startTime != null){
                setToolbarTitle(data);
                if(data.getStatus() == TaskStatusEnum.PROGRESS){
                    movingTaskData = data;
                    moveToFinishReplenishActivity();
                }else{
                    sourceItemDataList.clear();
                    sourceItemDataList.addAll(data.sourceItemList);
                    movingTaskData = data;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void moveToProductListActivity() {
        Intent intent = new Intent(this, ProductListForMutationOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MUTATION_DATA_CODE, movingTaskData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setToolbarTitle(MovingTaskData data){
        if(data.getTaskType() == TaskTypeEnum.REPLENISHMENT_BOX || data.getTaskType() == TaskTypeEnum.REPLENISHMENT_PCS)
            toolbar.setTitle(getResources().getString(R.string.title_replenish_start));
        else if (data.getTaskType() == TaskTypeEnum.REWAREHOUSING)
            toolbar.setTitle(getResources().getString(R.string.title_re_warehousing_start));
        else
            toolbar.setTitle(getResources().getString(R.string.title_moving_to_docking));
    }

    private void startReplenishTaskThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_start_replenishData), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                Thread.sleep(500);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                moveToFinishReplenishActivity();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    //endregion

    //region other functions

    private void getDataFromIntent()throws Exception{
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            movingTaskData = (MovingTaskData)bundle.getSerializable(MovingStartActivity.REPLENISH_DATA_CODE);

        if (movingTaskData == null)
            movingTaskData = (MovingTaskData)bundle.getSerializable(MovingStartActivity.DESTRUCTION_DATA_CODE);

        if (movingTaskData == null)
            movingTaskData = (MovingTaskData)bundle.getSerializable(MovingStartActivity.MUTATION_DATA_CODE);

        if (movingTaskData == null)
            movingTaskData = (MovingTaskData)bundle.getSerializable(MovingStartActivity.MUTATION_ORDER_DATA_CODE);

        if(movingTaskData != null)
            setToolbarTitle(movingTaskData);
    }

    private void showInputFragment(int typeCode, String dialogTitle, String previousValue){
        DialogFragment dialogFragment;
        dialogFragment = SingleInputDialogFragment.newInstance(typeCode, dialogTitle, previousValue);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void validateInputAndShowToUi(int typeCode, String inputText) throws Exception{
        if(typeCode == SCAN_PALLET_ID_CODE){
            if(validateInputText(typeCode, inputText)){
                txtPalletNo.setText(inputText);
            } else{
                String pallet_warning = getResources().getString(R.string.pallet_id_is_not_match);
                showErrorDialog(pallet_warning);
                txtPalletNo.setText("");
            }
        } else if (typeCode == SCAN_SOURCE_ID_CODE){
            if(validateInputText(typeCode, inputText)){
                txtSourceNo.setText(inputText);
            } else{
                String dest_warning = getResources().getString(R.string.source_id_is_not_match);
                showErrorDialog(dest_warning);
                txtSourceNo.setText("");
            }
        }
        else{
            String default_warning = getResources().getString(R.string.typeCode_is_not_match);
            throw new ClassNotFoundException(default_warning);
        }
    }

    private boolean validateInputText(int typeCode, String inputText){
        boolean isCorrect = false;
        if(typeCode == SCAN_PALLET_ID_CODE){
            if(inputText.toLowerCase().equals(movingTaskData.palletNo.toLowerCase()))
                isCorrect = true;
        }else if(typeCode == SCAN_SOURCE_ID_CODE){
            if(inputText.toLowerCase().equals(movingTaskData.stagingBinId.toLowerCase()))
                isCorrect = true;
        }

        return isCorrect;
    }

    private void showConfirmationToStartTask(){
        String title = getResources().getString(R.string.common_confirmation_title);
        String body = getResources().getString(R.string.ask_to_move_to_dest);
        showAskDialog(title, body,
                (dialog, which) -> startReplenishTaskThread(),
                (dialog, which) -> dialog.dismiss());
    }

    private void moveToFinishReplenishActivity(){
        if(movingTaskData.movingType== TaskTypeEnum.INTERNAL_MOVEMENT.getValue() &&
                RefDocUriEnum.MUTATION.getValue().equals(movingTaskData.docRefUri)) {
            moveToProductListActivity();
        }
        else {
            Intent intent = new Intent(this, MovingFinishActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(REPLENISH_DATA_CODE, movingTaskData);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private boolean validateInputTextAlreadyFilled(){
        String warning = getResources().getString(R.string.warning_data_should_be_filled);
        if(txtPalletNo.getText().toString().equals("")){
            txtPalletNo.requestFocus();
            txtPalletNo.setError(warning);
            return false;
        }

        if (txtSourceNo.getText().toString().equals("")){
            txtSourceNo.requestFocus();
            txtSourceNo.setError(warning);
            return false;
        }

        return true;
    }



    //endregion
}
