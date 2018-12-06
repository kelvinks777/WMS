package com.gin.ngemart.baseui;

import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by luis on 1/20/2017.
 * Purpose :
 */

class NgemartRunnable<T> implements Runnable {
    private NgemartActivity activity;
    private NgemartActivity.ThreadHandler<T> handler;
    private Boolean cacthAllException = false;

    NgemartRunnable(NgemartActivity activity, NgemartActivity.ThreadHandler<T> handler) {
        this.activity = activity;
        this.handler = handler;
    }

    NgemartRunnable(NgemartActivity activity, NgemartActivity.ThreadHandler<T> handler, Boolean catchAllException) {
        this.activity = activity;
        this.handler = handler;
        this.cacthAllException = catchAllException;
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.onPrepare();
                } catch (Exception e) {
                    activity.showErrorSnackBar(e);
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
                        activity.showErrorSnackBar(e);
                    }
                }
            });

        } catch (final ConnectException | UnknownHostException ec) {
            ec.printStackTrace();
            activity.showErrorSnackBar(ec);
            if (cacthAllException) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.onError(ec);
                        } catch (Exception e) {
                            activity.showErrorSnackBar(e);
                        }
                    }
                });
            }

        } catch (final Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.onError(e);
                    } catch (Exception e) {
                        activity.showErrorSnackBar(e);
                    }
                }
            });

        } finally {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler.onFinish();
                    } catch (Exception e) {
                        activity.showErrorSnackBar(e);
                    }
                }
            });
        }
    }
}
