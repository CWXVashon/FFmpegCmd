#!/bin/sh
#
# Created by Vashon on 2021/7/27 10:46
# 用于构建 android 平台使用的 libffmpeg.so 文件的脚本
:<<EOF
  使用说明：运行本脚本，并传递参数，用于控制构建出不同架构的库文件

  例如：./build_ffmpeg_android.sh 1
  执行结果为构建出 arm64-v8a 架构的库文件，若有多个参数，通过空格分隔开
  例如：./build_ffmpeg_android.sh 1 2 3 4
  执行结果为构建出 arm64-v8a、armeabi-v7a、x86_64、x86 架构的库文件

  参数说明：对应的数字表示构建出的平台架构的库文件
  1：arm64-v8a
  2：armeabi-v7a
  3：x86_64
  4：x86
EOF

# 构建的最低支持 API 等级
API=21
# 在什么系统上构建，mac：darwin，linux：linux，windows：windows
OS_TYPE=darwin
# 自己本机 NDK 所在目录
NDK=/Users/mac/Library/Android/sdk/ndk/21.4.7075529
# 交叉编译工具链所在目录
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$OS_TYPE-x86_64

init_arm64() {
  echo "构建平台为：arm64-v8a"
  ABI=arm64-v8a
  ARCH1=aarch64
  ARCH2=aarch64
  ANDROID=android
  CPU=armv8-a
  PRE_CFLAGS="-march=$CPU"
  GCC_L=$NDK/toolchains/$ARCH1-linux-$ANDROID-4.9/prebuilt/$OS_TYPE-x86_64/lib/gcc/$ARCH1-linux-$ANDROID/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_arm32() {
  echo "构建平台为：armeabi-v7a"
  ABI=armeabi-v7a
  ARCH1=arm
  ARCH2=armv7a
  ANDROID=androideabi
  CPU=armv7-a
  PRE_CFLAGS="-march=$CPU -mcpu=cortex-a8 -mfpu=vfpv3-d16 -mfloat-abi=softfp -mthumb"
  GCC_L=$NDK/toolchains/$ARCH1-linux-$ANDROID-4.9/prebuilt/$OS_TYPE-x86_64/lib/gcc/$ARCH1-linux-$ANDROID/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_x86_64() {
  echo "构建平台为：x86_64"
  ABI=x86_64
  ARCH1=x86_64
  ARCH2=x86_64
  ANDROID=android
  CPU=x86-64
  PRE_CFLAGS="-march=$CPU -msse4.2 -mpopcnt -m64 -mtune=intel"
  GCC_L=$NDK/toolchains/$ABI-4.9/prebuilt/$OS_TYPE-x86_64/lib/gcc/$ARCH1-linux-$ANDROID/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_x86() {
  echo "构建平台为：x86"
  ABI=x86
  ARCH1=i686
  ARCH2=i686
  ANDROID=android
  CPU=i686
  PRE_CFLAGS="-march=$CPU -mtune=intel -mssse3 -mfpmath=sse -m32"
  GCC_L=$NDK/toolchains/$ABI-4.9/prebuilt/$OS_TYPE-x86_64/lib/gcc/$ARCH1-linux-$ANDROID/4.9.x
  # x86 架构必须设置这样，因为移除了寄存器，enable 会导致编译不通过
  ASM_SWITCH='--disable-asm'
}

package_params() {
  # 目标文件输出目录
  OUTPUT=$(pwd)/android/$ABI
  # 编译好的 x264 库所在目录
  X264_LIB_DIR=$(pwd)/android/libx264/$ABI
  # 系统库文件所在目录
  SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/$ARCH1-linux-$ANDROID
  # 传递给编译器的标志
  EXTRA_CFLAGS="-Os -fpic $PRE_CFLAGS -I$OUTPUT/include -I$X264_LIB_DIR/include"
  # 传递给链接器的标志
  EXTRA_LDFLAGS="-lc -ldl -lm -lz -llog -lgcc -L$OUTPUT/lib -L$X264_LIB_DIR/lib"
}

build() {
  echo "FFmpeg 编译开始"
  ./configure \
  --target-os=android \
  --prefix=$OUTPUT \
  --arch=$ARCH1 \
  --cpu=$CPU \
  --sysroot=$TOOLCHAIN/sysroot \
  --enable-cross-compile \
  --enable-neon \
  --enable-hwaccels \
  $ASM_SWITCH \
  --enable-small \
  --enable-avcodec \
  --enable-avformat \
  --enable-avutil \
  --enable-swresample \
  --enable-swscale \
  --enable-avfilter \
  --enable-postproc \
  --enable-network \
  --enable-bsfs \
  --enable-postproc \
  --enable-filters \
  --enable-encoders \
  --enable-gpl \
  --enable-muxers \
  --enable-parsers \
  --enable-protocols \
  --enable-nonfree \
  --enable-jni \
  --enable-mediacodec \
  --enable-libx264 \
  --enable-static \
  --disable-shared \
  --disable-doc \
  --disable-avdevice \
  --disable-avresample \
  --disable-ffmpeg \
  --disable-ffplay \
  --disable-ffprobe \
  --disable-debug \
  --disable-symver \
  --disable-indevs \
  --disable-outdevs \
  --enable-decoders \
  --enable-demuxers \
  --extra-cflags="$EXTRA_CFLAGS" \
  --extra-ldflags="$EXTRA_LDFLAGS" \
  --cc=$TOOLCHAIN/bin/$ARCH2-linux-$ANDROID$API-clang \
  --cxx=$TOOLCHAIN/bin/$ARCH2-linux-$ANDROID$API-clang++ \
  --cross-prefix=$TOOLCHAIN/bin/$ARCH1-linux-$ANDROID-

  make clean all
  # 设置编译用的核心数
  make -j12
  make install

  echo "FFmpeg 编译结束"
}

package_library() {
  echo "多个库打包开始"
  # 调用链接器进行打包
  $TOOLCHAIN/bin/$ARCH1-linux-$ANDROID-ld -L$OUTPUT/lib -L$GCC_L -L$X264_LIB_DIR/lib \
    -rpath-link=$SYSROOT_L/$API -L$SYSROOT_L/$API -soname libffmpeg.so \
    -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o $OUTPUT/libffmpeg.so \
    -lavcodec -lpostproc -lavfilter -lswresample -lavformat -lavutil -lswscale -lgcc -lx264 \
    -lc -ldl -lm -lz -llog \
    --dynamic-linker=/system/bin/linker
    # 设置动态链接器，不同平台的不同，android 使用的是 /system/bin/linker
  echo "多个库打包结束"
}

for i in "$@"; do
  case $i in
    1) 
    init_arm64 
    package_params
    build
    package_library
    ;;
    2) 
    init_arm32 
    package_params
    build
    package_library
    ;;
    3) 
    init_x86_64 
    package_params
    build
    package_library
    ;;
    4) 
    init_x86 
    package_params
    build
    package_library
    ;;
  esac
done