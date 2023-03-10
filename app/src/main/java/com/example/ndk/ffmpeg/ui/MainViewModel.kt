package com.example.ndk.ffmpeg.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ndk.ffmpeg.BaseApplication
import com.example.ndk.ffmpeg.util.VideoUtil
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    fun compressVideo(inFile: File) {
        val outFile = getOutputVideoFile(BaseApplication.context)
        viewModelScope.launch {
            VideoUtil.compressVideo(inFile, outFile)
        }
    }

    private fun getOutputVideoFile(context: Context) = File(
        context.getExternalFilesDir("temp"),
        "output_${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())}.mp4"
    )

}
