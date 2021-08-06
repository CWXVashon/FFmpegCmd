package com.example.ffmpegcmd.util;

import android.os.Environment;
import android.text.TextUtils;

import com.example.ffmpegcmd.ui.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Vashon on 2021/8/5.
 */
public class FileUtils {

    private static final String TYPE_MP4 = "mp4";
    private static final String TYPE_MKV = "mkv";
    private static final String TYPE_WEBM = "webm";
    private static final String TYPE_AVI = "avi";
    private static final String TYPE_WMV = "wmv";
    private static final String TYPE_FLV = "flv";
    private static final String TYPE_TS = "ts";
    private static final String TYPE_M3U8 = "m3u8";
    private static final String TYPE_3GP = "3gp";
    private static final String TYPE_MOV = "mov";
    private static final String TYPE_MPG = "mpg";

    // 临时文件存放目录的绝对路径
    public static final String TMP_DIR_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "ffmpegCmdTmpDir";

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
     * @param filePath 文件的绝对路径（含文件名）
     * @param isDir    是否创建文件夹
     * @return 创建结果
     */
    public static boolean createFileByName(String filePath, boolean isDir) {
        if (TextUtils.isEmpty(filePath)) return false;
        File target = new File(filePath);
        if (target.exists()) {
            if (isDir) {
                if (target.isDirectory()) {
                    deleteFolderWithoutSelf(target);
                    return true;
                } else {
                    deleteFolderContainSelf(target);
                    return target.mkdir();
                }
            } else {
                if (target.isDirectory()) {
                    deleteFolderContainSelf(target);
                    try {
                        return target.createNewFile();
                    } catch (IOException e) {
                        return false;
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
     * 递归删除指定目录下的所有文件或文件夹（包含指定目录）
     *
     * @param srcFolder 指定目录
     */
    public static void deleteFolderContainSelf(File srcFolder) {
        if (srcFolder != null) {
            deleteFolderWithoutSelf(srcFolder);
            srcFolder.delete();
        }
    }

    /**
     * 递归删除指定目录下的所有文件或文件夹（不含指定目录）
     *
     * @param srcFolder 指定目录
     */
    public static void deleteFolderWithoutSelf(File srcFolder) {
        if (srcFolder != null) {
            File[] fileList = srcFolder.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isDirectory())
                        deleteFolderContainSelf(file);
                    else
                        file.delete();
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

    /**
     * 判断路径下的文件是否为视频文件
     *
     * @param filePath 文件路径
     * @return true 是视频，false 不是视频
     */
    public static boolean isVideo(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) return false;
        String path = filePath;
        path = path.toLowerCase();
        return (path.endsWith(TYPE_MP4)
                || path.endsWith(TYPE_MKV)
                || path.endsWith(TYPE_WEBM)
                || path.endsWith(TYPE_WMV)
                || path.endsWith(TYPE_AVI)
                || path.endsWith(TYPE_FLV)
                || path.endsWith(TYPE_3GP)
                || path.endsWith(TYPE_TS)
                || path.endsWith(TYPE_M3U8)
                || path.endsWith(TYPE_MOV)
                || path.endsWith(TYPE_MPG));
    }

    /**
     * 将指定的字符串数据写到指定文件
     *
     * @param filePath 文件路径
     * @param content  字符串数据
     */
    public static boolean writeContent2File(String filePath, String content) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(content)) return false;
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(bw);
        }
        return true;
    }
}
