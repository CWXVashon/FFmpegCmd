package com.example.ffmpegcmd.presenter;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.bean.MainDividerBean;
import com.example.ffmpegcmd.bean.MainItemBean;
import com.example.ffmpegcmd.bean.MainTitleBean;
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd;
import com.example.ffmpegcmd.ffmpegjava.FFmpegHandler;
import com.example.ffmpegcmd.ffmpegjava.OnHandleListener;
import com.example.ffmpegcmd.ui.iview.IMainView;
import com.example.ffmpegcmd.util.FFmpegUtils;
import com.example.ffmpegcmd.util.SaveFileUtils;
import com.example.ffmpegcmd.util.ThreadPoolExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import x.com.log.ViseLog;

/**
 * Created by Vashon on 2021/8/5.
 */
public class MainPresenter extends BasePresenter<IMainView> {

    private MainHandler mMainHandler;
    private FFmpegHandler mFFmpegHandler;
    private List mList;

    private static class MainHandler extends Handler {

        private IMainView mView;

        public MainHandler(IMainView view) {
            super(Looper.getMainLooper());
            this.mView = view;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mView != null) {
                switch (msg.what) {
                    case FFmpegHandler.STATE_START:
                        mView.showToast("转码开始");
                        break;
                    case FFmpegHandler.STATE_MESSAGE:
                        mView.showToast((String) msg.obj);
                        break;
                    case FFmpegHandler.STATE_PROGRESS:
                        int position = msg.arg1;
//                        int duration = msg.arg2;
                        mView.showLoading(position + "%");
                        break;
                    case FFmpegHandler.STATE_FINISH:
                        mView.hideLoading();
                        mView.showToast("转码完成");
                        break;
                    default:
                        mView.showToast("未知信息");
                }
            }
        }

        void onDestroy() {
            mView = null;
        }
    }

    public MainPresenter(IMainView view) {
        super(view);
    }

    @Override
    protected void init() {
        mMainHandler = new MainHandler(mView);
        mFFmpegHandler = new FFmpegHandler(mMainHandler);
        mList = new ArrayList();
    }

    public void initData() {
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
        itemList.add(new MainItemBean("测试", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        mList.add(new MainDividerBean());

        mView.showList(mList);
    }

    public void handleMedia(String name) {
        switch (name) {
            case "视频编辑":
                mView.gotoVideoEditActivity();
                break;
            case "测试":
                mView.gotoTestActivity();
                break;
            case "视频拼接":
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file1 = new File(downloadDir, "cat.mp4");
                File file2 = new File(downloadDir, "rabbit.mp4");
                List<File> filePaths = new ArrayList<>();
                filePaths.add(file1);
                filePaths.add(file2);
                videoConcat(filePaths);
                break;
            case "视频倒放":
                videoReverse();
                break;
            default:
        }
    }

    private void videoConcat(List<File> filePaths) {
        if (filePaths == null || filePaths.size() < 2)
            return;
        List<String> existFiles = new ArrayList<>();
        for (File file : filePaths) {
            if (file.exists())
                existFiles.add(file.getAbsolutePath());
        }
        if (existFiles.size() < 2)
            return;
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // 存放多个视频文件路径的文本文件
        File fileList = new File(downloadDir, "fileList.txt");
        if (!fileList.exists()) {
            try {
                if (!fileList.createNewFile())
                    return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 存放转码后的临时文件的临时目录
        File tmpDir = new File(downloadDir, "tmpDir");
        if (tmpDir.exists()) {
            File[] files = tmpDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile()) file.delete();
                }
            }
        } else {
            if (!tmpDir.mkdir())
                return;
        }
        // 生成输出文件路径列表
        List<String> tmpFiles = new ArrayList<>();
        for (int i = 0; i < existFiles.size(); i++) {
            tmpFiles.add(tmpDir.getAbsolutePath() + File.separator + String.format(Locale.getDefault(), "tmp_file_%d", i));
        }
        // 存放命令集的列表
        List<String[]> commandList = new ArrayList<>();
        String firstFile = existFiles.remove(0);
        // 1.统一视频编码与音频编码，视频：libx264、音频：aac，并将第一个视频转码
        commandList.add(FFmpegUtils.transformVideoWithEncode(firstFile, tmpFiles.get(0)));
        // 2.通过 ffprobe 获取第一个视频的流信息与格式，得到宽高
        String json = FFmpegCmd.getInstance().executeFFprobe(FFmpegUtils.probeFormat(tmpFiles.get(0)));


        // TODO: 2021/8/5 vashon
        // 1.统一视频编码与音频编码，视频：libx264、音频：aac，并将第一个视频转码
        // 2.通过 ffprobe 获取第一个视频的流信息与格式，得到宽高
        // 3.将需要拼接的视频统一转码并设置宽高为第一视频的宽高
        // 4.拼接视频
    }

    private void videoReverse() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File srcFile = new File(downloadDir, "cat.mp4");
        File targetFile = new File(downloadDir, "reverse.mp4");
        if (srcFile.exists()) {
            Log.e("--------------", "文件存在");
        } else {
            Log.e("--------------", "文件不存在");
        }
        String srcPath = srcFile.getAbsolutePath();
        String targetPath = targetFile.getAbsolutePath();
        mFFmpegHandler.executeFFmpegCmd(FFmpegUtils.reverseVideo(srcPath, targetPath));
    }

    // 将 assets 文件夹的测试文件保存在指定路径
    public void saveAssetsFile() {
        ThreadPoolExecutor.INSTANCE.executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                String downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                SaveFileUtils.saveAssetsFileToPath(downloadDir, "cat.mp4");
                SaveFileUtils.saveAssetsFileToPath(downloadDir, "rabbit.mp4");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.onDestroy();
    }
}
