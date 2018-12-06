package com.gin.wms.warehouse.operator.Picking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.DateUtil;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.PickingTaskManager;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.PickingTaskData;
import com.gin.wms.manager.db.data.PickingTaskDestItemData;
import com.gin.wms.manager.db.data.PickingTaskSourceItemData;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.CompUomInterpreter;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;
import com.gin.wms.warehouse.operator.TaskSwitcherActivity;
import com.gin.wms.warehouse.warehouse_problem.WarehouseProblemActivity;

import java.text.MessageFormat;
import java.util.List;

public class PickingTaskActivity extends WarehouseActivity
        implements View.OnClickListener,
        SingleInputDialogFragment.InputManualInterface{
    private PickingTaskManager pickingTaskManager;
    private PickingTaskData pickingTaskData;

    private EditText txtId, txtBarcode, txtPalletNumber, txtProductName, txtExpiredDate;
    private TextView tvToolbarTitle, tvToolbarCounter, tvLocation;
    private ProgressBar spinnerPickingTask;
    private Button btnStartPicking, btnNextPicking, btnFinishPicking,btnReport;
    private CardView cardExpDate;
    private CompUomInterpreter compUomInterpreter;

    private static int SCAN_PICKING_ID_CODE = 1001;
    private String currentAction;
    private boolean alreadyStartTask;
    private double actualSourcePickingQty = 0;
    private double actualDestPickingQty = 0;
    private static int DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET = 999;
    private static int DIALOG_FRAGMENT_TYPE_CODE_FOR_BARCODE = 666;
    public static String PICKING_TASK_ID = "pickingTaskId";

    private enum CurrentActionEnum {
        PICKING_FROM_SOURCE("PICKING_FROM_SOURCE"),
        PICKING_TO_DEST("PICKING_TO_DEST");
        private String action;

        CurrentActionEnum(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    //region Override function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_picking_task);
            initObject();
            initComponent();
            initToolbar();
            getBundle();
            getPickingTaskDataThread();
        }catch (Exception ex){
            showErrorDialog(ex);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnBarcode:
                if (alreadyStartTask)
                    LaunchBarcodeScanner(SCAN_PICKING_ID_CODE);
                else
                    showInfoDialog(getResources().getString(R.string.info), getResources().getString(R.string.info_must_start_task));
                break;
            case R.id.btnNextPicking:
                showNextItemToUi();
                break;
            case R.id.btnStartPicking:
                showAskDialog(getResources().getString(R.string.start_picking), getResources().getString(R.string.ask_start_task),
                        (dialog, which) -> startPickingThread(),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
            case R.id.btnFinishPicking:
                finishTask();
                break;
            case R.id.txtBarcode:
                if (alreadyStartTask)
                    showInputBarcodeDialog();
                else
                    showInfoDialog(getResources().getString(R.string.info), getResources().getString(R.string.info_must_start_task));
                break;
            case R.id.btnReport:
                Intent intentToReport=new Intent(PickingTaskActivity.this, WarehouseProblemActivity.class);
                startActivity(intentToReport);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (resultCode == Activity.RESULT_OK && (requestCode == SCAN_PICKING_ID_CODE))
                    validateLocationId(result.getContents());
            }
        }catch (Exception ex){
            showErrorDialog(ex);
        }
    }

    @Override
    public void onBackPressed() {
        if(currentAction.equals(CurrentActionEnum.PICKING_FROM_SOURCE.getAction())){
            showInfoSnackBar(getResources().getString(R.string.exit_warning));
        }else if(currentAction.equals(CurrentActionEnum.PICKING_TO_DEST.getAction())){
            if((DescLocationHelper.currentIndex >= pickingTaskData.destBin.size()-1) && DescLocationHelper.hasCorrectLocation){
                super.onBackPressed();
                finish();
            }else{
                showInfoSnackBar(getResources().getString(R.string.exit_warning));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showInfoSnackBar(getResources().getString(R.string.exit_warning));
                break;
        }
        return true;
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            if(typeCode == DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET){
                showAskDialog(getResources().getString(R.string.update_pallet_number),
                        MessageFormat.format(getString(R.string.ask_to_update_pallet_number), inputText),
                        (dialog, which) -> UpdatePalletNumberThread(inputText),
                        (dialog, which) -> dialog.dismiss()
                );
            }else if(typeCode == DIALOG_FRAGMENT_TYPE_CODE_FOR_BARCODE){
                validateLocationId(inputText);
            }else{
                throw new ClassNotFoundException("typeCode is not match !");
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    //endregion

    //region Init

    private void initToolbar(){
        tvToolbarTitle.setText(getResources().getString(R.string.picking_task_title));
    }

    private void initComponent(){
        ImageButton imgBtnBarcode = findViewById(R.id.imgBtnBarcode);
        txtId = findViewById(R.id.txtId);
        txtBarcode = findViewById(R.id.txtBarcode);
        txtPalletNumber = findViewById(R.id.txtPalletNumber);
        txtExpiredDate = findViewById(R.id.txtExpiredDate);
        txtProductName = findViewById(R.id.txtProductName);
        compUomInterpreter = findViewById(R.id.compUomInterpreter);
        spinnerPickingTask = findViewById(R.id.spinnerPickingTask);
        btnStartPicking = findViewById(R.id.btnStartPicking);
        btnNextPicking = findViewById(R.id.btnNextPicking);
        btnFinishPicking = findViewById(R.id.btnFinishPicking);
        btnReport=findViewById(R.id.btnReport);
        cardExpDate = findViewById(R.id.cardExpDate);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvToolbarCounter = findViewById(R.id.tvToolbarCounter);
        tvLocation = findViewById(R.id.tvLocation);

        imgBtnBarcode.setOnClickListener(this);
        txtBarcode.setOnClickListener(this);
        btnStartPicking.setOnClickListener(this);
        btnNextPicking.setOnClickListener(this);
        btnFinishPicking.setOnClickListener(this);
        btnReport.setOnClickListener(this);

        clearUi();
        setCardExpDateVisibility(true);
    }

    private void initObject()throws Exception{
        pickingTaskManager = new PickingTaskManager(getApplicationContext());
    }

    private void setSpinnerVisibility(boolean needToShow){
        if(needToShow){
            if(spinnerPickingTask.getVisibility() == View.GONE)
                spinnerPickingTask.setVisibility(View.VISIBLE);
        }else {
            if(spinnerPickingTask.getVisibility() == View.VISIBLE)
                spinnerPickingTask.setVisibility(View.GONE);
        }
    }

    //endregion

    //region Ui

    private void getBundle(){
        if(getIntent().getExtras() != null)
            this.pickingTaskData = (PickingTaskData)getIntent().getSerializableExtra(PICKING_TASK_ID);
    }

    private void showInputPalletDialog(){
        String customTitle = "Masukkan Nomor Pallet Tujuan Sebelum Memulai Task Ini";
        DialogFragment dialogFragment;
        dialogFragment = SingleInputDialogFragment.newInstanceWithCustomTitle(DIALOG_FRAGMENT_TYPE_CODE_FOR_PALLET, customTitle, "");
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void showInputBarcodeDialog(){
        DialogFragment dialogFragment;
        dialogFragment = SingleInputDialogFragment.newInstance(DIALOG_FRAGMENT_TYPE_CODE_FOR_BARCODE, "Barcode", txtBarcode.getText().toString());
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void setCardExpDateVisibility(boolean isShowProperty){
        if(isShowProperty){
            if(cardExpDate.getVisibility() == View.GONE)
                cardExpDate.setVisibility(View.VISIBLE);
        }else{
            if(cardExpDate.getVisibility() == View.VISIBLE)
                cardExpDate.setVisibility(View.GONE);
        }
    }

    private void clearUi(){
        txtId.setText("");
        txtBarcode.setText("");
        txtProductName.setText("");
        txtExpiredDate.setText("");
    }

    private void setSourceBinDataToUi(PickingTaskSourceItemData sourceItemData){
        clearUi();
        tvLocation.setText(getResources().getString(R.string.source_location));
        txtId.setText(sourceItemData.sourceBinId.trim());
        txtPalletNumber.setText(sourceItemData.palletNo.trim());
        txtProductName.setText(sourceItemData.productName.trim());
        txtExpiredDate.setText(DateUtil.DateToStringFormat("dd/MM/yyyy", sourceItemData.expiredData).trim());
        setUpCompUomInterpreter(sourceItemData.palletConversionValue, sourceItemData.getCompUom(), sourceItemData.qty);
        setCurrentStepTask();
    }

    private void setDestinationDataToUi(PickingTaskDestItemData taskDestItemData){
        clearUi();
        tvLocation.setText(getResources().getString(R.string.source_destination));
        txtId.setText(taskDestItemData.destBinId);
        txtPalletNumber.setText(taskDestItemData.palletNo);
        txtProductName.setText(taskDestItemData.productName);
        txtExpiredDate.setText("");
        setUpCompUomInterpreter(taskDestItemData.palletConversionValue, taskDestItemData.getCompUom(), taskDestItemData.qty);
        setCurrentStepTask();
    }

    private void setUpCompUomInterpreter(double palletConversionValue, CompUomData compUomData, double qty){
        compUomInterpreter.setPalletConversion(palletConversionValue);
        compUomInterpreter.setCompUomData(compUomData);
        compUomInterpreter.setQty(qty);
    }

    private void setCompUomInterpreterActive(){
        if(currentAction.equals(CurrentActionEnum.PICKING_FROM_SOURCE.getAction()))
            compUomInterpreter.setEditButtonActive(true);
        else
            compUomInterpreter.setEditButtonActive(false);
    }
    //endregion

    //region Validation

    private void finishTask(){
        showAskDialog(getResources().getString(R.string.info), getResources().getString(R.string.ask_finish_task),
                (dialog, which) -> finishPickingTaskThread(),
                (dialog, which) -> dialog.dismiss());
    }

    private void showNextItemToUi(){
        if(currentAction.equals(CurrentActionEnum.PICKING_FROM_SOURCE.getAction()))
            updateSourceItemQty();
        else if(currentAction.equals(CurrentActionEnum.PICKING_TO_DEST.getAction()))
            updateDestinationItemQty();
    }

    private void updateSourceItemQty(){
        if(SourceBinHelper.hasCorrectLocation){
            if(compUomInterpreter.getQty() > SourceBinHelper.sourceItemData.qty){
                showErrorDialog(getResources().getString(R.string.input_qty_cannot_larger_than) + " " +(int)SourceBinHelper.sourceItemData.qty);
            } else{
                SourceBinHelper.sourceItemData.qty = compUomInterpreter.getQty();
                updatePickingSourceQtyThread();
            }
        }else{
            showErrorDialog(getResources().getString(R.string.bin_not_selected_yet));
        }
    }

    private void updateDestinationItemQty(){
        if(DescLocationHelper.hasCorrectLocation){
            if(validatePickingDest()){
                DescLocationHelper.destItemData.qty = compUomInterpreter.getQty();
                updatePickingDestQtyThread();
            } else{
                showRelevantPickingDestQtyToFill();
            }
        }else{
            showErrorDialog(getResources().getString(R.string.dest_not_selected_yet));
        }
    }

    private boolean validatePickingDest(){
        return actualSourcePickingQty >= (compUomInterpreter.getQty() + actualDestPickingQty);
    }

    private void showRelevantPickingDestQtyToFill(){
        double relevantQty = actualSourcePickingQty - (compUomInterpreter.getQty() + actualDestPickingQty);
        if(relevantQty > 0)
            showInfoDialog(getResources().getString(R.string.input_qty),getResources().getString(R.string.allowed_qty_to_input) + relevantQty);
        else
            showInfoDialog(getResources().getString(R.string.input_qty), getResources().getString(R.string.input_qty_has_maximum_amount));
    }

    private void validateLocationId(String locationId){
        if(currentAction.equals(CurrentActionEnum.PICKING_FROM_SOURCE.getAction()))
            validateSourceBinId(locationId);
        else if(currentAction.equals(CurrentActionEnum.PICKING_TO_DEST.getAction()))
            validateDestinationId(locationId);
    }

    private void validateSourceBinId(String locationId){
        List<PickingTaskSourceItemData> sourceBin = pickingTaskData.sourceBin;
        if(sourceBin.get(SourceBinHelper.currentIndex).sourceBinId.toLowerCase().equals(locationId.toLowerCase())){
            SourceBinHelper.hasCorrectLocation = true;
            txtBarcode.setText(locationId);
            showInfoSnackBar(getResources().getString(R.string.source_bin_match));
        }else{
            txtBarcode.setText("");
            SourceBinHelper.hasCorrectLocation = false;
            showErrorSnackBar(getResources().getString(R.string.source_bin_not_match));
        }
    }

    private void validateDestinationId(String locationId){
        List<PickingTaskDestItemData> destLoc = pickingTaskData.destBin;
        if(destLoc.get(DescLocationHelper.currentIndex).destBinId.toLowerCase().equals(locationId.toLowerCase())){
            txtBarcode.setText(locationId);
            DescLocationHelper.hasCorrectLocation = true;
            showInfoSnackBar(getResources().getString(R.string.destination_bin_match));
        }else{
            txtBarcode.setText("");
            DescLocationHelper.hasCorrectLocation = false;
            showErrorSnackBar(getResources().getString(R.string.destination_bin_not_match));
        }
    }

    //endregion

    //region Threads
    private void getPickingTaskDataThread(){
        ThreadStart(new ThreadHandler<PickingTaskData>() {
            @Override
            public void onPrepare() throws Exception {
                setSpinnerVisibility(true);
            }

            @Override
            public PickingTaskData onBackground() throws Exception {
                if(pickingTaskData == null)
                    pickingTaskData =  pickingTaskManager.findAvailablePickingTask();

                return pickingTaskData;
            }

            @Override
            public void onError(Exception e) {
                setSpinnerVisibility(false);
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(PickingTaskData data) throws Exception {
                initFirstUi(data);
            }

            @Override
            public void onFinish() {
                setSpinnerVisibility(false);
            }

            void initFirstUi(PickingTaskData data){
                if(data.getStatus() == TaskStatusEnum.NEW){
                    btnStartPicking.setVisibility(View.VISIBLE);
                    btnStartPicking.setText(getResources().getString(R.string.start_picking_source_action_text_button));
                    alreadyStartTask = false;
                }else if(data.getStatus() == TaskStatusEnum.PROGRESS){
                    btnStartPicking.setVisibility(View.GONE);
                    btnNextPicking.setVisibility(View.VISIBLE);
                    alreadyStartTask = true;

                    if(data.destBin.get(0).palletNo.equals(""))
                        showInputPalletDialog();
                }

                tvToolbarTitle.setText(getResources().getString(R.string.picking_source_action));
                pickingTaskData = data;
                currentAction = CurrentActionEnum.PICKING_FROM_SOURCE.getAction();
                SourceBinHelper.currentIndex = 0;
                SourceBinHelper.sourceItemData = data.sourceBin.get(SourceBinHelper.currentIndex);
                setSourceBinDataToUi(SourceBinHelper.sourceItemData);
            }
        });
    }

    private void setCurrentStepTask(){
        if(currentAction.equals(CurrentActionEnum.PICKING_FROM_SOURCE.getAction()))
            tvToolbarCounter.setText("1/2");
        else if(currentAction.equals(CurrentActionEnum.PICKING_TO_DEST.getAction()))
            tvToolbarCounter.setText("2/2");
    }

    private void startPickingThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.start_picking_process), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                pickingTaskManager.startPickingTask(pickingTaskData.pickingTaskId);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                alreadyStartTask = true;
                btnStartPicking.setVisibility(View.GONE);
                btnNextPicking.setVisibility(View.VISIBLE);

                if(pickingTaskData.destBin.get(0).palletNo.equals(""))
                    showInputPalletDialog();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void updatePickingSourceQtyThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.saving_data), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                Thread.sleep(500);
                pickingTaskManager.updatePickingSourceQty(SourceBinHelper.sourceItemData);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                setActualPickingSourceQty();
                if(isPickingSourceAvailable())
                    updateNextPickingSourceToUi();
                else
                    switchToPickingDest();

                setCompUomInterpreterActive();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            void setActualPickingSourceQty(){
                PickingTaskSourceItemData data = pickingTaskData.sourceBin.get(SourceBinHelper.currentIndex);
                if(data.qty != compUomInterpreter.getQty())
                    actualSourcePickingQty = actualSourcePickingQty + compUomInterpreter.getQty();
                else
                    actualSourcePickingQty = actualSourcePickingQty + data.qty;
            }

            boolean isPickingSourceAvailable(){
                return pickingTaskData.sourceBin.size()-1 > SourceBinHelper.currentIndex;
            }

            void updateNextPickingSourceToUi(){
                SourceBinHelper.currentIndex = SourceBinHelper.currentIndex + 1;
                SourceBinHelper.hasCorrectLocation = false;
                SourceBinHelper.sourceItemData = pickingTaskData.sourceBin.get(SourceBinHelper.currentIndex);
                setSourceBinDataToUi(SourceBinHelper.sourceItemData);
            }

            void switchToPickingDest(){
                showInfoDialog(getResources().getString(R.string.info), getResources().getString(R.string.info_no_more_picking_source_data));
                setCardExpDateVisibility(false);
                tvToolbarTitle.setText(getResources().getString(R.string.picking_source_action));
                SourceBinHelper.clear();
                currentAction = CurrentActionEnum.PICKING_TO_DEST.getAction();

                setNewDescLocationHelper();
                setPickingDestToUi();
            }

            void setNewDescLocationHelper(){
                DescLocationHelper.clear();
                DescLocationHelper.destItemData = pickingTaskData.destBin.get(DescLocationHelper.currentIndex);
            }

            void setPickingDestToUi(){
                setDestinationDataToUi(DescLocationHelper.destItemData);
            }

        });
    }

    private void updatePickingDestQtyThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.saving_data), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                Thread.sleep(500);
                pickingTaskManager.updatePickingDestQty(DescLocationHelper.destItemData);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                setActualPickingDestQty();
                if(isPickingDestAvailable())
                    updateNextPickingLocationToUi();
                else
                    setNoMoreTaskToUi();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }

            boolean isPickingDestAvailable(){
                int nextPosition = DescLocationHelper.currentIndex + 1;
                return pickingTaskData.destBin.size()-1 >= nextPosition;
            }

            void setActualPickingDestQty(){
                actualDestPickingQty = actualDestPickingQty + compUomInterpreter.getQty();
            }

            void updateNextPickingLocationToUi(){
                DescLocationHelper.currentIndex = DescLocationHelper.currentIndex + 1;
                DescLocationHelper.hasCorrectLocation = false;
                DescLocationHelper.destItemData = pickingTaskData.destBin.get(DescLocationHelper.currentIndex);
                setDestinationDataToUi(DescLocationHelper.destItemData);
            }

            void setNoMoreTaskToUi(){
                DescLocationHelper.destItemData = pickingTaskData.destBin.get(DescLocationHelper.currentIndex);
                btnNextPicking.setVisibility(View.GONE);
                btnFinishPicking.setVisibility(View.VISIBLE);
                btnFinishPicking.setText(getResources().getString(R.string.btnFinish));
                showInfoDialog(getResources().getString(R.string.common_information_title),
                        getResources().getString(R.string.info_no_more_picking_dest_data_and_finish),
                        (dialog, which) -> finishPickingTaskThread());
            }
        });
    }

    private void finishPickingTaskThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.finish_picking_process), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                Thread.sleep(1000);
                pickingTaskManager.finishPickingTask(pickingTaskData.pickingTaskId);
                return null;
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

    private void moveToTaskSwitcher(){
        Intent intent = new Intent(this, TaskSwitcherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void UpdatePalletNumberThread(String palletNumber){
        ThreadStart(new ThreadHandler<Boolean>() {
            String pickingTaskId = pickingTaskData.pickingTaskId;

            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getResources().getString(R.string.update_pallet_number_process), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                Thread.sleep(500);
                pickingTaskManager.updateDestPalletNumber(pickingTaskId, palletNumber);
                pickingTaskData = pickingTaskManager.findAvailablePickingTask();
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showInfoDialog(getResources().getString(R.string.update_pallet_number), "Nomor pallet berhasil di simpan");
                txtPalletNumber.setText(palletNumber);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    //endregion

    //region Wrapper class
    private static class SourceBinHelper{
        static int currentIndex = 0;
        static boolean hasCorrectLocation = false;
        static PickingTaskSourceItemData sourceItemData;

        public static void clear(){
            currentIndex = 0;
            hasCorrectLocation = false;
            sourceItemData = null;
        }
    }

    private static class DescLocationHelper{
        static int currentIndex = 0;
        static boolean hasCorrectLocation = false;
        static PickingTaskDestItemData destItemData;

        public static void clear(){
            currentIndex = 0;
            hasCorrectLocation = false;
            destItemData = null;
        }
    }
    //endregion
}
