package com.weihu.apkshare.util

import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.weihu.apkshare.APKShareApp
import com.weihu.apkshare.BuildConfig
import java.io.File

/**
 * Created by hupihuai on 2017/11/30.
 */
class FileUriUtils {
    companion object {
        fun getUri(file: File) =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(APKShareApp.instance.baseContext,
                            "${BuildConfig.APPLICATION_ID}.fileProvider", file)
                } else {
                    Uri.fromFile(file)
                }

    }
}