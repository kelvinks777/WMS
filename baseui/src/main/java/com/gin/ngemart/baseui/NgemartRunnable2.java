package com.gin.ngemart.baseui;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by luis on 2/6/2017.
 * Purpose :
 */

class NgemartRunnable2<T> implements Runnable {
    private NgemartActivity activity;
    private NgemartActivity.ThreadHandler2<T> handler;
    private Boolean catchAllException = false;

    NgemartRunnable2(NgemartActivity activity, NgemartActivity.ThreadHandler2<T> handler) {
        this.activity = activity;
        this.handler = handler;
    }

    NgemartRunnable2(NgemartActivity activity, NgemartActivity.ThreadHandler2<T> handler, Boolean catchAllException) {
        this.activity = activity;
        this.handler = handler;
        this.catchAllException = catchAllException;
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.onPrepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            final T object = handler.onBackground();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.onSuccess(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (final ConnectException | UnknownHostException ec) {
            ec.printStackTrace();
            activity.showErrorSnackBar(ec);
            if (catchAllException) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.onError(ec);
                    }
                });
            }

        } catch (final Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handler.onError(e);
                }
            });
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.onFinish();
            }
        });
    }
}