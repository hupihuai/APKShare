package com.weihu.apkshare.ui.app

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.ItemAppLayoutBinding
import com.weihu.apkshare.vo.AppInfo

/**
 * Created by hupihuai on 2017/11/27.
 */
class AppAdapter(private val appList: List<AppInfo>?,
                 private var onExtractClickListener: (position: Int) -> Unit = {},
                 private var onShareClickListener: (position: Int) -> Unit = {})
    : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private var layoutInflate: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        var binding = DataBindingUtil.inflate<ItemAppLayoutBinding>(getLayoutInflate(parent.context),
                R.layout.item_app_layout, parent, false)
        binding.extract.setOnClickListener {
            onExtractClickListener.invoke(getItemPosition(binding.root, parent))
        }
        binding.share.setOnClickListener {
            onShareClickListener.invoke(getItemPosition(binding.root, parent))

        }
        return AppViewHolder(binding.root)
    }

    private fun getItemPosition(view: View, parent: ViewGroup) = (parent as RecyclerView).layoutManager.getPosition(view)

    private fun getLayoutInflate(context: Context): LayoutInflater {
        if (layoutInflate != null) {
            return layoutInflate!!
        }
        layoutInflate = LayoutInflater.from(context)
        return layoutInflate!!

    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.binding(appList?.get(position))
    }

    override fun getItemCount() = appList?.size ?: 0

    class AppViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun binding(appInfo: AppInfo?) {
            appInfo?.let {
                var binding = DataBindingUtil.getBinding<ItemAppLayoutBinding>(view)
                binding!!.info = appInfo
            }
        }
    }
}