package com.weihu.apkshare

import com.weihu.apkshare.preferences.SharePreference
import com.weihu.apkshare.util.NotNullSingleValueVar
import kotlin.properties.ReadWriteProperty

/**
 * Created by hupihuai on 2017/11/23.
 */

fun <T : Any> preference(name: String, default: T)
        : ReadWriteProperty<Any?, T> = SharePreference(APKShareApp.instance, name, default)

fun <T : Any> notNullSingleValue(): ReadWriteProperty<Any?, T> = NotNullSingleValueVar()
