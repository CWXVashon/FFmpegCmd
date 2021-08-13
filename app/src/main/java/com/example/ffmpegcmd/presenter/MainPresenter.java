package com.example.ffmpegcmd.presenter;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.bean.MainDividerBean;
import com.example.ffmpegcmd.bean.MainItemBean;
import com.example.ffmpegcmd.bean.MainTitleBean;
import com.example.ffmpegcmd.bean.MediaBean;
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd;
import com.example.ffmpegcmd.ffmpegjava.FFmpegHandler;
import com.example.ffmpegcmd.ui.iview.IMainView;
import com.example.ffmpegcmd.util.FFmpegUtils;
import com.example.ffmpegcmd.util.FFmpegVideoUtils;
import com.example.ffmpegcmd.util.FileUtils;
import com.example.ffmpegcmd.util.JsonUtils;
import com.example.ffmpegcmd.util.ThreadPoolExecutor;

import java.io.File;
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

    private String mOperateType = "";

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
                        mView.showToast("操作开始");
                        mView.operateStart();
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
                        mView.showToast("操作完成");
                        mView.operateEnd();
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
        itemList.add(new MainItemBean("整合", R.mipmap.ic_launcher_round));
        mList.add(itemList);

        mList.add(new MainDividerBean());

        mView.showList(mList);
    }

    public void handleMedia(String name) {
        mOperateType = name;
        ViseLog.d(name);
        switch (name) {
            case "视频编辑":
                mView.gotoVideoEditActivity();
                break;
            case "整合":
                mView.gotoEditActivity();
                break;
            case "测试":
                mView.gotoTestActivity();
                break;
            case "视频拼接":
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file1 = new File(downloadDir, "cat.mp4");
                File file2 = new File(downloadDir, "rabbit.mp4");
                List<String> filePaths = new ArrayList<>();
                filePaths.add(file1.getAbsolutePath());
                filePaths.add(file2.getAbsolutePath());
                videoConcat(1, 1, filePaths, new File(downloadDir, "target.mp4").getAbsolutePath());
                break;
            case "视频倒放":
                videoReverse(
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "rabbit.mp4").getAbsolutePath(),
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "reverse.mp4").getAbsolutePath());
                break;
            default:
        }
    }

    /**
     * 拼接视频，生成的视频宽高与第一个视频宽高一样
     *
     * @param filePaths      需要拼接的文件路径列表
     * @param targetFilePath 生成的文件存放的绝对路径（含文件名）
     */
    public void videoConcat(List<String> filePaths, String targetFilePath) {
        videoConcat(1, 1, filePaths, targetFilePath);
    }

    /**
     * 拼接视频，如果想要生成的视频宽高与第一个视频宽高一样，宽高传 1
     *
     * @param width          生成的目标视频宽
     * @param height         生成的目标视频高
     * @param filePaths      需要拼接的文件路径列表
     * @param targetFilePath 生成的文件存放的绝对路径（含文件名）
     */
    public void videoConcat(int width, int height, List<String> filePaths, String targetFilePath) {
        if (width <= 0 || height <= 0) return;
        if (filePaths == null || filePaths.size() < 2 || TextUtils.isEmpty(targetFilePath)) return;
        List<String> existFiles = new ArrayList<>();
        for (String path : filePaths)
            if (FileUtils.isVideo(path)) existFiles.add(path);
        if (existFiles.size() < 2) return;
        // 存放转码后的临时文件的临时目录
        String tmpDirPath = FileUtils.TMP_DIR_PATH;
        if (!FileUtils.createFileByName(tmpDirPath, true)) return;
        // 存放多个视频文件路径的文本文件
        String fileListPath = tmpDirPath + File.separator + "fileList.txt";
        if (!FileUtils.createFileByName(fileListPath, false)) return;
        // 输出临时文件路径列表
        List<String> tmpFiles = new ArrayList<>();
        for (int i = 0; i < existFiles.size(); i++)
            tmpFiles.add(tmpDirPath + File.separator + String.format(Locale.getDefault(), "tmp_file_%d.ts", i));
        // 存放命令集的列表
        List<String[]> commandList = new ArrayList<>();
        if (width == 1 && height == 1) {    // 生成视频的宽高与第一个视频保持一致
            String firstFile = existFiles.remove(0);
            // 1.统一视频编码与音频编码，视频：libx264、音频：aac，并将第一个视频转码
            commandList.add(FFmpegUtils.transformVideoWithEncode(firstFile, tmpFiles.get(0)));
            // 2.通过 ffprobe 获取第一个视频的流信息与格式，得到宽高
            String json = FFmpegCmd.getInstance().executeFFprobe(FFmpegUtils.probeFormat(firstFile));
            MediaBean mediaBean = JsonUtils.parseMediaFormat(json);
            if (mediaBean.videoBean == null) return;
            // 3.将需要拼接的视频统一转码并设置宽高为第一视频的宽高
            for (int i = 0; i < existFiles.size(); i++)
                commandList.add(FFmpegUtils.transformVideoWithEncode(
                        existFiles.get(i), mediaBean.videoBean.width, mediaBean.videoBean.height, tmpFiles.get(i + 1)));
        } else {    // 自己手动设置生成视频的宽高
            for (int i = 0; i < existFiles.size(); i++)
                commandList.add(FFmpegUtils.transformVideoWithEncode(existFiles.get(i), width, height, tmpFiles.get(i)));
        }
        // 4.拼接视频
        // 4.1格式化需要拼接的文件列表
        StringBuilder builder = new StringBuilder();
        for (String file : tmpFiles)
            builder.append("file ").append("'").append(file).append("'").append(System.getProperty("line.separator"));
        // 4.2将转码后的文件路径以特定格式存放到一个文件
        if (!FileUtils.writeContent2File(fileListPath, builder.toString())) return;
        // 4.3生成拼接命令
        commandList.add(FFmpegUtils.jointVideo(fileListPath, targetFilePath));
        mFFmpegHandler.executeFFmpegCmd(commandList);
    }

    /**
     * 视频倒放
     *
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     */
    private void videoReverse(String sourceFilePath, String targetFilePath) {
        if (!FileUtils.isVideo(sourceFilePath) || TextUtils.isEmpty(targetFilePath)) return;
        mFFmpegHandler.executeFFmpegCmd(FFmpegVideoUtils.reverseVideo(sourceFilePath, targetFilePath));
    }

    // 将 assets 文件夹的测试文件保存在指定路径
    public void saveAssetsFile() {
        ThreadPoolExecutor.INSTANCE.executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                String downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                FileUtils.saveAssetsFileToPath(downloadDir, "cat.mp4");
                FileUtils.saveAssetsFileToPath(downloadDir, "rabbit.mp4");
            }
        });
    }

    // 释放不需要的资源
    public void releaseSource() {
        switch (mOperateType) {
            case "视频拼接":
                // 拼接完成，将临时目录与文件删除
                FileUtils.deleteFolderContainSelf(new File(FileUtils.TMP_DIR_PATH));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.onDestroy();
    }
}
