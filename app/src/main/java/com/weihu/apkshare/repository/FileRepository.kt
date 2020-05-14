package com.weihu.apkshare.repository

import android.app.Application
import com.weihu.apkshare.AppExecutors
import com.weihu.apkshare.local.LocalService
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.vo.FileInfo
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/27.
 */
class FileRepository {
    private var appExecutors: AppExecutors
    private var application: Application

    @Inject
    constructor(appExecutors: AppExecutors, application: Application) {
        this.appExecutors = appExecutors
        this.application = application
    }

    fun getAPKFiles() = object : LocalBoundResource<List<FileInfo>>(appExecutors) {
        override fun loadData(): List<FileInfo>? {
            var fileList = LocalService.getAPKFromAPKDirectory()
            fileList.forEach {
                var apkInfo = AppUtils.getAPKInfo(application, "${AppUtils.getAPKDir()}/${it.fileName}")
                it.name = apkInfo?.first
                it.icon = apkInfo?.second
            }
            return fileList
        }
    }.asLiveData()


    fun deleteAPKFiles(deleteFiles: List<FileInfo>, finish: () -> Unit) {
        appExecutors.diskIO.execute {
            deleteFiles.forEach {
                AppUtils.getFilePathByAllName(it.fileName).delete()
            }
            appExecutors.mainThread.execute(finish)
        }
    }

}