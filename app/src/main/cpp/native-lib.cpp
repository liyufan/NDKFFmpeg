#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
#include "tools/run.h"
}

#define TAG "JNI_TAG"
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

static jobject j_callback;
static JNIEnv *m_env;

void call_back(uint64_t current, int64_t total) {
    // LOG_D("Compress progress: %llu / %lld", current, total);
    if (j_callback && m_env) {
        auto j_clazz = m_env->GetObjectClass(j_callback);
        auto j_mid = m_env->GetMethodID(j_clazz, "onCompress", "(JJ)V");
        m_env->CallVoidMethod(j_callback, j_mid, (int64_t) current, total);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_ndk_ffmpeg_logic_video_VideoCompress_compressVideo(JNIEnv *env, jobject /* this */,
                                                                    jobjectArray compress_command,
                                                                    jobject callback) {
    j_callback = env->NewGlobalRef(callback);
    m_env = env;

    // 1. Get the length of the command
    int argc = env->GetArrayLength(compress_command);
    // 2. Create a two-dimensional character array and store the command
    char **argv = new char *[argc];
    for (int i = 0; i < argc; ++i) {
        auto j_param = (jstring) env->GetObjectArrayElement(compress_command, i);
        argv[i] = (char *) env->GetStringUTFChars(j_param, nullptr);
        LOG_D("argv[%d] = %s", i, argv[i]);
    }
    // 3. Call the command function to compress
    ffmpeg_main(argc, argv, call_back);
    // 4. Release resources
    for (int i = 0; i < argc; ++i) {
        delete argv[i];
    }
    delete[] argv;
    env->DeleteGlobalRef(j_callback);
}
