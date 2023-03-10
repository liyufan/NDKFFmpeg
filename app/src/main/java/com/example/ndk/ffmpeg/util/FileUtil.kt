package com.example.ndk.ffmpeg.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

object FileUtil {

    private const val TAG = "FileUtil"

    // https://blog.csdn.net/guolin_blog/article/details/105419420
    suspend fun copyUriToExternalFilesDir(context: Context, uri: Uri, fileName: String) =
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempDir = context.getExternalFilesDir("temp")
            if (inputStream != null && tempDir != null) {
                val file = File("$tempDir/$fileName")
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val bos = BufferedOutputStream(fos)
                val byteArray = ByteArray(1024)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                fos.close()
            }
        }

    suspend fun copyExternalFileToUri(context: Context, file: File, uri: Uri) =
        withContext(Dispatchers.IO) {
            val fis = FileInputStream(file)
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val bis = BufferedInputStream(fis)
                val bos = BufferedOutputStream(outputStream)
                val byteArray = ByteArray(1024)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                outputStream.close()
            }
        }

}
