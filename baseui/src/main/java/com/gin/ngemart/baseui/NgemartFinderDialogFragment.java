package com.gin.ngemart.baseui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gin.ngemart.baseui.component.NgemartSimpleListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 3/28/2018.
 */

public class NgemartFinderDialogFragment<T> extends NgemartDialogFragment {
    NgemartFinderProcessor<T> listener;
    BridgeListener<T> bridgeListener;
    NgemartSimpleListView<T> listView;
    List<T> listData = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bridgeListener = (BridgeListener<T>) context;
        listener = bridgeListener.getListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        ThreadStart2(new NgemartActivity.ThreadHandler2<List<T>>(getFragmentActivity()) {
            @Override
            public void onPrepare() throws Exception {
                super.onPrepare();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                getFragmentActivity().showErrorDialog(e);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public List<T> onBackground() throws Exception {
                return listener.getData(getTag());
            }

            @Override
            public void onSuccess(List<T> data) throws Exception {
                super.onSuccess(data);
                listData.clear();
                listData.addAll(data);
                listView.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.ngemart_finder_dialog, null);
            TextView tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
            tvDialogTitle.setText(listener.getDialogTitle(getTag()));

            listView = view.findViewById(R.id.simple_list_view_finder);
            progressBar = view.findViewById(R.id.progressbar);
            listView.initiate(listData,listener.getFieldTitle(getTag()),listener.getFieldSubTitle(getTag()),listener.getFieldImage(getTag()));
            listView.notifyDataSetChanged();
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                listener.onSelected(listData.get(position), getTag());
                dismiss();
            });

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return alertDialog;
        }catch (Exception e) {
            getFragmentActivity().showErrorSnackBar(e);
            return  null;
        }
    }
}
