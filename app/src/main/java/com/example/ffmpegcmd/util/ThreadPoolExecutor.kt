package com.example.ffmpegcmd.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPoolExecutor {

    fun executeSingleThreadPool(runnable: Runnable): ExecutorService{
        val executor = Executors.newSingleThreadExecutor()
        executor.submit(runnable)
        return executor
    }
}