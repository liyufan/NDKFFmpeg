# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("ndk_ffmpeg")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        video

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp tools/run.c)

# Include ffmpeg header

target_include_directories(video PRIVATE $ENV{FFMPEG_SOURCE_DIR})

# Add ffmpeg tools source

set(FFMPEG_TOOLS_SOURCE_DIR $ENV{FFMPEG_SOURCE_DIR}/fftools)
file(GLOB FFMPEG_TOOLS_SOURCE ${FFMPEG_TOOLS_SOURCE_DIR}/*.c)
list(REMOVE_ITEM FFMPEG_TOOLS_SOURCE
        ${FFMPEG_TOOLS_SOURCE_DIR}/ffplay.c
        ${FFMPEG_TOOLS_SOURCE_DIR}/ffmpeg.c
        ${FFMPEG_TOOLS_SOURCE_DIR}/ffprobe.c)
target_sources(video PRIVATE ${FFMPEG_TOOLS_SOURCE})

# Link ffmpeg so

set(FFMPEG_LIB_DIR ../jniLibs/${ANDROID_ABI})
target_link_directories(video PRIVATE ${FFMPEG_LIB_DIR})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        video avcodec avdevice avfilter avformat avutil swresample swscale

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
