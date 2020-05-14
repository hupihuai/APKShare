package com.weihu.apkshare.ui.wxapk

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.ItemWxapkLayoutBinding
import com.weihu.apkshare.vo.FileInfo

/**
 * Created by hupihuai on 2017/11/30.
 */
class WXAPKAdapter(private val fileList: List<FileInfo>?,
                   private var onInstallClickListener: (position: Int) -> Unit = {},
                   private var onShareClickListener: (position: Int) -> Unit = {}) : RecyclerView.Adapter<WXAPKAdapter.FileViewHolder>() {
    private var layoutInflate: LayoutInflater? = null
    override fun getItemCount() = fileList?.size ?: 0

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.binding(fileList!![position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        var binding = DataBindingUtil.inflate<ItemWxapkLayoutBinding>(getLayoutInflate(parent.context),
                R.layout.item_wxapk_layout, parent, false)
        binding.install.setOnClickListener {
            onInstallClickListener.invoke(getItemPosition(binding.root, parent))
        }
        binding.share.setOnClickListener {
            onShareClickListener.invoke(getItemPosition(binding.root, parent))

        }
        return FileViewHolder(binding.root)
    }

    private fun getItemPosition(view: View, parent: ViewGroup) = (parent as RecyclerView).layoutManager.getPosition(view)

    private fun getLayoutInflate(context: Context): LayoutInflater {
        if (layoutInflate != null) {
            return layoutInflate!!
        }
        layoutInflate = LayoutInflater.from(context)
        return layoutInflate!!

    }

    class FileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun binding(fileInfo: FileInfo) {
            var binding = DataBindingUtil.getBinding<ItemWxapkLayoutBinding>(view)!!
            binding.name = fileInfo.fileName
        }

    }
}