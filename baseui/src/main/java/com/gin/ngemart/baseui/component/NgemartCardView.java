package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.gin.ngemart.baseui.BuildConfig;
import com.gin.ngemart.baseui.NgemartActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NgemartCardView<T> extends CardView {

    protected CardListener cardListener;
    private Context context;
    protected T data;
    protected T data2;
    protected String containerName = "";
    protected NgemartAlert ngemartAlert;
    private NgemartFirebaseAnalytics ngemartFirebaseAnalytics;
    protected List<Object> params = new ArrayList<>();

    public NgemartCardView(Context context) {
        super(context);
        init(context);
    }

    public NgemartCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public NgemartCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setParameters(Object... params) {
        if (params != null && params.length > 0)
            this.params = Arrays.asList(params);
    }

    private void init(Context context) {
        this.context = context;
        ngemartFirebaseAnalytics = new NgemartFirebaseAnalytics(context);
        if (context instanceof NgemartActivity) {
            ngemartAlert = new NgemartAlert(getCardActivity());
        }
    }

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
        InitOnClickListener();
    }

    public void setActivity(NgemartActivity activity) {
        this.context = activity;
        ngemartAlert = new NgemartAlert(getCardActivity());
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setData(T data,T data2) {
        this.data = data;
        this.data2 = data2;
    }

    public void setData(T data, String containerName) {
        this.data = data;
        this.containerName = containerName;
    }

    private void InitOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cardListener.onCardClick(getVerticalScrollbarPosition(), v, data);
            }
        });
    }

    public NgemartActivity getCardActivity() {
        return (NgemartActivity) context;
    }

    public Context getCardContext() { return context;}

    public void ShowError(String message) {
        ngemartAlert.showErrorSnackBar(message);
    }

    public void ShowError(Exception e) {
        ngemartAlert.showErrorSnackBar(e);
    }

    public void LogButtonClickEvent(String eventName) {
        if (!BuildConfig.DEBUG)
            ngemartFirebaseAnalytics.LogButtonClickEvent(eventName);
    }

    public interface CardListener<T> {
        void onCardClick(int position, View view, T data);
    }
}
