package com.gin.ngemart.baseui;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Luis Ginanjar on 22/01/2017.
 * Purpose :
 */

public class NgemartThreadPoolBackground {
    private static ThreadPoolExecutor executor;

    private static void Init() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(
                    4,
                    10,
                    1L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>()
            );
        }
    }

    public static <T> void ThreadStart(final NgemartActivity activity, final NgemartActivity.ThreadHandler<T> handler) {
        Runnable runnable = new NgemartRunnable<>(activity, handler);

        if (executor == null) {
            Init();
        }

        executor.execute(runnable);
    }

}
