package com.weihu.apkshare.ui.file

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.weihu.apkshare.R
import com.weihu.apkshare.databinding.ItemFileLayoutBinding
import com.weihu.apkshare.vo.FileInfo
import kotlin.properties.Delegates

/**
 * Created by hupihuai on 2017/11/30.
 */
class FileAdapter(private var fileList: List<FileInfo>?,
                  private val onShareClickListener: (position: Int) -> Unit = {}) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    var isEdit by Delegates.observable(false) { _, _, new ->
        notifyDataSetChanged()
        if (new) {
            deleteList.clear()
        }
    }
    val deleteList by lazy {
        ArrayList<FileInfo>()
    }
    private var layoutInflate: LayoutInflater? = null
    override fun getItemCount() = fileList?.size ?: 0

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        var fileInfo = fileList!![position]
        holder.binding(fileInfo)
        holder.isEdit(isEdit)
        holder.isSelected(deleteList.contains(fileInfo))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        var binding = DataBindingUtil.inflate<ItemFileLayoutBinding>(getLayoutInflate(parent.context), R.layout.item_file_layout, parent, false)
        binding.share.setOnClickListener {
            var position = getItemPosition(binding.root, parent)
            if (isEdit) {
                toggleDelete(binding.checkbox, position)
            } else {
                onShareClickListener.invoke(position)
            }
        }
        binding.root.setOnClickListener {
            if (isEdit) {
                var position = getItemPosition(binding.root, parent)
                toggleDelete(binding.checkbox, position)
            }

        }
        return FileViewHolder(binding.root)
    }

    private fun toggleDelete(checkBox: CheckBox, position: Int) {
        var fileInfo = fileList?.get(position)
        if (deleteList.contains(fileInfo)) {
            checkBox.isChecked = false
            deleteList.remove(fileInfo)
        } else {
            checkBox.isChecked = true
            deleteList.add(fileInfo!!)
        }
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
        private val binding: ItemFileLayoutBinding = DataBindingUtil.getBinding(view)!!
        fun binding(fileInfo: FileInfo) {
            binding.fileInfo = fileInfo
        }

        fun isEdit(isEdit: Boolean) {
            binding.checkbox.visibility = if (isEdit) View.VISIBLE else View.GONE
        }

        fun isSelected(isChecked: Boolean) {
            binding.checkbox.isChecked = isChecked
        }

    }

    fun replaceData(fileList: List<FileInfo>?) {
        this.fileList = fileList
        notifyDataSetChanged()
    }

    fun allSelect() {
        fileList?.let {
            deleteList.clear()
            deleteList.addAll(it)
            notifyDataSetChanged()
        }
    }
}