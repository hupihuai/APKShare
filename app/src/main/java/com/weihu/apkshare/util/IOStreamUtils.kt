package com.weihu.apkshare.util

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by hupihuai on 2017/12/18.
 */
class IOStreamUtils {

    companion object {
        fun inputStreamToString(inputStream: InputStream?): String? {
            if (inputStream == null) {
                return null
            }
            val baos = ByteArrayOutputStream()
            var len = 0
            val bytes = ByteArray(2048)
            try {
                while (true) {
                    len = inputStream.read(bytes)
                    if (len <= 0) {
                        break
                    }
                    baos.write(bytes, 0, len)
                }

                return baos.toString("UTF-8")
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

    }
}