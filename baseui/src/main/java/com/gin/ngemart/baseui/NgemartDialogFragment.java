package com.gin.ngemart.baseui;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gin.ngemart.baseui.component.NgemartAlert;
/**
 * Created by luis on 4/13/2017.
 * Purpose :
 */

public abstract class NgemartDialogFragment extends DialogFragment {
    public NgemartActivity activity;
    public NgemartAlert ngemartAlert;

    public NgemartDialogFragment(){

    }

    @Override
    public Context getContext() {
        return activity;
    }

    public Context getFragmentContext() {
        return activity.getApplicationContext();
    }

    public NgemartActivity getFragmentActivity() {
        return activity;
    }

    public <T> void ThreadStart(final NgemartActivity.ThreadHandler<T> handler) {
        activity.ThreadStart(handler);
    }

    public <T> void ThreadStart2(final NgemartActivity.ThreadHandler2<T> handler2) {
        activity.ThreadStart2(handler2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NgemartActivity) context;
        ngemartAlert = new NgemartAlert(activity);
    }

    public void ShowErrorDialog(Exception e) {
        ngemartAlert.showErrorSnackBar(e);
    }

    protected void setBeAbleToCanceled(AlertDialog alertDialog, boolean bCancelable) {
        setCancelable(bCancelable);
        alertDialog.setCanceledOnTouchOutside(bCancelable);
    }

}
