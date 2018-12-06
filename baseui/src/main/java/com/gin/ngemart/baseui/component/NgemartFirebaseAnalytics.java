package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.Event;


/**
 * Created by Jati on 3/13/2017.
 */

public class NgemartFirebaseAnalytics {
    private FirebaseAnalytics firebaseAnalytics;
    public NgemartFirebaseAnalytics(Context context) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //firebaseAnalytics.setMinimumSessionDuration(2000);
        //firebaseAnalytics.setSessionTimeoutDuration(300000);
    }

    public void LogEvent(FirebaseAnalytics.Event event, Bundle params) {
        firebaseAnalytics.logEvent(event.toString(),params);
    }

    public void LogCheckoutEvent(String soId,String currency,double totalAmount) {
        Bundle params = new Bundle();
        params.putString(Param.TRANSACTION_ID,soId);
        params.putDouble(Param.VALUE,totalAmount);
        params.putString(Param.CURRENCY,currency);
        firebaseAnalytics.logEvent(Event.BEGIN_CHECKOUT,params);
    }

    public void LogButtonClickEvent(String eventName) {
        Bundle params = new Bundle();
        params.putString(CustomParam.STATUS,"clicked");
        firebaseAnalytics.logEvent(eventName,params);
    }

    public static class CustomParam {
        public static final String STATUS = "status";
    }
}
