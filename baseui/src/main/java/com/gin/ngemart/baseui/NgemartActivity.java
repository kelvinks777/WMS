package com.gin.ngemart.baseui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gin.ngemart.baseui.component.NgemartAlert;
import com.gin.ngemart.baseui.component.NgemartFirebaseAnalytics;
import com.gin.ngemart.baseui.receiver.NetworkChangeReceiver;
import com.gin.ngemart.baseui.receiver.NetworkUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by luis on 3/21/2016.
 * Purpose : Base ngemart activity for Delman, Point and Shop.
 */
public abstract class NgemartActivity extends AppCompatActivity {
    protected final int REQ_CODE_PERMISSION = 1000;
    protected final int ACCESS_LOCATION_REQ_CODE = 1001;
    protected final int CALL_PHONE_REQ_CODE = 1002;
    protected final int CAMERA_REQ_CODE = 1003;
    private boolean isExternalBarcodeActive = false;
    public ProgressDialog progressDialog;
    NgemartAlert ngemartAlert = new NgemartAlert(this);
    private boolean isInternetAvailable = true;
    private NgemartFirebaseAnalytics ngemartFirebaseAnalytics;
    private View.OnClickListener submitClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            OnClickFabButton(view);
        }
    };
    private BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int status = NetworkUtil.getConnectivityStatus(context);
                String strStatus = NetworkUtil.getConnectivityStatusString(context);
                OnNetworkConnectivityChange(status, strStatus);
                if (NetworkUtil.isConnected(context)) {
                    OnConnectedToInternet();
                } else {
                    OnDisconnectedFromInternet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected boolean IsNetworkConnected() {
        String currentConnectionStatus = NetworkUtil.getConnectivityStatusString(getBaseContext());
        return !currentConnectionStatus.trim().contains("Not");
    }

    public void ToggleSoftInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);
    }

    public void HideSoftInputKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void RequestCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PHONE_REQ_CODE);
        } else {
            OnPermissionCallPhoneGranted();
        }
    }

    protected void OnPermissionCallPhoneGranted() {
    }

    protected void OnPermissionCallPhoneDenied() {
    }


    protected void RequestAccessLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQ_CODE);
        } else {
            OnPermissionAccessLocationGranted();
        }
    }

    protected void OnPermissionAccessLocationGranted() {
    }

    protected void OnPermissionAccessLocationDenied() {
    }

    protected void RequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_PERMISSION);
        } else {
            OnPermissionStorageGranted();
        }
    }

    protected void OnPermissionStorageGranted() {
    }

    protected void OnPermissionStorageDenied() {
    }

    protected void RequestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQ_CODE);
        } else {
            OnPermissionCameraGranted();
        }
    }

    protected  void OnPermissionCameraGranted(){

    }

    protected  void OnPermissionCameraDenied(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case REQ_CODE_PERMISSION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        OnPermissionStorageGranted();
                    else
                        OnPermissionStorageDenied();

                    break;
                case ACCESS_LOCATION_REQ_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        OnPermissionAccessLocationGranted();
                    else
                        OnPermissionAccessLocationDenied();

                    break;
                case CALL_PHONE_REQ_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        OnPermissionCallPhoneGranted();
                    else
                        OnPermissionCallPhoneDenied();

                    break;
                case CAMERA_REQ_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        OnPermissionCameraGranted();
                    else
                        OnPermissionCameraDenied();

                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setAnimationSideTransition();
        ngemartFirebaseAnalytics = new NgemartFirebaseAnalytics(this);
        super.onCreate(savedInstanceState);
    }

    public void showUpdateInfoDialog(String title, String message, DialogInterface.OnClickListener listenerOk) {
        ngemartAlert.showUpdateInfoDialog(title, message, listenerOk);
    }

    public void showInfoDialog(String title, String message, DialogInterface.OnClickListener listenerOk) {
        ngemartAlert.showInfoDialog(title, message, listenerOk);
    }

    public void showInfoDialog(String title, String message) {
        showInfoDialog(title, message, null);
    }

    public void showErrorDialog(String title, String message, final Exception e, DialogInterface.OnClickListener listenerOk) {
        ngemartAlert.showErrorDialog(title, message, e, listenerOk);
    }

    public void showErrorDialog(String title, String message, Exception e) {
        showErrorDialog(title, message, e, null);
    }


    public void showErrorDialog(Exception e) {
        showErrorDialog(getString(R.string.error_title), e.getMessage(), e, null);
    }

    public void showErrorDialog(String msg) {
        showInfoDialog(getString(R.string.error_title), msg);
    }

    public void showErrorDialog(String msg, Exception e) {
        showErrorDialog(getString(R.string.error_title), msg, e, null);
    }

    public void showErrorDialog(Exception e, DialogInterface.OnClickListener listenerOk) {
        ngemartAlert.showErrorDialog(e, listenerOk);
    }

    public void showAskDialog(String title, String message, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel) {
        ngemartAlert.showAskDialog(title, message, listenerOk, listenerCancel, null, null);
    }

    public void showAskDialog(String title, String message, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel, String strButtonOK, String strButtonCancel) {
        ngemartAlert.showAskDialog(title, message, listenerOk, listenerCancel, strButtonOK, strButtonCancel);
    }

    public void showAlertToast(String message) {
        ngemartAlert.showAlertToast(message);
    }

    public void showInfoSnackBar(String message) {
        ngemartAlert.showInfoSnackBar(message);
    }

    public void showErrorSnackBar(String message) {
        ngemartAlert.showErrorSnackBar(message);
    }

    public void showErrorSnackBar(String message, Exception e) {
        ngemartAlert.showErrorSnackBar(message, e);
    }

    public void showErrorSnackBar(final Exception e) {
        ngemartAlert.showErrorSnackBar(e);
    }

    public void showInformationToast(String messageInfo) {
        Toast.makeText(getBaseContext(), messageInfo, Toast.LENGTH_SHORT).show();
    }

    public <T> void ThreadStart(final ThreadHandler<T> handler) {
        new Thread(new NgemartRunnable<T>(this, handler)).start();
    }

    public <T> void ThreadStart(final ThreadHandler<T> handler, Boolean catchAllException) {
        new Thread(new NgemartRunnable<T>(this, handler, catchAllException)).start();
    }

    public <T> void ThreadStart2(final ThreadHandler2<T> handler) {
        new Thread(new NgemartRunnable2<T>(this, handler)).start();
    }

    public <T> void ThreadStart2(final ThreadHandler2<T> handler, Boolean catchAllException) {
        new Thread(new NgemartRunnable2<T>(this, handler, catchAllException)).start();
    }

    public void startProgressDialog(String message, int type) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(type);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected void setFAB(int FABPosition, Drawable icon, int color, int rippleColor) {
        final android.support.design.widget.FloatingActionButton fab;
        FrameLayout.LayoutParams params;
        int size;
        float scale;

        scale = this.getResources().getDisplayMetrics().density;
        size = convertToPixels(56, scale);
        params = new FrameLayout.LayoutParams(size, size);
        params.gravity = FABPosition;
        params.rightMargin = 15;
        params.leftMargin = 15;
        params.topMargin = 15;
        params.bottomMargin = 15;

        fab = new android.support.design.widget.FloatingActionButton(this);
        fab.setImageDrawable(icon);
        fab.setMaxHeight(56);
        fab.setMaxWidth(56);
        fab.setOnClickListener(submitClickListener);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        fab.setRippleColor(rippleColor);
        addContentView(fab, params);

    }

    public void setSubmitFAB(int FABPosition) {
        setFAB(FABPosition, ContextCompat.getDrawable(this, R.drawable.ic_assignment), Color.rgb(59, 89, 152), Color.rgb(00, 132, 255));
    }

    protected void OnClickFabButton(View view) {
        Log.d("NgemartActivity", "OnClickFabButton: Clicked");
    }

    private int convertToPixels(int dp, float scale) {
        return (int) (dp * scale + 0.5f);
    }

    public void setAnimationSideTransition() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void setAnimationSideTransitionOut() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void DismissActivity() {
        finish();
        setAnimationSideTransitionOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setAnimationSideTransitionOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                setAnimationSideTransitionOut();
                break;
        }
        return true;
    }

    public <T> void setRefreshListenerStart(final SwipeRefreshLayout swipeRefreshLayout, final RefreshLayoutHandler<T> handler) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ThreadStart(new ThreadHandler<T>() {
                    @Override
                    public void onPrepare() {
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public T onBackground() throws Exception {
                        return handler.OnSwipeRefresh(swipeRefreshLayout);
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.OnSwipeError(swipeRefreshLayout, e);
                    }

                    @Override
                    public void onSuccess(T data) {
                        handler.OnSwipeFinish(swipeRefreshLayout, data);
                    }

                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(networkBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.NETWORK_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(networkBroadcastReceiver, intentFilter);
    }

    public void OnNetworkConnectivityChange(int status, String strStatus) {
    }

    public void OnConnectedToInternet() {
        isInternetAvailable = true;
    }

    public void OnDisconnectedFromInternet() {
        isInternetAvailable = false;
    }

    public boolean isInternetAvailable() {
        return isInternetAvailable;
    }

    public void LogButtonClickEvent(String eventName) {
        ngemartFirebaseAnalytics.LogButtonClickEvent(eventName);
    }

    public void LogCheckoutEvent(String orderId, String currency, int totalOrder) {
        ngemartFirebaseAnalytics.LogCheckoutEvent(orderId, currency, totalOrder);
    }

    public interface ThreadHandler<T> {

        void onPrepare() throws Exception;

        T onBackground() throws Exception;

        void onError(Exception e);

        void onSuccess(T data) throws Exception;

        void onFinish();
    }

    public interface RefreshLayoutHandler<T> {
        T OnSwipeRefresh(SwipeRefreshLayout sender) throws Exception;

        void OnSwipeFinish(SwipeRefreshLayout sender, T data);

        void OnSwipeError(SwipeRefreshLayout sender, Exception e);
    }

    static public abstract class ThreadHandler2<T> {
        NgemartAlert ngemartAlert;
        NgemartActivity activity;
        Boolean isNeedShowProgressbar = false;
        String textProgress = "Sedang memproses data";

        public ThreadHandler2(NgemartActivity activity) {
            this.activity = activity;
            ngemartAlert = new NgemartAlert(this.activity);
        }

        public ThreadHandler2(NgemartActivity activity, Boolean isNeedShowProgressbar) {
            this.activity = activity;
            ngemartAlert = new NgemartAlert(this.activity);
            this.isNeedShowProgressbar = isNeedShowProgressbar;
        }

        public ThreadHandler2(NgemartActivity activity, Boolean isNeedShowProgressbar, String textProgress) {
            this.activity = activity;
            ngemartAlert = new NgemartAlert(this.activity);
            this.isNeedShowProgressbar = isNeedShowProgressbar;
            this.textProgress = textProgress;
        }

        public void onPrepare() throws Exception {
            if (isNeedShowProgressbar)
                activity.startProgressDialog(textProgress, ProgressType.SPINNER);
        }

        public abstract T onBackground() throws Exception;

        public void onError(Exception e) {
            ngemartAlert.showErrorDialog(e);
        }

        public void onSuccess(T data) throws Exception {

        }

        public void onFinish() {
            if (isNeedShowProgressbar)
                activity.dismissProgressDialog();
        }
    }

    public class ProgressType {
        public final static int SPINNER = ProgressDialog.STYLE_SPINNER;
        public final static int NORMAL = ProgressDialog.STYLE_HORIZONTAL;

    }

    String barcode = "";
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            if(isExternalBarcodeActive) {
                //barcode scanner
                int c = event.getUnicodeChar();
                //accept only 0..9 and ENTER
                if ((c >= 48 && c <= 57) || c == 10) {
                    if (event.getAction() == 0) {
                        if (c >= 48 && c <= 57)
                            barcode += "" + (char) c;
                        else {
                            if (!barcode.equals("")) {
                                final String resultBarcode = barcode;
                                barcode = "";
                                getExternalBarcodeResult(resultBarcode);
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
        return super.dispatchKeyEvent(event);
    }

    protected void getExternalBarcodeResult(String barcode) {

    }

    public interface IMapCaller {
        void onMapMarkerClick(Marker marker, Location agentLocation);
    }

    public interface IMapInteraction {
        void onMapMoveInteraction(boolean mapIsMoving, Location currentLoc,Location cameraLoc ,String zipCode, double radius);
    }

    public void setExternalBarcodeActive(boolean isActive) {
        this.isExternalBarcodeActive = isActive;
    }


    public class FABPosition {
        public final static int UP_LEFT = Gravity.TOP | Gravity.START;
        public final static int UP_CENTER = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        public final static int UP_RIGHT = Gravity.TOP | Gravity.END;
        public final static int MIDDLE_LEFT = Gravity.CENTER_VERTICAL | Gravity.START;
        public final static int MIDDLE_CENTER = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        public final static int MIDDLE_RIGHT = Gravity.CENTER_VERTICAL | Gravity.END;
        public final static int BOTTOM_LEFT = Gravity.BOTTOM | Gravity.START;
        public final static int BOTTOM_CENTER = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        public final static int BOTTOM_RIGHT = Gravity.BOTTOM | Gravity.END;
    }
}
