package com.weihu.apkshare.ui.app

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.weihu.apkshare.vo.AppInfo
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.vo.Resource
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hupihuai on 2017/11/28.
 */
abstract class AppViewModel : ViewModel() {
    abstract var appList: LiveData<Resource<List<AppInfo>>>

    fun extractApk(position: Int, callBack: (Boolean) -> Unit = {}) {
        Single.just(appList.value?.data?.get(position))
                .map {
                    AppUtils.copyFile(it)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callBack.invoke(it)
                }, {
                    //fail
                    it.printStackTrace()
                })
    }

    fun getShareIntent(position: Int)
            = AppUtils.getShareIntent(appList.value?.data!![position].apk)


}