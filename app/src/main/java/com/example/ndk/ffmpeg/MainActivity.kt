package com.example.ndk.ffmpeg

import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ndk.ffmpeg.databinding.ActivityMainBinding
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
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val inFileName = "input.mp4"
                FileUtil.copyUriToExternalFilesDir(this@MainActivity, uri, inFileName)
                val inputVideo = File(getExternalFilesDir("temp"), inFileName)
                try {
                    VideoUtil.compressVideoAndCopy(inputVideo)
                    "Video compressed successfully".showToast()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Video compression failed".showToast()
                } finally {
                    inputVideo.delete()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
        }
    }

}
