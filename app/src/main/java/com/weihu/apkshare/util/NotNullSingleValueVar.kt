package com.weihu.apkshare.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by hupihuai on 2017/11/23.
 */
class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {
    private var singleValue: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            singleValue ?: throw IllegalStateException("not initialized")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.singleValue = if (this.singleValue == null) value
        else throw IllegalStateException("already initialized")
    }
}