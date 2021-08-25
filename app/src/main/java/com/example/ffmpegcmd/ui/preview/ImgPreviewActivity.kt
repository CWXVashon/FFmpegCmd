package com.example.ffmpegcmd.ui.preview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ffmpegcmd.databinding.ActivityPreviewImageBinding

/**
 * 图片预览页面
 */
class ImgPreviewActivity : AppCompatActivity() {
    var binding: ActivityPreviewImageBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val path = intent.getStringExtra("path")
        Glide.with(this).load(path).into(binding!!.imgImg)
        binding!!.backBtn.setOnClickListener { v: View? -> finish() }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, path: String?) {
            val intent = Intent(context, ImgPreviewActivity::class.java)
            intent.putExtra("path", path)
            context.startActivity(intent)
        }
    }
}