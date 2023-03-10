package com.example.ndk.ffmpeg.util

import android.widget.Toast
import com.example.ndk.ffmpeg.BaseApplication

fun <T> T.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(BaseApplication.context, this.toString(), duration).show()
}
