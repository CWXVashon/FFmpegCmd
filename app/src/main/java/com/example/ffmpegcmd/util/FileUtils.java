package com.example.ffmpegcmd.util;

import com.example.ffmpegcmd.ui.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Vashon on 2021/8/5.
 */
public class FileUtils {

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
        int len;
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
            closeStream(bos);
            closeStream(bis);
        }
    }

    /**
     * 通过路径与文件名创建文件或文件夹
     *
     * @param parent   路径
     * @param fileName 文件名
     * @param isDir    是否创建文件夹
     * @return 创建结果
     */
    public static boolean createFileByName(File parent, String fileName, boolean isDir) {
        File target = new File(parent, fileName);
        if (target.exists()) {
            if (isDir) {
                File[] files = target.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.isFile() && !file.delete())
                            return false;
                    }
                    return target.mkdir();
                } else {
                    return true;
                }
            } else {
                if (target.delete()) {
                    try {
                        return target.createNewFile();
                    } catch (IOException e) {
                        return false;
                    }
                }
                return false;
            }
        } else {
            if (isDir) {
                return target.mkdir();
            } else {
                try {
                    return target.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
        }
    }

    /**
     * 用于关闭流的帮助函数
     *
     * @param stream 流
     */
    public static void closeStream(Object stream) {
        if (stream == null)
            return;
        if (stream instanceof OutputStream) {
            OutputStream os = (OutputStream) stream;
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (stream instanceof InputStream) {
            InputStream is = (InputStream) stream;
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (stream instanceof Writer) {
            Writer writer = (Writer) stream;
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (stream instanceof Reader) {
            Reader reader = (Reader) stream;
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
