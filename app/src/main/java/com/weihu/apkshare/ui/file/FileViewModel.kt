package com.weihu.apkshare.ui.file

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.content.Intent
import com.weihu.apkshare.repository.FileRepository
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.vo.FileInfo
import com.weihu.apkshare.vo.Resource
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/30.
 */
class FileViewModel @Inject constructor(var fileRepository: FileRepository) : ViewModel() {


    private val refreshData = MutableLiveData<Int>()

    init {
        refreshData.value = 1
    }

    var fileList: LiveData<Resource<List<FileInfo>>> = Transformations.switchMap(refreshData) {
        fileRepository.getAPKFiles()
    }


    fun getShareIntent(position: Int): Intent {
        var fileInfo = fileList.value?.data?.get(position)
        return AppUtils.getShareIntent(getAPKName(fileInfo!!.fileName))
    }

    private fun getAPKName(name: String) = name.replace(".apk", "")
    fun deleteAPKFile(deleteList: ArrayList<FileInfo>, finish: () -> Unit = {}) {
        fileRepository.deleteAPKFiles(deleteList) {
            refreshData.value = refreshData.value!!.plus(1)
            finish.invoke()
        }
    }

}