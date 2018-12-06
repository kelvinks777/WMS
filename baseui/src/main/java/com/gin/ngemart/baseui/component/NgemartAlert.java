package com.gin.ngemart.baseui.component;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.StackTraceHandler;
import com.bosnet.ngemart.libgen.ValidateException;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.R;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by luis on 2/6/2017.
 * Purpose : Component Alert for all Ngemart Object
 */

public class NgemartAlert {
    private NgemartActivity activity;

    private static ErrorListener errorListener;
    public static void setListener(ErrorListener listener){
        if (errorListener == null)
            errorListener = listener;
    }
    public NgemartAlert(NgemartActivity activity) {
        this.activity = activity;
    }

    public void showInfoDialog(String title, String message, DialogInterface.OnClickListener listenerOk) {
        final AlertDialog.Builder commonDialogBuilder = new AlertDialog.Builder(activity);
        commonDialogBuilder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.positive_ok), listenerOk);
        AlertDialog dialog = commonDialogBuilder.create();
        dialog.show();
    }

    public void showInfoDialog(String title, String message) {
        showInfoDialog(title, message, null);
    }

    public void showErrorDialog(String title, String message, final Exception e, DialogInterface.OnClickListener listenerOk) {
        try {
            if (errorListener != null) {
                if (!(e instanceof ValidateException)){
                    errorListener.onError(e);
                }
            }

            final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(activity);
            errorDialogBuilder.setCancelable(false)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(activity.getString(R.string.positive_ok), listenerOk)
            .setNeutralButton(activity.getString(R.string.detail_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showInfoDialog(activity.getString(R.string.detail_error_title), StackTraceHandler.getStacktrace(e));
                }
            });
            AlertDialog dialog = errorDialogBuilder.create();
            dialog.show();
        } catch (Exception ex) {
            showErrorSnackBar(ex);
        }
    }

    public void showErrorDialog(String title, String message, Exception e) {
        showErrorDialog(title, message, e, null);
    }

    public void showErrorDialog(Exception e) {
        showErrorDialog(activity.getString(R.string.error_title), e.getMessage(), e, null);
    }

    public void showErrorDialog(Exception e, DialogInterface.OnClickListener listenerOk) {
        final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(activity);
        errorDialogBuilder.setCancelable(false)
                .setTitle(activity.getString(R.string.error_title))
                .setMessage(e.getMessage())
                .setPositiveButton(R.string.positive_ok, listenerOk);
        AlertDialog dialog = errorDialogBuilder.create();
        dialog.show();
    }

    public void showAskDialog(String title, String message, DialogInterface.OnClickListener listenerOk, DialogInterface.OnClickListener listenerCancel, String strButtonOK, String strButtonCancel) {
        final AlertDialog.Builder commonDialogBuilder = new AlertDialog.Builder(activity);
        String buttonOK = strButtonOK == null ? activity.getResources().getString(R.string.positive_yes) : strButtonOK;
        String buttonCancel = strButtonCancel == null ? activity.getResources().getString(R.string.negative_no) : strButtonCancel;
        commonDialogBuilder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonOK, listenerOk)
                .setNegativeButton(buttonCancel, listenerCancel);
        AlertDialog dialog = commonDialogBuilder.create();
        dialog.show();
    }

    public void showAlertToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public void showInfoSnackBar(String message) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        final Snackbar snackbar = Snackbar
                .make(viewGroup, message, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });


        snackbar.show();
    }

    public void showErrorSnackBar(String message) {

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        final Snackbar snackbar = Snackbar
                .make(viewGroup, message, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackView.setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public void showErrorSnackBar(final Exception e) {
        if (errorListener != null) {
            if (!(e instanceof ValidateException)){
                errorListener.onError(e);
            }
        }
        String message;
        if (e instanceof ConnectException || e instanceof UnknownHostException) {
            message = activity.getString(R.string.gagal_koneksi);
        } else {
            message = activity.getString(R.string.error_title) + " , Ada kesalahan pada aplikasi Ngemart.";
        }
        showErrorSnackBar(message, e);
    }

    public void showErrorSnackBar(String msg,final Exception e) {
        if (errorListener != null) {
            if (!(e instanceof ValidateException)){
                errorListener.onError(e);
            }
        }
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        final Snackbar snackbar = Snackbar
                .make(viewGroup, msg, Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.detail_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(activity.getString(R.string.detail_error_title), StackTraceHandler.getStacktrace(e));
            }
        }).setActionTextColor(Color.YELLOW);

        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(10);
        snackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackView.setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public void showInformationToast(String messageInfo) {
        Toast.makeText(activity, messageInfo, Toast.LENGTH_SHORT).show();
    }

    public void showUpdateInfoDialog(String title, String message, DialogInterface.OnClickListener listenerOk) {
        final AlertDialog.Builder commonDialogBuilder = new AlertDialog.Builder(activity);
        commonDialogBuilder.setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.positive_update), listenerOk);
        AlertDialog dialog = commonDialogBuilder.create();
        dialog.show();
    }


}
