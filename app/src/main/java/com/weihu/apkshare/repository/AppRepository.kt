package com.weihu.apkshare.repository

import android.app.Application
import com.weihu.apkshare.AppExecutors
import com.weihu.apkshare.vo.AppInfo
import com.weihu.apkshare.local.LocalService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by hupihuai on 2017/11/27.
 */
@Singleton
class AppRepository {
    private  var appExecutors: AppExecutors
    private  var application: Application

    @Inject
    constructor(appExecutors: AppExecutors, application: Application) {
        this.appExecutors = appExecutors
        this.application = application
    }

    fun getApp() = object : LocalBoundResource<List<AppInfo>>(appExecutors) {
        override fun loadData(): List<AppInfo>? {
            var appList = LocalService.getApps(application)
            return appList.filter {
                !it.isSystem
            }
        }
    }.asLiveData()

    fun getSystemApp() = object : LocalBoundResource<List<AppInfo>>(appExecutors) {
        override fun loadData(): List<AppInfo>? {
            var appList = LocalService.getApps(application)
            return appList.filter {
                it.isSystem
            }
        }
    }.asLiveData()


}