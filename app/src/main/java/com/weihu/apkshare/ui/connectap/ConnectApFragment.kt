package com.weihu.apkshare.ui.connectap

import android.support.v4.app.Fragment
import com.weihu.apkshare.di.Injectable
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/12/14.
 */
class ConnectApFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: APKShareViewModelFactory


}