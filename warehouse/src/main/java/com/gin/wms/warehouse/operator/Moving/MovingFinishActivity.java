package com.gin.wms.warehouse.operator.Moving;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.MovingTaskDestItemData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.manager.db.data.enums.TaskTypeEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;
import com.gin.wms.warehouse.operator.TaskSwitcherActivity;
import com.gin.wms.warehouse.warehouse_problem.WarehouseProblemActivity;

import java.util.ArrayList;
import java.util.List;

public class MovingFinishActivity extends WarehouseActivity implements View.OnClickListener, SingleInputDialogFragment.InputManualInterface {
    private MovingTaskData movingTaskData;
    private TextView tvPalletNo, tvDestNo;
    private static int SCAN_PALLET_ID_CODE = 3456;
    private static int SCAN_DEST_ID_CODE = 6543;
    private NgemartRecyclerView recyclerView;
    private NgemartRecyclerViewAdapter<List<MovingTaskDestItemData>> adapter;
    private final List<MovingTaskDestItemData> destItemDataList = new ArrayList<>();
    private MovingTaskManager movingTaskManager;
    private Toolbar toolbar;
    protected MovingTaskDestItemData destItemData;
    protected TextView titleDialogInput;
    protected EditText etInputNewPallet;
    protected Button btnAction;

