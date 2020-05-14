package com.weihu.apkshare.ui.app

import android.arch.lifecycle.LiveData
import com.weihu.apkshare.vo.AppInfo
import com.weihu.apkshare.repository.AppRepository
import com.weihu.apkshare.vo.Resource
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/27.
 */
class SystemAppViewModel @Inject constructor(repository: AppRepository) : AppViewModel() {

    override var appList: LiveData<Resource<List<AppInfo>>> = repository.getSystemApp()

}