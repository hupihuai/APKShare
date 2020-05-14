package com.weihu.apkshare.util

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface


/**
 * Created by hupihuai on 2017/12/8.
 */
class ApManager {

    companion object {


        private fun getWifiManager(context: Context): WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        fun isApOn(context: Context): Boolean {
            var isApOn = false
            try {
                var isWifiApEnabled = WifiManager::class.java.getMethod("isWifiApEnabled")
                isWifiApEnabled.isAccessible = true
                isApOn = isWifiApEnabled.invoke(getWifiManager(context)) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isApOn
        }


        fun disableAp(context: Context, name: String) {
            var wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = name
            var setWifiApEnabled = WifiManager::class.java.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.java)
            setWifiApEnabled.isAccessible = true
            setWifiApEnabled.invoke(getWifiManager(context), wifiConfiguration, false)
        }

        fun openAp(context: Context, name: String): Boolean {
            var wifiManager = getWifiManager(context)
            var result = false
            try {
                var wifiConfiguration = WifiConfiguration()
                wifiConfiguration.SSID = name
                if (isApOn(context)) {
                    wifiManager.isWifiEnabled = false
                    disableAp(context, name)
                }

                var setWifiApEnabled = WifiManager::class.java.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.java)
                setWifiApEnabled.isAccessible = true
                setWifiApEnabled.invoke(wifiManager, wifiConfiguration, !isApOn(context))
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun getIpAddress(): String {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    var networkInterface = interfaces.nextElement()

                    val enumeration = networkInterface.inetAddresses
                    while (enumeration.hasMoreElements()) {
                        var inetAddress = enumeration.nextElement()
                        if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    }
}