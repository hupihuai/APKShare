package com.weihu.apkshare.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.weihu.apkshare.ui.app.SystemAppViewModel
import com.weihu.apkshare.ui.app.UserAppViewModel
import com.weihu.apkshare.ui.file.FileViewModel
import com.weihu.apkshare.ui.openap.OpenApViewModel
import com.weihu.apkshare.ui.wxapk.WXAPKViewModel
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by hupihuai on 2017/11/22.
 */
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserAppViewModel::class)
    abstract fun bindUserAppViewModel(viewModel: UserAppViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SystemAppViewModel::class)
    abstract fun bindSystemAppViewModel(viewModel: SystemAppViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FileViewModel::class)
    abstract fun bindFileViewModel(viewModel: FileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WXAPKViewModel::class)
    abstract fun bindWXAPKViewModel(viewModel: WXAPKViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OpenApViewModel::class)
    abstract fun bindOpenApViewModel(viewModel: OpenApViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: APKShareViewModelFactory): ViewModelProvider.Factory
}