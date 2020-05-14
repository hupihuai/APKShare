package com.weihu.apkshare.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.weihu.apkshare.APKShareApp
import com.weihu.apkshare.vo.AppInfo
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Created by hupihuai on 2017/11/28.
 */
class AppUtils {

    companion object {
        fun copyFile(appInfo: AppInfo) = copyFile(appInfo.source ?: "", appInfo.apk)

        fun copyFile(initialPath: String, name: String): Boolean {
            var result: Boolean
            result = try {
                val initialFile = File(initialPath)
                val finalFile = getFilePathByName(name)
                FileUtils.copyFile(initialFile, finalFile)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            return result
        }

        fun getFilePathByName(name: String): File = File("${getAPKDir()}/$name.apk")

        fun getFilePathByAllName(name: String): File = File("${getAPKDir()}/$name")

        fun getAPKDir(): String {
            var storageDirectory = Environment.getExternalStorageDirectory()
            var rootPath = "${storageDirectory}/apkshare"
            var file = File(rootPath)
            if (!file.exists()) {
                file.mkdirs()
            }
            return rootPath
        }

        fun getWXAPKDir(): String {
            var storageDirectory = Environment.getExternalStorageDirectory()
            var rootPath = "${storageDirectory}/tencent/MicroMsg/Download"
            return rootPath
        }

        fun getWXAPKPath(name: String) = "${getWXAPKDir()}/$name"

        fun getShareIntent(name: String) = Intent().apply {
            val file = getFilePathByName(name)
            action = Intent.ACTION_SEND
            type = "application/vnd.android.package-archive"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            putExtra(Intent.EXTRA_STREAM, FileUriUtils.getUri(file))
        }

        fun getInstallIntent(name: String) = Intent(Intent.ACTION_VIEW).apply {
            val file = getFilePathByName(name)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
//            setDataAndType(FileUriUtils.getUri(file), "application/vnd.android.package-archive")

            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                //provider authorities
                val apkUri = FileProvider.getUriForFile(APKShareApp.instance, "com.weihu.apkshare.fileProvider", file);
                //Granting Temporary Permissions to a URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
        }


        fun getAPKInfo(context: Context, apkPath: String): Pair<String, Drawable>? {
            var pm = context.packageManager
            var info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)

            var pair = info?.let {
                var appInfo = info.applicationInfo
                appInfo.sourceDir = apkPath
                appInfo.publicSourceDir = apkPath
                var icon = appInfo.loadIcon(pm)
                var name = appInfo.loadLabel(pm).toString()
                Pair<String, Drawable>(name, icon)
            }

            return pair

        }

    }
}