package com.weihu.apkshare.util

/**
 * Created by hupihuai on 2017/11/27.
 */
class Objects {
    companion object {
        fun equals(o1: Any?, o2: Any?): Boolean {
            if (o1 == null) {
                return o2 == null
            }
            if (o2 == null) {
                return false
            }
            return o1 == o2
        }
    }
}