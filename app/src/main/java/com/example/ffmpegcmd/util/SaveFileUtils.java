package com.example.ffmpegcmd.util;

import com.example.ffmpegcmd.ui.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vashon on 2021/8/5.
 * 用于将 assets 文件夹的测试文件保存在指定目录
 */
public class SaveFileUtils {

    /**
     * 将 assets 文件夹的测试文件保存在指定路径
     *
     * @param path     存放文件的路径，不包含文件名
     * @param fileName 文件名
     */
    public static void saveAssetsFileToPath(String path, String fileName) {
        File saveFile = new File(path, fileName);
        if (saveFile.exists())
            return;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            if (saveFile.createNewFile()) {
                bis = new BufferedInputStream(app.sContext.getResources().getAssets().open(fileName));
                bos = new BufferedOutputStream(new FileOutputStream(saveFile));
                while ((len = bis.read(buff)) != -1)
                    bos.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
