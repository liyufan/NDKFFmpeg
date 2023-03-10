package com.example.ndk.ffmpeg

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ndk.ffmpeg.databinding.ActivityMainBinding
import com.example.ndk.ffmpeg.ui.MainViewModel
import com.example.ndk.ffmpeg.util.showToast
import com.permissionx.guolindev.PermissionX
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private val requestList = ArrayList<String>()
    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                val inFileName = "input.mp4"
                if (uri != null) {
                    copyUriToExternalFilesDir(uri, inFileName)
                    val inputVideo = File(getExternalFilesDir("temp"), inFileName)
                    try {
                        viewModel.compressVideo(inputVideo)
                        /**
                         * If call `showToast` here, because `compressVideo` is asynchronous,
                         * the toast will be displayed immediately even if the video is being compressed.
                         * TODO: Show toast after the video is compressed
                         */
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.btn.setOnClickListener {
            PermissionX.init(this).permissions(requestList).explainReasonBeforeRequest()
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList, "We need these permissions to access your files", "OK", "Cancel"
                    )
                }.onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "You need to allow these permissions in Settings",
                        "OK",
                        "Cancel"
                    )
                }.request { allGranted, _, deniedList ->
                    if (allGranted) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "video/*"
                        requestDataLauncher.launch(intent)
                    } else {
                        "These permissions are denied: $deniedList".showToast(Toast.LENGTH_LONG)
                    }
                }
        }
    }

    /**
     * Logcat shows "PerfMonitor: Slow Operation: Activity com.example.ndk.ffmpeg/.MainActivity onActivityResult took ..."
     * TODO: Make copy operation asynchronous
     */
    private fun copyUriToExternalFilesDir(uri: Uri, fileName: String) {
        val startTime = System.currentTimeMillis()
        val inputStream = contentResolver.openInputStream(uri)
        val tempDir = getExternalFilesDir("temp")
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
        val endTime = System.currentTimeMillis()
        Log.d(TAG, "copyUriToExternalFilesDir: $fileName cost ${endTime - startTime}ms")
    }

}
