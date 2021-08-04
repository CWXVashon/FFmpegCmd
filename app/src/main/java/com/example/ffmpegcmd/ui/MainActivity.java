package com.example.ffmpegcmd.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.ffmpegcmd.adapter.MainAdapterRV;
import com.example.ffmpegcmd.bean.MainDividerBean;
import com.example.ffmpegcmd.bean.MainItemBean;
import com.example.ffmpegcmd.bean.MainTitleBean;
import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List mList;
    private MainAdapterRV mMainAdapterRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPermission();
        initRecyclerView();
        initData();
    }

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(permissions[2]) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 123);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mMainAdapterRV = new MainAdapterRV(new MainAdapterRV.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                switch (name) {
                    case "视频编辑":
                        VideoHandleActivity_Java.start(MainActivity.this);
                        break;
                    default:
                }
            }
        });
        recyclerView.setAdapter(mMainAdapterRV);
    }

    private void initData() {
        mList = new ArrayList();
        mList.add(new MainTitleBean("热门工具"));

        List<MainItemBean> itemList = new ArrayList<>();
        itemList.add(new MainItemBean("视频编辑", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("图片相册", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("手持弹幕", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("人像抠图", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        itemList = new ArrayList<>();
        itemList.add(new MainItemBean("区域裁剪", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("多格视频", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("更换音乐", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("视频拼接", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        mList.add(new MainDividerBean());
        mList.add(new MainTitleBean("必备工具"));

        itemList = new ArrayList<>();
        itemList.add(new MainItemBean("合拍视频", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("去水印", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("提取音频", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("变快变慢", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        itemList = new ArrayList<>();
        itemList.add(new MainItemBean("画布比例", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("GIF动画", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("镜像视频", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("视频倒放", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        itemList = new ArrayList<>();
        itemList.add(new MainItemBean("人像动漫", R.mipmap.ic_launcher_round));
        itemList.add(new MainItemBean("精美滤镜", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        mList.add(new MainDividerBean());

        mMainAdapterRV.setList(mList);
    }

}