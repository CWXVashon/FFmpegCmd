package com.example.ffmpegcmd.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ffmpegcmd.adapter.MainAdapterRV;
import com.example.ffmpegcmd.databinding.ActivityMainBinding;
import com.example.ffmpegcmd.presenter.MainPresenter;
import com.example.ffmpegcmd.ui.iview.IMainView;
import com.example.ffmpegcmd.ui.preview.VideoPreviewActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import x.com.base.toast.U_Toast;

public class MainActivity extends AppCompatActivity implements IMainView {

    private ActivityMainBinding binding;
    private LinearLayout mProgressLl;
    private TextView mProgressTv;
    private MainAdapterRV mMainAdapterRV;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPresenter = new MainPresenter(this);

        initPermission();
        initView();
        initRecyclerView();
    }

    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.saveAssetsFile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.saveAssetsFile();
    }

    private void initView() {
        mProgressLl = binding.progressLl;
        mProgressTv = binding.progressTv;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mMainAdapterRV = new MainAdapterRV(new MainAdapterRV.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                mPresenter.handleMedia(name);
            }
        });
        recyclerView.setAdapter(mMainAdapterRV);
        mPresenter.initData();
    }

    @Override
    public void showList(List list) {
        mMainAdapterRV.setList(list);
    }

    @Override
    public void gotoVideoEditActivity() {
        VideoHandleActivity_Java.start(this);
    }

    @Override
    public void gotoTestActivity() {
        startActivity(new Intent(this, TestActivity.class));
    }

    @Override
    public void gotoPreview(String path) {
        VideoPreviewActivity.start(this,path);
    }

    @Override
    public void gotoEditActivity() {
        startActivity(new Intent(this, VideoEditActivity.class));
    }

    @Override
    public void operateStart() {

    }

    @Override
    public void operateEnd() {
        mPresenter.releaseSource();
    }

    @Override
    public void showToast(String message) {
        U_Toast.show(message);
    }

    @Override
    public void showLoading(String message) {
        mProgressLl.setVisibility(View.VISIBLE);
        mProgressTv.setText(message);
    }

    @Override
    public void hideLoading() {
        mProgressLl.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}