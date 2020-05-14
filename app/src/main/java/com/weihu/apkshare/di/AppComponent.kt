package com.weihu.apkshare.di

import android.app.Application
import com.weihu.apkshare.APKShareApp
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by hupihuai on 2017/11/22.
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityModule::class
]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(apkShareApp: APKShareApp)

}