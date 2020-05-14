package com.weihu.apkshare

import android.app.Activity
import android.app.Application
import com.weihu.apkshare.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/22.
 */
class APKShareApp : Application(), HasActivityInjector {

    companion object {
        var instance: APKShareApp by notNullSingleValue()
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppInjector.init(this)

    }

    override fun activityInjector() = dispatchingAndroidInjector
}