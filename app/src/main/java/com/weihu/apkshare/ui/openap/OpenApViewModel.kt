package com.weihu.apkshare.ui.openap

import android.arch.lifecycle.ViewModel
import com.weihu.apkshare.preference
import com.weihu.apkshare.preferences.PreferenceKey
import javax.inject.Inject

/**
 * Created by hupihuai on 2017/12/14.
 */
class OpenApViewModel @Inject constructor() : ViewModel() {

    var apName: String by preference(PreferenceKey.AP_NAME, "")



}