package com.weihu.apkshare.ui.app

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
import com.weihu.apkshare.databinding.FragmentUserInstallAppBinding
import com.weihu.apkshare.di.Injectable
import com.weihu.apkshare.vo.AppInfo
import com.weihu.apkshare.ui.common.RetryCallback
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import com.weihu.apkshare.vo.Resource
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/23.
 */
class AppFragment : Fragment(), Injectable {

    companion object {
        val TYPE_USER = 0
        val TYPE_SYSTEM = 1
        val ARGUMENT_TYPE = "type"
    }

    @Inject
    lateinit var viewModelFactory: APKShareViewModelFactory

    private lateinit var viewModel: AppViewModel

    private lateinit var binding: FragmentUserInstallAppBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_install_app, container, false)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)

        }
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false

        }
        binding.retryCallback = object : RetryCallback {
            override fun retry() {

            }
        }
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var type = arguments[ARGUMENT_TYPE]
        viewModel = if (type == TYPE_USER) {
            ViewModelProviders.of(this, viewModelFactory).get(UserAppViewModel::class.java)
        } else {
            ViewModelProviders.of(this, viewModelFactory).get(SystemAppViewModel::class.java)
        }

        viewModel.appList.observe(this, Observer<Resource<List<AppInfo>>> { resource ->
            binding.apply {
                appResource = resource
                resource?.data?.let {
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = AppAdapter(it, {
                            extractAPK(it)
                        }, {
                            shareApk(it)
                        })
                    } else {
                        recyclerView.adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun extractAPK(it: Int) {
        requestPermission {
            viewModel.extractApk(it) {
                Snackbar.make(view!!, getString(R.string.extract_success), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareApk(position: Int) {
        requestPermission {
            viewModel.extractApk(position) {
                startActivity(viewModel.getShareIntent(position))

            }
        }

    }


    private fun requestPermission(callback: () -> Unit) {
        RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        callback.invoke()
                    } else {
                        Snackbar.make(binding.refreshLayout, getString(R.string.please_open_permission), Snackbar.LENGTH_SHORT).show()
                    }
                }
    }


}