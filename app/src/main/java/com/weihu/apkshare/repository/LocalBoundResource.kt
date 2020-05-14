package com.weihu.apkshare.repository

import android.arch.lifecycle.MediatorLiveData
import com.weihu.apkshare.AppExecutors
import com.weihu.apkshare.util.Objects
import com.weihu.apkshare.vo.Resource

/**
 * Created by hupihuai on 2017/11/27.
 */
abstract class LocalBoundResource<ResultType>(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        fetchFromLocal()
    }

    private fun setValue(newValue: Resource<ResultType>?) {
        if (!Objects.equals(result.value, newValue)) {
            appExecutors.mainThread.execute {
                result.value = newValue
            }

        }
    }

    private fun fetchFromLocal() {
        appExecutors.diskIO.execute {
            try {
                var data = loadData()
                if (data != null) {
                    setValue(Resource.success(data))
                } else {
                    setValue(Resource.error(null, "加载失败了或者没有数据^_^"))
                }
            } catch (e: Exception) {
                setValue(Resource.error(null, "加载失败了^_^"))
            }
        }
    }

    fun asLiveData() = result

    abstract fun loadData(): ResultType?


}