package com.weihu.apkshare.ui.wxapk

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.FragmentWxapkBinding
import com.weihu.apkshare.di.Injectable
import com.weihu.apkshare.ui.common.RetryCallback
import com.weihu.apkshare.util.AppUtils
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import com.weihu.apkshare.vo.FileInfo
import com.weihu.apkshare.vo.Resource
import com.weihu.apkshare.vo.Status
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/12/1.
 */
class WXAPKFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: APKShareViewModelFactory

    private lateinit var viewModel: WXAPKViewModel
    private lateinit var binding: FragmentWxapkBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wxapk, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WXAPKViewModel::class.java)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
        binding.retryCallback = object : RetryCallback {
            override fun retry() {

            }
        }

        requestPermission {
            viewModel.fileList.observe(this, Observer<Resource<List<FileInfo>>> { resource ->
                binding.apply {
                    fileResource = resource
                    resource?.data?.let {
                        if (recyclerView.adapter == null) {
                            recyclerView.adapter = WXAPKAdapter(resource?.data,
                                    {
                                        installApp(it)
                                    },
                                    {
                                        shareApp(it)

                                    })
                        } else {
                            recyclerView.adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }

    private fun shareApp(position: Int) {
        viewModel.shareApp(position).observe(this, Observer {
            if (it == Status.SUCCESS) {
                var shareIntent = viewModel.getShareIntent(position)
                startActivity(shareIntent)
            }
        })
    }

    private fun installApp(position: Int) {
        viewModel.installApp(position).observe(this, Observer {
            if (it == Status.SUCCESS) {
                var shareIntent = viewModel.getInstallIntent(position)
                startActivity(shareIntent)
            }
        })
    }

    private fun requestPermission(callback: () -> Unit) {
        RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        callback.invoke()
                    } else {
                        Snackbar.make(binding.root, getString(R.string.please_open_permission), Snackbar.LENGTH_SHORT).show()
                    }
                }
    }

}