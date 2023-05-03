package com.example.ndk.ffmpeg.util

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import com.example.ndk.ffmpeg.BaseApplication
import com.example.ndk.ffmpeg.R
import com.example.ndk.ffmpeg.logic.video.VideoCompress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VideoUtil {

    private const val TAG = "VideoUtil"

    private suspend fun compressVideo(
        inFile: File, compressCallback: VideoCompress.CompressCallback
    ) = withContext(Dispatchers.IO) {
        val outFile = getOutputVideoFile(BaseApplication.context)
        val compressCommand = arrayOf(
            "ffmpeg", "-i", inFile.absolutePath, "-b:v", "1024k", outFile.absolutePath
        )
        VideoCompress.compressVideo(compressCommand, compressCallback)
        outFile
    }

    suspend fun compressVideoAndCopy(
        context: Context, inFile: File, compressCallback: VideoCompress.CompressCallback
    ) {
        val outFile = compressVideo(inFile, compressCallback)
        val outFileName = outFile.name
        val values = contentValuesOf(
            MediaStore.Video.Media.DISPLAY_NAME to outFileName,
            MediaStore.Video.Media.RELATIVE_PATH to Environment.DIRECTORY_DCIM + File.separator + context.getString(
                R.string.app_name
            )
        )
        val uri =
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            FileUtil.copyExternalFileToUri(context, outFile, uri)
        }
        outFile.delete()
    }

    private fun getOutputVideoFile(context: Context) = File(
        context.getExternalFilesDir("temp"),
        "output_${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())}.mp4"
    )

}
