package com.example.ffmpegcmd.ui.iview;

import java.util.List;

/**
 * Created by Vashon on 2021/8/5.
 */
public interface IMainView extends IView {

    void showList(List list);

    void gotoVideoEditActivity();

    void gotoTestActivity();
    void gotoEditActivity();
}
