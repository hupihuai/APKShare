package com.weihu.apkshare.ui.file

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.FragmentFileBinding
import com.weihu.apkshare.di.Injectable
import com.weihu.apkshare.viewmodel.APKShareViewModelFactory
import com.weihu.apkshare.vo.FileInfo
import com.weihu.apkshare.vo.Resource
import kotlinx.android.synthetic.main.fragment_file.*
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/11/30.
 */
class FileFragment : Fragment(), Injectable {
    private lateinit var binding: FragmentFileBinding
    @Inject
    lateinit var viewModelFactory: APKShareViewModelFactory

    private lateinit var viewModel: FileViewModel

    private lateinit var actionAll: MenuItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_file, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FileViewModel::class.java)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }

        fab.setOnClickListener {
            val adapter: FileAdapter = binding.recyclerView.adapter as FileAdapter
            viewModel.deleteAPKFile(adapter.deleteList) {
                view?.let {
                    Snackbar.make(it, getString(R.string.delete_success), Snackbar.LENGTH_LONG).show()
                }

            }

        }
        requestPermission {
            viewModel.fileList.observe(this, Observer<Resource<List<FileInfo>>> { resource ->
                binding.apply {
                    fileResource = resource
                    resource?.data?.let {
                        if (recyclerView.adapter == null) {
                            recyclerView.adapter = FileAdapter(it, {
                                onShare(it)
                            })
                        } else {
                            (recyclerView.adapter as FileAdapter).replaceData(it)
                        }
                    }
                }
            })
        }


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        actionAll = menu.findItem(R.id.action_all)
        actionAll.isVisible = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_file, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit -> {
                actionAll.isVisible = !actionAll.isVisible
                fab.visibility = if (actionAll.isVisible) View.VISIBLE else View.GONE
                if (actionAll.isVisible) {
                    item.title = getString(R.string.action_cancel)
                } else {
                    item.title = getString(R.string.action_edit)
                }
                (recyclerView.adapter as FileAdapter).isEdit = actionAll.isVisible
                true
            }
            R.id.action_all -> {
                (recyclerView.adapter as FileAdapter).allSelect()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onShare(position: Int) {
        startActivity(viewModel.getShareIntent(position))
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