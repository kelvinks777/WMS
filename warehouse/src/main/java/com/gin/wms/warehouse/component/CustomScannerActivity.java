package com.gin.wms.warehouse.component;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bosnet.ngemart.libgen.barcode_scanner.CaptureManager;
import com.bosnet.ngemart.libgen.barcode_scanner.DecoratedBarcodeView;
import com.bosnet.ngemart.libgen.barcode_scanner.client.AmbientLightListener;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

/**
 * Created by manbaul on 3/26/2018.
 */

public class CustomScannerActivity extends WarehouseActivity implements AmbientLightListener.IAmbientLightListener {

    private final String TAG = "BarcodeScanner";
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button btnEnableFlashLight;
    private boolean isFlashLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbCustomScanner);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        InitComponent();
        AmbientLightListener.ChangeTorchListener torchListener = new AmbientLightListener.ChangeTorchListener();
        torchListener.addChangeTorchListener(this);
    }

    private void InitComponent() {
        btnEnableFlashLight = (Button) findViewById(R.id.btnEnableFlashLight);
        btnEnableFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlashLightOn) {
                    SetFlashLightOn();
                } else {
                    SetFlashLightOff();
                    LogButtonClickEvent(TAG + "_BtnFlashLightOn_Click");
                }
            }
        });

        FrameLayout btnFlashLightContainer = (FrameLayout) findViewById(R.id.btnFlashLightContainer);

        if (!isFlashLightOn)
            btnEnableFlashLight.setText(R.string.btn_flashlight_on_label);

        if (!HasFlash()) {
            btnEnableFlashLight.setVisibility(View.INVISIBLE);
            btnFlashLightContainer.setVisibility(View.INVISIBLE);
        }

    }

    private boolean HasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void SetFlashLightOn() {
        btnEnableFlashLight.setText(R.string.btn_flashlight_on_label);
        barcodeScannerView.setTorchOff();
        isFlashLightOn = false;
    }

    private void SetFlashLightOff() {
        btnEnableFlashLight.setText(R.string.btn_flashlight_off_label);
        barcodeScannerView.setTorchOn();
        isFlashLightOn = true;
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            capture.onResume();
        } catch (Exception e) {
            showErrorSnackBar(e);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            capture.onPause();
        } catch (Exception e) {
            showErrorSnackBar(e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            capture.onDestroy();
        } catch (Exception e) {
            showErrorSnackBar(e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            capture.onSaveInstanceState(outState);
        } catch (Exception e) {
            showErrorSnackBar(e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            onBackPressed();
            return true;
        } catch (Exception e) {
            showErrorSnackBar(e);
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    SetFlashLightOn();
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    SetFlashLightOff();
                    break;
            }
            return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        } catch (Exception e) {
            showErrorSnackBar(e);
        }

        return false;
    }

    @Override
    public void OnTorchChangeTurnOn(boolean isTorchOn) {
        try {
            if (isTorchOn) {
                btnEnableFlashLight.setText(R.string.btn_flashlight_off_label);
            } else {
                btnEnableFlashLight.setText(R.string.btn_flashlight_on_label);
            }
        } catch (Exception e) {
            showErrorSnackBar(e);
        }
    }
}
