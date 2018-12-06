package com.gin.wms.warehouse.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.gin.wms.warehouse.BuildConfig

/**
 * Created by manbaul on 3/15/2018.
 */
class CountdownService : Service() {

    companion object {
        const val COUNTDOWN_SERVICE_FINISH = "COUNTDOWN_SERVICE_FINISH"
        const val MILIS_IN_FUTURE_DEBUG: Long = 60000
        const val MILIS_IN_FUTURE: Long = 300000
        const val COUNTDOWN_INTERVAL: Long = 60000
    }

    private var cdt: CountDownTimer? = null

    private var milisInFuture:Long = 0
    get() {
        if (BuildConfig.DEBUG) {
            return MILIS_IN_FUTURE_DEBUG
        } else {
            return MILIS_IN_FUTURE
        }
    }

    override fun onCreate() {
        super.onCreate()
        cdt = object : CountDownTimer(milisInFuture, COUNTDOWN_INTERVAL) {
            override fun onFinish() {
                val intent = Intent(COUNTDOWN_SERVICE_FINISH)
                LocalBroadcastManager.getInstance(this@CountdownService).sendBroadcast(intent)
                cdt?.start()
            }

            override fun onTick(p0: Long) {

            }
        }
        cdt?.start()
    }

    override fun onDestroy() {
        cdt?.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}