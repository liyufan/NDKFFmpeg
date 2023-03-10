# NDKFFmpeg

This repository is an example of using FFmpeg to compress video.

## Build steps

Refer this [guide](https://trac.ffmpeg.org/wiki/CompilationGuide/Android) in FFmpeg wiki,
recommended to use [ffmpeg-android-maker](https://github.com/Javernaut/ffmpeg-android-maker), see
details below.

1. Install [Android Studio](https://developer.android.google.cn/studio). If you do not need it,
   install command line tools only.
2. Install latest NDK and export environment variables.
   ```bash
   sdkmanager "ndk;${ndk_version}" # For command line tools only
   export ANDROID_SDK_HOME=/path/to/Android_sdk
   export ANDROID_NDK_HOME=${ANDROID_SDK_HOME}/ndk/${ndk_version}
   ```
3. Clone `ffmpeg-android-maker` repository and build.
   ```bash
   git clone https://github.com/Javernaut/ffmpeg-android-maker
   cd ffmpeg-android-maker
   export FFMPEG_ANDROID=`pwd`
   ./ffmpeg-android-maker.sh
   ```
4. The `.so` files are located in `${FFMPEG_ANDROID}/build/ffmpeg/${ANDROID_ABI}/lib`, copy them to
   the project.
   ```bash
   cd /path/to/NDKFFmpeg
   mkdir -p app/src/main/jniLibs/${ANDROID_ABI}
   cp /path/to/*.so app/src/main/jniLibs/${ANDROID_ABI}
   ```
5. Locate the source code of FFmpeg.
    - If compile this project on the same machine used to compile FFmpeg, the source is located
      at `${FFMPEG_ANDROID}/source/ffmpeg/ffmpeg-${ffmpeg_version}`
    - If compile this project on another machine,
      download [source code](https://ffmpeg.org/download.html) of FFmpeg.

6. Set `FFMPEG_SOURCE_DIR` to ensure that CMake can work.

   ```bash
   export FFMPEG_SOURCE_DIR=/path/to/ffmpeg_source
   cd ${FFMPEG_SOURCE_DIR}
   rm VERSION # This file will cause error
   ```

   Remember to copy the following missing headers if compile this project on another machine.

       config.h libavutil/avconfig.h libavutil/ffversion.h

7. Open the project with Android Studio, build and run.
