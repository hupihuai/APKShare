package com.weihu.apkshare.binding

import android.databinding.BindingAdapter
import android.view.View

/**
 * Created by hupihuai on 2017/11/27.
 */

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}