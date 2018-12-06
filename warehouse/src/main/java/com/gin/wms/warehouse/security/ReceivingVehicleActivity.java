package com.gin.wms.warehouse.security;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.Common;
import com.bosnet.ngemart.libgen.Data;
import com.gin.ngemart.baseui.BridgeListener;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.NgemartFinderDialogFragment;
import com.gin.ngemart.baseui.NgemartFinderProcessor;
import com.gin.wms.manager.ParkingAreaManager;
import com.gin.wms.manager.ReceivingVehicleManager;
import com.gin.wms.manager.VehicleManager;
import com.gin.wms.manager.TokenLocalManager;
import com.gin.wms.manager.db.data.UserData;
import com.gin.wms.manager.db.data.VehicleCategoryData;
import com.gin.wms.manager.db.data.VehicleData;
import com.gin.wms.manager.db.data.VehicleTypeData;
import com.gin.wms.manager.db.data.enums.VehicleEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.LeftMenuModule;
import com.gin.wms.manager.UserManager;
import com.gin.wms.warehouse.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ReceivingVehicleActivity extends WarehouseActivity
        implements LeftMenuModule.ILeftMenu, View.OnClickListener,
        BridgeListener<Data>{
    private CardView cardNote;
    private TextView txtVehicleType, txtVehicleCategory1, txtVehicleCategory2;
    private EditText txtPoliceNumber, txtPoliceNumber2, txtPoliceNumber3, txtDriverName;
    private ImageButton imgBtnVehicleType, imgBtnVehicleCategory1, imgBtnVehicleCategory2;
    private ProgressBar spinner;
    private VehicleManager vehicleManager;
    private ParkingAreaManager parkingAreaManager;
    private ReceivingVehicleManager receivingVehicleManager;
    private Button btnProcess, btnCheck;
    private LinearLayout descriptionLayout;
    private LeftMenuModule leftMenuModule;
    private Toolbar toolbar;
    private UserManager userManager;
    private UserData userData;
    private TokenLocalManager tokenLocalManager;
    private VehicleData vehicleData;
    private VehicleData previousVehicleData;
    private String previousDriverName;
    private String selectedType;
    private String selectedCategory1;
    private String selectedCategory2;
    private String lokasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       try{
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_rcv_vehicle_main);
           initManager();
           initObject();
           initComponent();
       }catch (Exception e){
           showErrorDialog(e);
       }
    }

    @Override
    public boolean onMenuItemSelected(int itemId) throws Exception {
        switch (itemId){
            case R.id.nav_security_logout:
                showAskDialog(getString(R.string.common_confirmation_title),
                        getString(R.string.common_close_app_confirmation_content),
                        (dialog, which) -> doLogoutThread(),
                        (dialog, which) -> dialog.dismiss());
                break;
        }
        return false;
    }

    @Override
    public void onHandleBackPressed() {
        //
    }

    @Override
    public void onBackPressed() {
        doubleClickBackPressedToExit();
    }

    //region View.OnclickListener implemented functions
    @Override
    public void onClick(View v) {
        if(validatePoliceNumber()){
            HideSoftInputKeyboard();
            switch (v.getId()){
                case R.id.btnPoliceNumberCheck:
                    getVehicleDataFromServerThread(justGetPoliceNumberText());
                    break;
                case R.id.btnProcess:
                    if (validatedVehicleDataIsCompleted()){
                        VehicleData dataToSave = prepareVehicleData();
                        String driverName = txtDriverName.getText().toString();
                    //    justGetVehicleDataThread(dataToSave.policeNumber);
                        justSaveVehicleDataThread(dataToSave, driverName);
                    }
                    break;
                case R.id.imgBtnVehicleType:
                    showVehicleCategoryListFragment(VehicleEnum.TYPE.getValue());
                    break;
                case R.id.imgBtnVehicleCategory1:
                    showVehicleCategoryListFragment(VehicleEnum.CATEGORY_1.getValue());
                    break;
                case R.id.imgBtnVehicleCategory2:
                    showVehicleCategoryListFragment(VehicleEnum.CATEGORY_2.getValue());
                    break;
            }
        }
    }
    //endregion

    //region Init

    private void resetUi(){
        this.vehicleData = null;
        txtPoliceNumber.requestFocus();
        clearText();
        setDropDownButtonEnable(false);
    }

    private void clearText(){
        clearVehicleDescriptionViews();
        txtPoliceNumber.setText("");
        txtPoliceNumber2.setText("");
        txtPoliceNumber3.setText("");
        txtDriverName.setText("");
    }

    private void setDropDownButtonEnable(boolean enable){
        if(!enable){
            imgBtnVehicleType.setEnabled(false);
            imgBtnVehicleCategory1.setEnabled(false);
            imgBtnVehicleCategory2.setEnabled(false);
            imgBtnVehicleType.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDimGrey));
            imgBtnVehicleCategory1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDimGrey));
            imgBtnVehicleCategory2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDimGrey));
        }else{
            imgBtnVehicleType.setEnabled(true);
            imgBtnVehicleCategory1.setEnabled(true);
            imgBtnVehicleCategory2.setEnabled(true);
            imgBtnVehicleType.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            imgBtnVehicleCategory1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            imgBtnVehicleCategory2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    private void initComponent(){
        initToolbar();
        initLeftModuleComponent();
        justBindingProperties();
        initPropertyHandler();
        resetUi();
    }

    private void initObject()throws Exception{
        userData = userManager.GetUserDataFromLocal();
    }

    private void initManager() throws Exception{
        userManager = new UserManager(getApplicationContext());
        tokenLocalManager = new TokenLocalManager(getApplicationContext());
        vehicleManager = new VehicleManager(getApplicationContext());
        receivingVehicleManager = new ReceivingVehicleManager(getApplicationContext());
        parkingAreaManager=new ParkingAreaManager(getApplicationContext());
    }

    private void initLeftModuleComponent(){
        leftMenuModule = new LeftMenuModule(this, toolbar);
        leftMenuModule.setLeftMenuListener(this);
        leftMenuModule.setAppVersion(Common.GetVersionInfo(this));
        leftMenuModule.setProfileInfo(null);
        setProfileImage();
    }

    private void justBindingProperties(){
        descriptionLayout = findViewById(R.id.descriptionLayout);
        btnProcess = findViewById(R.id.btnProcess);
        btnCheck = findViewById(R.id.btnPoliceNumberCheck);
        cardNote = findViewById(R.id.cardVehicleReceivingNote);
        cardNote.setVisibility(View.GONE);
        txtPoliceNumber = findViewById(R.id.txtPoliceNumber);
        txtPoliceNumber2 = findViewById(R.id.txtPoliceNumber2);
        txtPoliceNumber3 = findViewById(R.id.txtPoliceNumber3);
        txtDriverName = findViewById(R.id.txtDriverName);
        txtVehicleType = findViewById(R.id.txtVehicleType);
        txtVehicleCategory1 = findViewById(R.id.txtVehicleCategory1);
        txtVehicleCategory2 = findViewById(R.id.txtVehicleCategory2);
        imgBtnVehicleType = findViewById(R.id.imgBtnVehicleType);
        imgBtnVehicleCategory1 = findViewById(R.id.imgBtnVehicleCategory1);
        imgBtnVehicleCategory2 = findViewById(R.id.imgBtnVehicleCategory2);
        spinner = findViewById(R.id.spinnerReceivingVehicle);
        lokasi="";
        removeFocus();
        setSpinnerVisibility(false);
    }

    private void initPropertyHandler(){
        imgBtnVehicleType.setOnClickListener(this);
        imgBtnVehicleCategory1.setOnClickListener(this);
        imgBtnVehicleCategory2.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
    }

    private void setSpinnerVisibility(boolean needToShow){
        if(needToShow) {
            if (spinner.getVisibility() == View.GONE)
                spinner.setVisibility(View.VISIBLE);
        }else{
            if(spinner.getVisibility() == View.VISIBLE)
                spinner.setVisibility(View.GONE);
        }
    }

    private void removeFocus(){
        txtPoliceNumber.clearFocus();
        txtPoliceNumber2.clearFocus();
        txtPoliceNumber3.clearFocus();
        txtVehicleType.clearFocus();
        txtVehicleCategory1.clearFocus();
        txtVehicleCategory2.clearFocus();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarMainSecurity);
        toolbar.setTitle(getResources().getString(R.string.receiving_vehicle_title));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setProfileImage(){
        if (!userData.profileImage.equals("")) {
            leftMenuModule.setProfileImage(userData.profileImage);
        }else{
            ThreadStart(new ThreadHandler<String>() {
                @Override
                public void onPrepare() throws Exception {

                }

                @Override
                public String onBackground() throws Exception {
                    userData = userManager.GetUserDataFromLocal();
                    if(userData != null)
                        return userData.profileImage;
                    else
                        return "";
                }

                @Override
                public void onError(Exception e) {
                    showErrorSnackBar(e);
                }

                @Override
                public void onSuccess(String strImage) throws Exception {
                    leftMenuModule.setProfileImage(strImage);
                }

                @Override
                public void onFinish() {

                }
            });
        }
    }

    //endregion

    //region Present Data to UI

    private void clearVehicleDescriptionViews(){
        txtVehicleType.setError(null);
        txtVehicleCategory1.setError(null);
        txtVehicleCategory2.setError(null);

        txtVehicleType.setText("");
        txtVehicleCategory1.setText("");
        txtVehicleCategory2.setText("");
    }

    private void setDataToUI(VehicleData vehicleData){
        try{
            clearVehicleDescriptionViews();
            if(vehicleData != null){
                cardNote.setVisibility(View.GONE);
                String vehicleTypeName = getVehicleTypeName(vehicleData.type);
                String category1_name = getVehicleCategoryName(vehicleData.category1);
                String category2_name = getVehicleCategoryName(vehicleData.category2);

                txtVehicleType.setText(vehicleTypeName);
                txtVehicleCategory1.setText(category1_name);
                txtVehicleCategory2.setText(category2_name);
            }else{
                cardNote.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    private String justGetPoliceNumberText() {
        StringBuilder sb = new StringBuilder();
        String strFront = txtPoliceNumber.getText().toString().trim();
        String strNumber = txtPoliceNumber2.getText().toString().trim();
        String strRear = txtPoliceNumber3.getText().toString().trim();
        sb.append(strFront);
        sb.append(strNumber);
        sb.append(strRear);

        return sb.toString();
    }

    private boolean validatePoliceNumber(){
        String warningText = getResources().getString(R.string.warning_data_should_be_filled);

        if(txtPoliceNumber.getText().toString().equals("")){
            txtPoliceNumber.setError(warningText);
            return false;
        }

        if(txtPoliceNumber2.getText().toString().equals("")){
            txtPoliceNumber2.setError(warningText);
            return false;
        }

        if(txtPoliceNumber3.getText().toString().equals("")){
            txtPoliceNumber3.setError(warningText);
            return false;
        }

        return true;
    }

    private String getVehicleTypeName(String vehicleTypeId)throws Exception{
        VehicleTypeData data = vehicleManager.getVehicleTypeDataByIdFromLocal(vehicleTypeId);
        if(data != null)
            return data.name;
        else
            return "";
    }

    private String getVehicleCategoryName(String vehicleTypeId)throws Exception{
        VehicleCategoryData data = vehicleManager.getVehicleCategoryDataByIdFromLocal(vehicleTypeId);
        if(data != null)
            return data.name;
        else
            return "";
    }

    //endregion

    //region Threads
    private void getVehicleDataFromServerThread(final String policeNumber){
        ThreadStart(new ThreadHandler<VehicleData>() {
            @Override
            public void onPrepare() throws Exception {
                setSpinnerVisibility(true);
            }

            @Override
            public VehicleData onBackground() throws Exception {
                justGetVehicleTypeList();
                justGetVehicleCategoryDataList();

                return justGetVehicleData();
            }

            @Override
            public void onError(Exception e) {
                setSpinnerVisibility(false);
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(VehicleData data) throws Exception {
                if(data != null)
                    setGlobalVariables(data);

                descriptionLayout.setVisibility(View.VISIBLE);
                setDropDownButtonEnable(true);
                setDataToUI(data);
            }

            @Override
            public void onFinish() {
                setSpinnerVisibility(false);
            }

            void setGlobalVariables(VehicleData data){
                vehicleData = data;
                selectedType = data.type;
                selectedCategory1 = data.category1;
                selectedCategory2 = data.category2;
            }

            VehicleData justGetVehicleData()throws Exception{
                VehicleData vehicleData = vehicleManager.getVehicleFromServer(policeNumber);
                if(vehicleData != null)
                    vehicleManager.saveToLocal(vehicleData);

                return vehicleData;
            }

            void justGetVehicleCategoryDataList()throws Exception{
                List<VehicleCategoryData> vehicleCategoryDataList = vehicleManager.getVehicleCategoryList();
                if(vehicleCategoryDataList.size() > 0)
                    vehicleManager.saveVehicleCategoryListToLocal(vehicleCategoryDataList);
            }

            void justGetVehicleTypeList()throws Exception{
                List<VehicleTypeData> vehicleTypeDataList = vehicleManager.getVehicleTypeListFromServer();
                if(vehicleTypeDataList.size() > 0)
                    vehicleManager.saveVehicleTypeListToLocal(vehicleTypeDataList);
            }
        });
    }

    private void doLogoutThread(){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.common_close_app_progress_content), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                tokenLocalManager.SaveNgToken("", "");
                userManager.SignOut();
                return true;
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }
        });
    }
    //endregion

    //region Functions

    //region OnBackPressed
    private boolean isPressedTwice = false;
    private void doubleClickBackPressedToExit() {
        if (isPressedTwice) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }else{
            Toast.makeText(this, getResources().getString(R.string.common_double_tap_for_close), Toast.LENGTH_SHORT).show();
        }

        isPressedTwice = true;
        new Handler().postDelayed(() -> isPressedTwice = false, 2000);
    }
    //endregion

    //region ShowDialog
    private void showVehicleCategoryListFragment(String categoryEnum){
        NgemartFinderDialogFragment<VehicleCategoryData> dialogFragment = new NgemartFinderDialogFragment<>();
        dialogFragment.show(getSupportFragmentManager(), categoryEnum);
    }
    //endregion

    //region Vehicle Process
    private boolean validatedVehicleDataIsCompleted(){
        boolean isValid = true;
        String warningText = getResources().getString(R.string.warning_data_should_be_filled);

        if(txtDriverName.getText().toString().equals("")){
            txtDriverName.setError(warningText);
            isValid = false;
        }

        if(txtVehicleType.getText().toString().equals("")){
            txtVehicleType.setError(warningText);
            isValid = false;
        }

        if(txtVehicleCategory1.getText().toString().equals("")){
            txtVehicleCategory1.setError(warningText);
            isValid = false;
        }

        if(txtVehicleCategory2.getText().toString().equals("")){
            txtVehicleCategory2.setError(warningText);
            isValid = false;
        }

        return isValid;
    }

    private boolean isCurrentEqualsPreviousSaved(VehicleData dataToSave) {
        return !(previousVehicleData == null && previousDriverName == null) && (isEqualWithPreviousSavedData(dataToSave));
    }

    private boolean isEqualWithPreviousSavedData(VehicleData dataToSave) {
        return dataToSave.policeNumber.equals(previousVehicleData.policeNumber) ||
                dataToSave.type.equals(previousVehicleData.type) ||
                dataToSave.category1.equals(previousVehicleData.category1) ||
                dataToSave.category2.equals(previousVehicleData.category2) ||
                previousDriverName.equals(txtDriverName.getText().toString());
    }

    private VehicleData prepareVehicleData(){
        VehicleData preparedData = new VehicleData();
        if(this.vehicleData != null){
            preparedData = this.vehicleData;
        }else{
            preparedData.policeNumber = justGetPoliceNumberText() ;
            preparedData.vehicleOwnerId = "";
            preparedData.workplaceId = "";
        }

        String type = selectedType;
        String category1 = selectedCategory1;
        String category2 = selectedCategory2;

        preparedData.type = type;
        preparedData.category1 = category1;
        preparedData.category2 = category2;

        return preparedData;
    }

    private void justSaveVehicleDataThread(final VehicleData vehicleData, String driverName){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.receiving_vehicle_saving), NgemartActivity.ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                receivingVehicleManager.SaveReceivingVehicleToServer(vehicleData, driverName);
                previousDriverName = driverName;
                previousVehicleData = vehicleData;
                lokasi=parkingAreaManager.GetParkingInfoFromPoliceNo(vehicleData.policeNumber);
                return true;
            }

            @Override
            public void onError(Exception e) {
                previousDriverName = null;
                previousVehicleData = null;
                progressDialog.dismiss();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showInfoSnackBar(getResources().getString(R.string.receiving_vehicle_successfully_saved));
                cardNote.setVisibility(View.GONE);
                showDialog(ReceivingVehicleActivity.this,lokasi);
                resetUi();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }
        });
    }

    /*private void justGetVehicleDataThread(String policeNo){
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.receiving_parkir_area), NgemartActivity.ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                lokasi=parkingAreaManager.GetParkingInfoFromPoliceNo(policeNo);
                return true;
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showDialog(ReceivingVehicleActivity.this,lokasi);

            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }
        });

    }
    */

    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_dialog_parking_area);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }



    @Override
    public NgemartFinderProcessor<Data> getListener() {
        return new NgemartFinderProcessor<Data>() {
            @Override
            public List<Data> getData(String Tag) throws Exception {
                List<Data> dataList = new ArrayList<>();

                if(Tag.equals(VehicleEnum.CATEGORY_1.getValue()) || Tag.equals(VehicleEnum.CATEGORY_2.getValue())){
                    List<VehicleCategoryData> vehicleCategoryDataList = vehicleManager.getVehicleCategoryListFromLocal();
                    dataList.addAll(vehicleCategoryDataList);
                }else if(Tag.equals(VehicleEnum.TYPE.getValue())){
                    List<VehicleTypeData> vehicleTypeDataList = vehicleManager.getVehicleTypeListFromLocal();
                    dataList.addAll(vehicleTypeDataList);
                }

                return dataList;
            }

            @Override
            public String getDialogTitle(String Tag){
                String titleDialog = "";
                if(Tag.equals(VehicleEnum.TYPE.getValue()))
                    titleDialog = getResources().getString(R.string.choose_type);
                else if(Tag.equals(VehicleEnum.CATEGORY_1.getValue()) || Tag.equals(VehicleEnum.CATEGORY_2.getValue()))
                    titleDialog = getResources().getString(R.string.choose_category);

                return titleDialog;
            }

            @Override
            public String getFieldTitle(String Tag) {
                return "name";
            }

            @Override
            public String getFieldSubTitle(String Tag) {
                return "";
            }

            @Override
            public String getFieldImage(String Tag) {
                return "";
            }

            @Override
            public void onSelected(Data data, String tag) {
                setSelectedData(data, tag);
            }

            void setSelectedData(Data data, String tag){
                if(tag.equals(VehicleEnum.CATEGORY_1.getValue()) ){
                    selectedCategory1 = ((VehicleCategoryData)data).vehicleCategoryId;
                    txtVehicleCategory1.setText(((VehicleCategoryData)data).name);
                }else if(tag.equals(VehicleEnum.CATEGORY_2.getValue())){
                    selectedCategory2 = ((VehicleCategoryData)data).vehicleCategoryId;
                    txtVehicleCategory2.setText(((VehicleCategoryData)data).name);
                }else if(tag.equals(VehicleEnum.TYPE.getValue())){
                    selectedType = ((VehicleTypeData)data).vehicleTypeId;
                    txtVehicleType.setText(((VehicleTypeData)data).name);
                }
            }
        };
    }
    //endregion

    //endregion
}
