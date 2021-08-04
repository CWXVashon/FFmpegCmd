#!/bin/sh
#
# Created by Vashon on 2021/8/01 10:46
# 用于构建 android 平台使用的 libx264.so 文件的脚本

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
  TOOLNAME_BASE="aarch64-linux-android"
  COMPILER_BASE="aarch64-linux-android"
  AOSP_ABI="arm64-v8a"
  AOSP_ARCH="arm64"
  OPENSSL_ARCH="android-arm64"
  HOST="aarch64-linux-android"
  FF_EXTRA_CFLAGS="-DANDROID -Wall -fPIC"
  FF_CFLAGS="-DANDROID -Wall -fPIC"
}

init_arm32() {
  echo "构建平台为：armeabi-v7a"
  TOOLNAME_BASE="arm-linux-androideabi"
  COMPILER_BASE="armv7a-linux-androideabi"
  AOSP_ABI="armeabi-v7a"
  AOSP_ARCH="armeabi-v7a"
  OPENSSL_ARCH="android-arm"
  HOST="arm-linux-androideabi"
  FF_EXTRA_CFLAGS="-DANDROID -Wall -fPIC"
  FF_CFLAGS="-DANDROID -Wall -fPIC"
}

init_x86_64() {
  echo "构建平台为：x86_64"
  TOOLNAME_BASE="x86_64-linux-android"
  COMPILER_BASE="x86_64-linux-android"
  AOSP_ABI="x86_64"
  AOSP_ARCH="x86_64"
  OPENSSL_ARCH="android-x86_64"
  HOST="x86_64-linux-android"
  FF_EXTRA_CFLAGS="-DANDROID -Wall -fPIC"
  FF_CFLAGS="-DANDROID -Wall -fPIC"
}

init_x86() {
  echo "构建平台为：x86"
  TOOLNAME_BASE="i686-linux-android"
  COMPILER_BASE="i686-linux-android"
  AOSP_ABI="x86"
  AOSP_ARCH="x86"
  OPENSSL_ARCH="android-x86"
  HOST="i686-linux-android"
  FF_EXTRA_CFLAGS="-DANDROID -Wall -fPIC"
  FF_CFLAGS="-DANDROID -Wall -fPIC"
}

package_params() {
  # 目标文件输出目录
  OUTPUT=$(pwd)/android/$AOSP_ABI
  # 系统库文件所在目录
  SYSROOT=$TOOLCHAIN/sysroot
  CROSS_PREFIX=$TOOLCHAIN/bin/$TOOLNAME_BASE-
  export CC=$TOOLCHAIN/bin/$COMPILER_BASE$API-clang
  export CXX=$TOOLCHAIN/bin/$COMPILER_BASE$API-clang++
  export CXXFLAGS=$FF_EXTRA_CFLAGS
  export CFLAGS=$FF_CFLAGS
  export AR="${CROSS_PREFIX}ar"
  export LD="${CROSS_PREFIX}ld"
  export AS="${CROSS_PREFIX}as"
  export NM="${CROSS_PREFIX}nm"
  export STRIP="${CROSS_PREFIX}strip"
  export RANLIB="${CROSS_PREFIX}ranlib"
}

build() {
  echo "x264 编译开始"
  ./configure \
  --prefix=$OUTPUT \
  --enable-static \
  --enable-pic \
  --disable-asm \
  --host=$HOST \
  --cross-prefix=$CROSS_PREFIX \
  --sysroot=$SYSROOT \
  --extra-cflags="$FF_CFLAGS"
  
  # 设置编译用的核心数
  make -j12
  make install
  make clean

  echo "x264 编译结束"
}

for i in "$@"; do
  case $i in
    1)
    init_arm64
    package_params
    build
    ;;
    2)
    init_arm32
    package_params
    build
    ;;
    3)
    init_x86_64
    package_params
    build
    ;;
    4)
    init_x86
    package_params
    build
    ;;
  esac
done