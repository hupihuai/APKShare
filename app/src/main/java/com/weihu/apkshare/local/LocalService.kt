package com.weihu.apkshare.local

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.vo.AppInfo
import com.weihu.apkshare.vo.FileInfo
import java.io.File

/**
 * Created by hupihuai on 2017/11/27.
 */
class LocalService {

    companion object {
        fun getApps(context: Context): List<AppInfo> {
            var appList = ArrayList<AppInfo>()
            var packageManager = context.packageManager
            var packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            packages.forEach {
                appList.add(AppInfo(
                        name = packageManager.getApplicationLabel(it.applicationInfo).toString(),
                        apk = it.packageName,
                        version = it.versionName,
                        source = it.applicationInfo.sourceDir,
                        data = it.applicationInfo.dataDir,
                        icon = packageManager.getApplicationIcon(it.applicationInfo),
                        isSystem = (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                ))
            }

            return appList
        }

        fun getAPKFromAPKDirectory(): List<FileInfo> {
            var rootDir = AppUtils.getAPKDir()
            var file = File(rootDir)
            return file.listFiles().map { FileInfo(it.name, it.lastModified()) }
        }

        fun getAPKFromWXDirectory(): List<FileInfo>? {
            var rootDir = AppUtils.getWXAPKDir()
            var file = File(rootDir)
            if (!file.exists()) {
                return null
            }
            return file.listFiles().filter { it.name.contains("apk") }.map { FileInfo(it.name, it.lastModified()) }
        }
    }
}