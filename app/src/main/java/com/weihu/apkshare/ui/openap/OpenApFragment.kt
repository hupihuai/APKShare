package com.weihu.apkshare.ui.openap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.FragmentOpenApBinding
import com.weihu.apkshare.di.AppModule
import com.weihu.apkshare.di.Injectable
import com.weihu.apkshare.http.HttpConstant
import com.weihu.apkshare.http.HttpService
import com.weihu.apkshare.util.ApManager
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import com.weihu.apkshare.zxing.EncodingHandler
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by hupihuai on 2017/12/14.
 */
class OpenApFragment : Fragment(), Injectable {
    companion object {
        private val REQUEST_CODE_WRITE_SETTINGS = 0x100
        private val WHAT_QUERY_AP_ON = 0x100
    }

    @Inject
    lateinit var viewModelFactory: APKShareViewModelFactory

    private lateinit var binding: FragmentOpenApBinding
    private lateinit var viewModel: OpenApViewModel
    private var hopeOpen: Boolean = false

    private var apOn by Delegates.observable(false) { _, old, new ->
        if (old != new) {
            binding.apOn = new
            binding.apName.isEnabled = !new
            if (new) {
                HttpService.start(activity)
                var ipAddress = ApManager.getIpAddress()
                var httpUrl = "http://$ipAddress:${HttpConstant.port}"
                val bitmap = EncodingHandler.createQRCode(httpUrl, 500)
                binding.downloadTip = "请打开浏览器扫描二维码下载分享的文件\n$httpUrl"
                binding.qrCodeIv.setImageBitmap(bitmap)
            }
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            var isApOn = ApManager.isApOn(context)
            apOn = isApOn
            if (isApOn != hopeOpen) {
                sendApOnQuery()
            }
        }
    }

    private fun sendApOnQuery() {
        handler.sendEmptyMessageDelayed(WHAT_QUERY_AP_ON, 500)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_open_ap, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OpenApViewModel::class.java)
        binding.setApName(viewModel.apName)
        binding.setOpenClickListener {
            if (apOn) {
                closeAp()
            } else {
                openAp()
            }
        }

        apOn = ApManager.isApOn(context)
        hopeOpen = apOn
    }

    private fun closeAp() {
        requestApPermission(activity) {
            ApManager.disableAp(context, binding.getApName()!!)
            sendApOnQuery()
            hopeOpen = false
        }
    }

    private fun openAp() {
        var apName = binding.getApName()
        if (TextUtils.isEmpty(apName)) {
            Snackbar.make(binding.root, "请输入热点名字", Snackbar.LENGTH_SHORT).show()
            return
        }
        apName?.let {
            requestApPermission(activity) {
                ApManager.openAp(context, it)
                sendApOnQuery()
                hopeOpen = true
            }
        }
    }

    private fun requestApPermission(context: Context, callback: () -> Unit) {
        val permission: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        }
        if (permission) {
            callback.invoke()
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
            } else {
                RxPermissions(context as Activity).request(
                        Manifest.permission.WRITE_SETTINGS
                ).subscribe { granted ->
                    if (granted) {
                        callback.invoke()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.apName = binding.getApName() ?: viewModel.apName
        handler.removeCallbacksAndMessages(null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Settings.System.canWrite(context)) {
            binding.getApName()?.let {
                ApManager.openAp(context, it)
            }
        }
    }


}