package com.weihu.apkshare.di

import com.weihu.apkshare.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by hupihuai on 2017/11/22.
 */
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}