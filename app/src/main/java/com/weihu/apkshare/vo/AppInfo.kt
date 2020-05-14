package com.weihu.apkshare.vo

import android.graphics.drawable.Drawable

/**
 * Created by hupihuai on 2017/11/27.
 */
data class AppInfo(
        val name: String,
        val apk: String,
        val version: String?,
        val source: String?,
        val data: String?,
        val icon: Drawable?,
        val isSystem: Boolean
)