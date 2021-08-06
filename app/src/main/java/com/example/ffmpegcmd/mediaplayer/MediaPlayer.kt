package com.example.ffmpegcmd.mediaplayer

import android.view.SurfaceHolder

class MediaPlayer {

    external fun setDataSource(path: String, surfaceHolder: SurfaceHolder?)

    external fun start()

    external fun stop()

    external fun seekTo()

    external fun release()

    external fun pause()
}