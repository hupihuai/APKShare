package com.weihu.apkshare.vo

import android.graphics.drawable.Drawable

/**
 * Created by hupihuai on 2017/11/30.
 */
data class FileInfo(val fileName: String, val lastModifyTime: Long, var name: String? = null, var icon: Drawable? = null)