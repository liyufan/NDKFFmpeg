package com.example.ndk.ffmpeg.logic.video

object VideoCompress {

    init {
        System.loadLibrary("video")
    }

    external fun compressVideo(compressCommand: Array<String>, callback: CompressCallback)

    interface CompressCallback {
        fun onCompress(current: Long, total: Long)
    }

}
