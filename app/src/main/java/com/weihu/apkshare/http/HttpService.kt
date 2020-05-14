package com.weihu.apkshare.http

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlin.concurrent.thread

/**
 * Created by hupihuai on 2017/12/15.
 */
class HttpService : Service() {

    companion object {
        fun start(activity: Activity) {
            var intent = Intent(activity, HttpService::class.java)
            activity.startService(intent)
        }

        fun stop(activity: Activity) {
            var intent = Intent(activity, HttpService::class.java)
            activity.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        println("start server")
        thread {
            try {
                DefaultHttpServer.start(HttpConstant.port)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}