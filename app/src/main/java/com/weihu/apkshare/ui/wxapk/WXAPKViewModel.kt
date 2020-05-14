package com.weihu.apkshare.ui.wxapk

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import com.weihu.apkshare.repository.WXAPKRepository
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.vo.Status
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/12/1.
 */
class WXAPKViewModel @Inject constructor(repository: WXAPKRepository) : ViewModel() {
    var fileList = repository.getAPKFiles()

    private val shareLive: MutableLiveData<Status> by lazy {
        MutableLiveData<Status>()
    }

    private val installLive: MutableLiveData<Status> by lazy {
        MutableLiveData<Status>()
    }

    fun shareApp(position: Int): LiveData<Status> {
        copyFile(shareLive, position)
        return shareLive

    }

    fun getShareIntent(position: Int): Intent {
        var fileInfo = fileList.value?.data?.get(position)
        return AppUtils.getShareIntent(getAPKName(fileInfo!!.fileName))
    }

    fun getInstallIntent(position: Int): Intent {
        var fileInfo = fileList.value?.data?.get(position)
        return AppUtils.getInstallIntent(getAPKName(fileInfo!!.fileName))
    }

    fun installApp(position: Int): LiveData<Status> {
        copyFile(installLive, position)
        return installLive
    }

    private fun copyFile(liveData: MutableLiveData<Status>, position: Int) {
        liveData.value = Status.LOADING
        Single.just(fileList.value?.data?.get(position))
                .map {
                    AppUtils.copyFile(AppUtils.getWXAPKPath(it.fileName), getAPKName(it.fileName))
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveData.value = Status.SUCCESS
                }, {
                    //fail
                    liveData.value = Status.ERROR
                })
    }

    private fun getAPKName(name: String) = name.replace(".apk", ".wx")
}