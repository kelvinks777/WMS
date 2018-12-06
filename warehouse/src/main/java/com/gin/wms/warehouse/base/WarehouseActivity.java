package com.gin.wms.warehouse.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.bosnet.ngemart.libgen.GsonMapper;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.wms.manager.DeviceTokenManager;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.component.CustomScannerActivity;
import com.gin.wms.warehouse.service.CountdownService;

import static com.gin.ngemart.baseui.fcm.InstanceIDService.GetDeviceToken;

/**
 * Created by manbaul on 2/19/2018.
 */

public class WarehouseActivity extends NgemartActivity {

    @Override
    public WarehouseAppContext getApplicationContext(){
        return (WarehouseAppContext) super.getApplicationContext();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void startActivity(Class activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
    }

    public void sendDeviceTokenToServer() throws Exception {
        new Thread(() -> {
            try {
                String deviceToken = GetDeviceToken(getString(R.string.fcmSenderId));
                if (deviceToken != null) {
                    DeviceTokenManager deviceTokenManager = new DeviceTokenManager(getApplicationContext());
                    deviceTokenManager.SendToken(deviceToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private BroadcastReceiver countdownBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onFinishCountdownService();
        }
    };

    protected void onFinishCountdownService() { }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countdownBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(CountdownService.COUNTDOWN_SERVICE_FINISH);
        LocalBroadcastManager.getInstance(this).registerReceiver(countdownBroadcastReceiver, intentFilter);
    }

    private GsonMapper gsonMapper = null;
    public GsonMapper getGsonMapper() throws Exception {
        if (gsonMapper==null)
            gsonMapper = new GsonMapper();

        return gsonMapper;
    }


    public void LaunchBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        initateScan(integrator);
    }

    public void LaunchBarcodeScanner(int requestCode) {
        IntentIntegrator integrator = new IntentIntegrator(this, requestCode);
        initateScan(integrator);
    }

    private void initateScan(IntentIntegrator integrator) {
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan Barcode");
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }
}
