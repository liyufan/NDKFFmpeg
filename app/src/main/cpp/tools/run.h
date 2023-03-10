//
// Created by Administrator on 2023/3/4.
//

#ifndef NDK_FFMPEG_RUN_H
#define NDK_FFMPEG_RUN_H

int ffmpeg_main(int argc, char **argv, void (*call_back)(uint64_t, int64_t));

#endif //NDK_FFMPEG_RUN_H
