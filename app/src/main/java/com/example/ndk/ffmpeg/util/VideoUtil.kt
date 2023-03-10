package com.example.ndk.ffmpeg.util

import android.util.Log
import com.example.ndk.ffmpeg.logic.video.VideoCompress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object VideoUtil {

    private const val TAG = "VideoUtil"

    // TODO: Real-time progress bar display while compressing
    suspend fun compressVideo(inFile: File, outFile: File) = withContext(Dispatchers.IO) {
        val compressCommand = arrayOf(
            "ffmpeg", "-i", inFile.absolutePath, "-b:v", "1024k", outFile.absolutePath
        )
        VideoCompress.compressVideo(compressCommand, object : VideoCompress.CompressCallback {
            override fun onCompress(current: Long, total: Long) {
                Log.i(TAG, "Compress progress: $current / $total")
            }
        })
    }

}