    //region override functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_moving_finish);
            getDataFromIntent();
            init();
            showDataToUi();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvPalletNo:
                String PALLET_TITLE_DIALOG = getResources().getString(R.string.pallet_id);
                showInputFragment(SCAN_PALLET_ID_CODE, PALLET_TITLE_DIALOG, tvPalletNo.getText().toString());
                break;
            case R.id.tvDestNo:
                String DEST_TITLE_DIALOG = getResources().getString(R.string.dest_id);
                showInputFragment(SCAN_DEST_ID_CODE, DEST_TITLE_DIALOG, tvDestNo.getText().toString());
                break;
            case R.id.btnScanPallet:
                LaunchBarcodeScanner(SCAN_PALLET_ID_CODE);
                break;
            case R.id.btnScanDest:
                LaunchBarcodeScanner(SCAN_DEST_ID_CODE);
                break;
            case R.id.btnTakeNewPallet:
                showInputNewPallet();
                break;
            case R.id.btnFinishReplenish:
                if(validateInputTextAlreadyFilled())
                    showConfirmationToStartTask();
                break;
            case R.id.btnReport:
                Intent intentToReport=new Intent(MovingFinishActivity.this, WarehouseProblemActivity.class);
                startActivity(intentToReport);
                break;
        }
    }

    private void showInputNewPallet() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_take_new_pallet, null);
        dialog.setView(view);

        titleDialogInput = view.findViewById(R.id.tvInputNewPallet);
        etInputNewPallet = view.findViewById(R.id.txtUpdatePallet);
        btnAction = view.findViewById(R.id.btnUpdatePallet);

        AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        btnAction.setOnClickListener(v -> {
            if(etInputNewPallet.getText().toString().isEmpty())
                etInputNewPallet.setError(getResources().getString(R.string.error_cannot_input_text_empty));
            else {
                try {
                    String palletNo = etInputNewPallet.getText().toString();
                    updatePalletForMutation(palletNo);
                    alertDialog.cancel();
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }
        });
    }

    private void updatePalletForMutation(String palletNo) throws Exception {
        ThreadStart(new ThreadHandler<MovingTaskData>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_processing_update_pallet), ProgressType.SPINNER);
            }

            @Override
            public MovingTaskData onBackground() throws Exception {
                movingTaskManager.updatePalletNoForMutationOrder(movingTaskData.movingId, palletNo, destItemData.productId);
                movingTaskData = movingTaskManager.getMovingTaskFromServerById(movingTaskData.movingId);
                return movingTaskData;
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(MovingTaskData data) throws Exception {
                dismissProgressDialog();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_pallet_number_updated),Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }

    //endregion

    //region init

    private void init()throws Exception{
        initObjectManager();
        initComponent();
        initAdapter();
        initRecyclerAdapter();
    }

    private void initObjectManager()throws Exception{
        movingTaskManager = new MovingTaskManager(getApplicationContext());
    }

    private void initComponent(){
        Button btnProcess = findViewById(R.id.btnFinishReplenish);
        Button btnTakePallet = findViewById(R.id.btnTakeNewPallet);
        Button btnReport=findViewById(R.id.btnReport);
        ImageButton btnScanPallet = findViewById(R.id.btnScanPallet);
        ImageButton btnScanSource = findViewById(R.id.btnScanDest);
        tvPalletNo = findViewById(R.id.tvPalletNo);
        tvDestNo = findViewById(R.id.tvDestNo);
        recyclerView = findViewById(R.id.rvFinishReplenish);

        if(movingTaskData.movingType == TaskTypeEnum.INTERNAL_MOVEMENT.getValue() &&
                RefDocUriEnum.MUTATION.getValue().equals(movingTaskData.docRefUri)) {
            btnTakePallet.setVisibility(View.VISIBLE);
            btnProcess.setText(getResources().getString(R.string.btnFinishMoving));
        }
        else {
            btnTakePallet.setVisibility(View.GONE);
        }

        btnTakePallet.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        btnScanPallet.setOnClickListener(this);
        btnScanSource.setOnClickListener(this);
        btnReport.setOnClickListener(this);
        tvPalletNo.setOnClickListener(this);
        tvDestNo.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarReplenishFinish);
        toolbar.setTitle(getResources().getString(R.string.title_moving_finish));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if (movingTaskData.movingType == TaskTypeEnum.INTERNAL_MOVEMENT.getValue() &&
                RefDocUriEnum.MUTATION.getValue().equals(movingTaskData.docRefUri)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }*/
    }

    private void initAdapter(){
        adapter = new NgemartRecyclerViewAdapter(this, R.layout.card_moving_dest_item, destItemDataList);
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

    //region other functions

    private void showInputFragment(int typeCode, String dialogTitle, String previousValue){
        DialogFragment dialogFragment;
        dialogFragment = SingleInputDialogFragment.newInstance(typeCode, dialogTitle, previousValue);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void validateInputAndShowToUi(int typeCode, String inputText) throws Exception{
        if(typeCode == SCAN_PALLET_ID_CODE){
            if(validateInputText(typeCode, inputText)){
                tvPalletNo.setText(inputText);
            } else{
                String pallet_warning = getResources().getString(R.string.pallet_id_is_not_match);
                showErrorDialog(pallet_warning);
                tvPalletNo.setText("");
            }
        } else if (typeCode == SCAN_DEST_ID_CODE){
            if(validateInputText(typeCode, inputText)){
                tvDestNo.setText(inputText);
            } else{
                String dest_warning = getResources().getString(R.string.dest_id_is_not_match);
                showErrorDialog(dest_warning);
                tvDestNo.setText("");
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
        }else if(typeCode == SCAN_DEST_ID_CODE){
            if(inputText.toLowerCase().equals(movingTaskData.dockingId.toLowerCase()))
                isCorrect = true;
        }
        return isCorrect;
    }

    private boolean validateInputTextAlreadyFilled(){
        String warning = getResources().getString(R.string.warning_data_should_be_filled);
        if(tvPalletNo.getText().toString().equals("")){
            tvPalletNo.requestFocus();
            tvPalletNo.setError(warning);
            return false;
        }

        if (tvDestNo.getText().toString().equals("")){
            tvDestNo.requestFocus();
            tvDestNo.setError(warning);
            return false;
        }

        return true;
    }

    private void showConfirmationToStartTask(){
        String title = getResources().getString(R.string.common_confirmation_title);
        String body = getResources().getString(R.string.ask_to_finish_replenish);
        showAskDialog(title, body,
                (dialog, which) -> finishMutationTaskThread(),
                (dialog, which) -> dialog.dismiss());
    }

    private void moveToTaskSwitcherActivity(){
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDataToUi(){
        destItemDataList.clear();
        if(movingTaskData.movingType == TaskTypeEnum.INTERNAL_MOVEMENT.getValue() &&
                RefDocUriEnum.MUTATION.getValue().equals(movingTaskData.docRefUri)){
            destItemDataList.add(destItemData);

            adapter.notifyDataSetChanged();
        }
        else {
            destItemDataList.addAll(movingTaskData.destItemList);

            adapter.notifyDataSetChanged();
        }
    }

    private void getDataFromIntent()throws Exception{
        Bundle bundle = getIntent().getExtras();

        if(bundle == null) return;

        movingTaskData = (MovingTaskData) bundle.getSerializable(MovingStartActivity.REPLENISH_DATA_CODE);

        if(movingTaskData==null)
            movingTaskData = (MovingTaskData)bundle.getSerializable(MovingStartActivity.DESTRUCTION_DATA_CODE);

        if(movingTaskData==null) {
            movingTaskData = (MovingTaskData) bundle.getSerializable(ProductListForMutationOrderActivity.MUTATION_DATA_CODE);

            //if (movingTaskData.movingType == TaskTypeEnum.INTERNAL_MOVEMENT.getValue() &&
            //        RefDocUriEnum.MUTATION.getValue().equals(movingTaskData.docRefUri)) {

                if (destItemData == null) {
                    destItemData = (MovingTaskDestItemData) bundle.getSerializable(ProductListForMutationOrderActivity.DEST_ITEM_CODE);
                }
            //}
        }

        if(movingTaskData==null)
            movingTaskData = (MovingTaskData) bundle.getSerializable(MovingStartActivity.MUTATION_DATA_CODE);

        if(movingTaskData != null)
            setToolbarTitle(movingTaskData);
    }

    private void setToolbarTitle(MovingTaskData data){
        initToolbar();
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(data.getTaskType() == TaskTypeEnum.REPLENISHMENT_BOX || data.getTaskType() == TaskTypeEnum.REPLENISHMENT_PCS)
            toolbar.setTitle(getResources().getString(R.string.title_replenish_finish));
        else if (data.getTaskType() == TaskTypeEnum.REWAREHOUSING)
            toolbar.setTitle(getResources().getString(R.string.title_re_warehousing_finish));
        else if (data.getTaskType() == TaskTypeEnum.INTERNAL_MOVEMENT) {
            if (data.docRefUri == RefDocUriEnum.DESTRUCTION.toString())
                toolbar.setTitle(getResources().getString(R.string.title_destruction_finish));
            else if (data.docRefUri == RefDocUriEnum.MUTATION.toString()) {
                toolbar.setTitle(getResources().getString(R.string.title_mutation_finish));
            }
            else if (data.docRefUri == RefDocUriEnum.BANDED.toString())
                toolbar.setTitle(getResources().getString(R.string.title_banded_finish));
        }
        else
            toolbar.setTitle(getResources().getString(R.string.title_moving_to_docking));
    }

    //endregion

    //region threads

    private void finishMutationTaskThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_finish_replenishData), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                //movingTaskManager.finishMovingTaskAndBackToLoadingTask(movingTaskData.movingId);
                movingTaskManager.finishMovingTask(movingTaskData.movingId);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                 moveToTaskSwitcherActivity();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            validateInputAndShowToUi(typeCode, inputText);
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    //endregion

}
