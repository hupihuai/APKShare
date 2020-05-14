package com.weihu.apkshare.ui.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.weihu.apkshare.MainActivity
import com.weihu.apkshare.R
import com.weihu.apkshare.ui.app.AppFragment
import com.weihu.apkshare.ui.connectap.ConnectApFragment
import com.weihu.apkshare.ui.file.FileFragment
import com.weihu.apkshare.ui.openap.OpenApFragment
import com.weihu.apkshare.ui.openap.OpenApViewModel
import com.weihu.apkshare.ui.wxapk.WXAPKFragment
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/27.
 */
class NavigationController @Inject constructor(mainActivity: MainActivity) {
    private val containerId: Int = R.id.container
    private val fragmentManager: FragmentManager = mainActivity.supportFragmentManager

    private fun commit(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(containerId, fragment)
            commitAllowingStateLoss()
        }
    }

    fun navigateToUserInstallApp() {
        val fragment = AppFragment()
        val bundle = Bundle()
        bundle.putInt(AppFragment.ARGUMENT_TYPE, AppFragment.TYPE_USER)
        fragment.arguments = bundle
        commit(fragment)
    }

    fun navigateToSystemInstallApp() {
        val fragment = AppFragment()
        val bundle = Bundle()
        bundle.putInt(AppFragment.ARGUMENT_TYPE, AppFragment.TYPE_SYSTEM)
        fragment.arguments = bundle
        commit(fragment)
    }

    fun navigateToAPKFile() {
        val fragment = FileFragment()
        commit(fragment)
    }

    fun navigateToWXAPKFile() {
        val fragment = WXAPKFragment()
        commit(fragment)
    }

    fun navigateToOpenAp() {
        val fragment = OpenApFragment()
        commit(fragment)
    }

    fun navigateToConnectAp() {
        val fragment = ConnectApFragment()
        commit(fragment)
    }

}