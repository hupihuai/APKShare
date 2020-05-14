package com.weihu.apkshare.preferences

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by hupihuai on 2017/11/23.
 */
class SharePreference<T>(context: Context, private val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    private val prefs by lazy {
        context.getSharedPreferences("apk_share", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = findPreferences(name, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreferences(name, value)
    }

    private fun putPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Long -> putLong(name, value)
            is Float -> putFloat(name, value)
            is String -> putString(name, value)
            is Boolean -> putBoolean(name, value)
            else ->
                throw  IllegalArgumentException("This type can not be saved into preferences")
        }
        apply()
    }

    private fun findPreferences(name: String, default: T) = with(prefs) {
        var result = when (default) {
            is Int -> getInt(name, default)
            is Long -> getLong(name, default)
            is Float -> getFloat(name, default)
            is String -> getString(name, default)
            is Boolean -> getBoolean(name, default)
            else ->
                throw  IllegalArgumentException("This type can not be saved into preferences")
        }

        result as T
    }
}