package com.example.ndk.ffmpeg

import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ndk.ffmpeg.databinding.ActivityMainBinding
import com.example.ndk.ffmpeg.databinding.ProgressBinding
import com.example.ndk.ffmpeg.util.FileUtil
import com.example.ndk.ffmpeg.util.VideoUtil
import com.example.ndk.ffmpeg.util.showToast
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBinding: ProgressBinding
    private lateinit var compressDialog: AlertDialog
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val inFileName = "input.mp4"
                FileUtil.copyUriToExternalFilesDir(this@MainActivity, uri, inFileName)
                val inputVideo = File(getExternalFilesDir("temp"), inFileName)
                try {
                    compressDialog.show()
                    VideoUtil.compressVideoAndCopy(this@MainActivity, inputVideo, compressDialog)
                    "Video compressed successfully".showToast()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Video compression failed".showToast()
                } finally {
                    compressDialog.dismiss()
                    inputVideo.delete()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBinding = ProgressBinding.inflate(layoutInflater)
        val dialogLayout = progressBinding.root
        compressDialog = AlertDialog.Builder(this).apply {
            setView(dialogLayout)
            setTitle(getString(R.string.compressing))
            setCancelable(false)
        }.create()

        binding.btn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
        }
    }

}
