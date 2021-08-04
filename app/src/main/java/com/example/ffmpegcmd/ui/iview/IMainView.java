package com.example.ffmpegcmd.ui.iview;

import java.util.List;

/**
 * Created by Vashon on 2021/8/5.
 */
public interface IMainView extends IView {

    void showList(List list);

    void gotoVideoEditActivity();

    void gotoTestActivity();
    void gotoPreview(String path);
    void gotoEditActivity();

    /**
     * 视频操作开始
     */
    void operateStart();

    /**
     * 视频操作结束
     */
    void operateEnd();
}
