package com.weihu.apkshare

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weihu.apkshare.http.DefaultHttpServer
import com.weihu.apkshare.http.HttpService
import com.weihu.apkshare.ui.common.NavigationController
import com.weihu.apkshare.util.ApManager
import com.weihu.apkshare.util.WFManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigationController.navigateToUserInstallApp()
        }

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setCheckedItem(R.id.nav_user_app)
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            var hotspotLocalIpAddress = WFManager.getInstance(this@MainActivity).getHotspotLocalIpAddress()
            println("hotspotLocalIpAddress = [${hotspotLocalIpAddress}]")
            var ipAddressFromHotspot = WFManager.getInstance(this@MainActivity).getIpAddressFromHotspot()
            println("ipAddressFromHotspot = ${ipAddressFromHotspot}")
            sendEmptyMessageDelayed(100, 1000)

            var apIpAddress = ApManager.getIpAddress()
            println("apIpAddress = ${apIpAddress}")

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var title = ""
        when (item.itemId) {
            R.id.nav_user_app -> {
                navigationController.navigateToUserInstallApp()
                title = getString(R.string.title_user_app)
            }
            R.id.nav_system_app -> {
                navigationController.navigateToSystemInstallApp()
                title = getString(R.string.title_system_app)
            }
            R.id.nav_apk_file -> {
                navigationController.navigateToAPKFile()
                title = getString(R.string.title_extract_apk)
            }
            R.id.nav_apk_wx -> {
                navigationController.navigateToWXAPKFile()
                title = getString(R.string.title_wx_apk)
            }
            R.id.nav_open_ap -> {
                navigationController.navigateToOpenAp()
                title = getString(R.string.open_ap)
            }
            R.id.nav_connect_ap -> {
                navigationController.navigateToConnectAp()
                title = getString(R.string.connect_ap)
//                openWifiPermission(this) {
//                    if (!WFManager.getInstance(this).isWifiEnable()) {
//                        WFManager.getInstance(this).openWifi()
//                    }
//                }
//                scanWifiPermission(this) {
//
//                    var scanWifi = WFManager.getInstance(this).scanWifi()
//                    println("scanWifi size ${scanWifi?.size ?: 0}")
//                    scanWifi?.forEach {
//                        println("ssid=${it.SSID}")
//                    }
//                    window.decorView.postDelayed({
//                        var createWifiCfg = WFManager.getInstance(this).createWifiCfg("weihu", null, WFManager.WIFI_NOPASS)
//                        WFManager.getInstance(this).addNetwork(createWifiCfg)
//                        handler.sendEmptyMessageDelayed(100, 1000)
//                    }, 1000)
//
//
//                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        toolbar.title = title
        return true
    }


    private fun scanWifiPermission(context: Context, callback: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RxPermissions(context as Activity).request(
                    Manifest.permission.ACCESS_FINE_LOCATION

            ).subscribe { granted ->
                if (granted) {
                    callback.invoke()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        HttpService.stop(this)
    }

    private fun openWifiPermission(context: Context, callback: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RxPermissions(context as Activity).request(
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE

            ).subscribe { granted ->
                if (granted) {
                    callback.invoke()
                }
            }
        }
    }

}
