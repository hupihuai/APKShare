package com.weihu.apkshare

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by hupihuai on 2017/11/27.
 */
@Singleton
class AppExecutors(val diskIO: Executor, val networkIO: Executor, val mainThread: Executor) {

    @Inject
    constructor() : this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), MainThreadExecutor())

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            mainThreadHandler.post(command)
        }
    }
}