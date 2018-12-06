package com.gin.wms.warehouse.security;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.NgemartAdapterCommon;
import com.gin.wms.manager.db.data.VehicleTypeData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import android.support.v4.app.DialogFragment;

import java.util.List;

/**
 * Created by bintang on 4/3/2018.
 */

public class VehicleTypeDialogFragment extends DialogFragment implements DialogInterface.OnShowListener{
    private static List<VehicleTypeData> vehicleTypeDataList;
    private IVehicleTypeDialog listener;
    private ListView lvVehicleType;
    private AlertDialog alertDialog;
    private Context context;

    public static VehicleTypeDialogFragment newInstance(List<VehicleTypeData> dataList) throws Exception {
        vehicleTypeDataList = dataList;
        return new VehicleTypeDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        try{
            super.onAttach(context);
            this.context = context;
            listener = (IVehicleTypeDialog) context;
        }catch (Exception e){
            NgemartActivity activity = (NgemartActivity) context;
            activity.showErrorSnackBar(e);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try{
            View view = initComponent();
            alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
            alertDialog.setOnShowListener(this);
            setCancelable(true);
        }catch (Exception e){
            ((WarehouseActivity)context).showErrorSnackBar(e);
        }

        return alertDialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        lvVehicleType.setOnItemClickListener((parent, view, position, id) -> {
            try{
                listener.onTypeSelected(vehicleTypeDataList.get(position));
                alertDialog.dismiss();
            }catch (Exception e){
                ((WarehouseActivity)context).showErrorSnackBar(e);
            }
        });
    }

    View initComponent(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_vehicle_type, null);
        lvVehicleType = view.findViewById(R.id.lvVehicleType);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.choose_type));
        NgemartAdapterCommon<VehicleTypeData> adapter = new NgemartAdapterCommon<>(context, android.R.layout.simple_list_item_1, vehicleTypeDataList, new NgemartAdapterCommon.INgemartAdapterHandler<VehicleTypeData>() {
            @Override
            public View onGetView(VehicleTypeData data, View detailView) throws Exception {
                initDetailComponent(detailView, data);
                return detailView;
            }

            private void initDetailComponent(View detailView, VehicleTypeData data) throws Exception {
                TextView tvType = detailView.findViewById(android.R.id.text1);
                tvType.setText(data.name);
            }
        });

        lvVehicleType.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    public interface IVehicleTypeDialog {
        void onTypeSelected(VehicleTypeData vehicleType);
    }

}
