package com.weihu.apkshare.di

import com.weihu.apkshare.ui.app.AppFragment
import com.weihu.apkshare.ui.connectap.ConnectApFragment
import com.weihu.apkshare.ui.file.FileFragment
import com.weihu.apkshare.ui.openap.OpenApFragment
import com.weihu.apkshare.ui.wxapk.WXAPKFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by hupihuai on 2017/11/22.
 */
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeUserInstallAppFragment(): AppFragment

    @ContributesAndroidInjector
    abstract fun contributeUserInstallFileFragment(): FileFragment

    @ContributesAndroidInjector
    abstract fun contributeWXAPKFragment(): WXAPKFragment

    @ContributesAndroidInjector
    abstract fun contributeOpenApFragment(): OpenApFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectApFragment(): ConnectApFragment

}