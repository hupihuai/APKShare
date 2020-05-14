package com.weihu.apkshare.util

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import com.weihu.apkshare.SingletonHolder

/**
 * Created by hupihuai on 2017/12/11.
 */
class WFManager private constructor(context: Context) {
    private val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    companion object : SingletonHolder<WFManager, Context>(::WFManager) {
        val WIFI_NOPASS = 1
        val WIFI_WEP = 2
        val WIFI_WPA = 3
    }

    private var scanResults: List<ScanResult>? = null
    private lateinit var mWifiConfigurations: List<WifiConfiguration>

    fun openWifi() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.setWifiEnabled(true)
        }
    }

    fun closeWifi() {
        if (wifiManager.isWifiEnabled) {
            wifiManager.setWifiEnabled(false)
        }
    }

    fun isWifiEnable(): Boolean {
        return wifiManager?.isWifiEnabled ?: false
    }

    fun scanWifi(): List<ScanResult>? {
        wifiManager.startScan()
        scanResults = wifiManager.scanResults
        mWifiConfigurations = wifiManager.configuredNetworks
        return scanResults
    }

    fun disconnectCurrentWifi() {
        if (wifiManager.isWifiEnabled) {
            var networkId = wifiManager.connectionInfo.networkId
            wifiManager.disableNetwork(networkId)
        }
    }


    fun createWifiCfg(ssid: String, password: String?, type: Int): WifiConfiguration {
        var checkExist = checkExist(ssid, password)
        if (checkExist != null) {
            return checkExist
        }

        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        config.SSID = "\"$ssid\""

        when (type) {
            WIFI_NOPASS -> config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            WIFI_WEP -> {
                config.hiddenSSID = true
                config.wepKeys[0] = "\"$password\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
            }
            WIFI_WPA -> {
                config.preSharedKey = "\"$password\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                config.status = WifiConfiguration.Status.ENABLED
            }
        }

        return config
    }

    private fun checkExist(ssid: String, password: String?): WifiConfiguration? {
        mWifiConfigurations?.forEach {
            if (it.SSID == "\"$ssid\"") {
                return it
            }
        }
        return null
    }

    fun addNetwork(wf: WifiConfiguration): Boolean {
        //断开当前的连接
        disconnectCurrentWifi()
        //连接新的连接
        val netId = wifiManager.addNetwork(wf)
        return wifiManager.enableNetwork(netId, true)
    }


    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     * @return
     */
    fun getIpAddressFromHotspot(): String {
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        var ipAddress = "192.168.43.1"
        val dhcpInfo = wifiManager.getDhcpInfo()
        val address = dhcpInfo.gateway
        ipAddress = parseIp(address)
        return ipAddress
    }


    /**
     * 开启热点之后，获取自身热点的IP地址
     * @return
     */
    fun getHotspotLocalIpAddress(): String {
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        var ipAddress = "192.168.43.1"
        val dhcpInfo = wifiManager.getDhcpInfo()
        val address = dhcpInfo.serverAddress
        ipAddress = parseIp(address)
        return ipAddress
    }

    private fun parseIp(address: Int): String {
        var ipAddress = "${(address and 0xFF)}.${(address shr 8 and 0xFF)}.${(address shr 16 and 0xFF)}.${(address shr 24 and 0xFF)}"
        return ipAddress
    }


}