package com.gin.ngemart.baseui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gin.ngemart.baseui.component.NgemartAlert;

/**
 * Created by luis on 1/17/2017.
 * Purpose : Base for all Fragment
 */

public abstract class NgemartFragment extends Fragment {
    private NgemartActivity activity;
    private NgemartAlert ngemartAlert;

    @Override
    public Context getContext() {
        return activity;
    }

    public NgemartAppContext getFragmentContext() {
        return (NgemartAppContext) activity.getApplicationContext();
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

    public void startFragment(int containerViewId, android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void ShowErrorDialog(Exception e) {
        ngemartAlert.showErrorSnackBar(e);
    }

    public void ShowInfoDialog(String informationTitle, String message, DialogInterface.OnClickListener onClickListener){
        ngemartAlert.showInfoDialog(informationTitle, message, onClickListener);
    }
}
