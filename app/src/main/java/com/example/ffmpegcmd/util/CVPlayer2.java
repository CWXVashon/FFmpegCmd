package com.example.ffmpegcmd.util;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.hhplayer.R;
import com.xing.hhplayer.common.CPlayer;
import com.xing.hhplayer.common.View.PLiveView;
import com.xing.hhplayer.common.View.PTVView;
import com.xing.hhplayer.common.base.listener.ControlViewInterface;
import com.xing.hhplayer.common.base.listener.OnPlayerListener;
import com.xing.hhplayer.common.base.player.HHPlayerCore;
import com.xing.hhplayer.common.bean.TvList.TvDataList;
import com.xing.hhplayer.common.bean.TvList.TvView;
import com.xing.hhplayer.common.bean.Type.PlayerPrePareState;
import com.xing.hhplayer.common.bean.Type.PlayerViewState;
import com.xing.hhplayer.common.util.VLogUtil;
import com.xing.hhplayer.common.util.V_screen;
import com.xing.hhplayer.common.util.V_view;

import java.util.List;
import java.util.Map;

/**
 * 播放器控制类，也就是我们的使用类
 */
public class CVPlayer2 extends CPlayer {
    private final FragmentActivity mActivity;
    //播放列表相关
    private String title;
    private List<TvDataList> tvLists;//播放列表
    private int currentPlayIndex = -1;//列表中播放第几个，没列表时为-1
    private boolean isChangeBrightness = false;//是否设置改变亮度
    private final FrameLayout viewGroup;//视频容器
    private int screenScale = PlayerViewState.SCREEN_SCALE_DEFAULT;//默认显示比例
    private int screenType = PlayerViewState.SCREEN_DEFAULT;//默认显示正常大小
    private OnPlayerListener<Integer> screenChangeListener;
    private ControlViewInterface cvInterface;
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private int mode = 1;//1.竖屏 2横屏

    @Override
    protected void progressListener(Long along) {
        if (cvInterface != null) {
            cvInterface.changeProgress(along);
        }
    }

    @Override
    protected void preparedListener(Object o) {
        if (mode == 1) {
            screenScale = PlayerViewState.SCREEN_SCALE_DEFAULT;
        } else {
            screenScale = PlayerViewState.SCREEN_SCALE_DEFAULT_LANDSCAPE;
        }
        if (cvInterface != null) {
            cvInterface.getPrePareState(PlayerPrePareState.PREPARED);
        }
        changeScale(screenScale);
        if (viewGroup != null) {
            defaultWidth = viewGroup.getWidth();
            defaultHeight = viewGroup.getHeight();
            Log.d(hhPlayerCore.TAG, "容器包含View个数：" + viewGroup.getChildCount());
            Log.d(hhPlayerCore.TAG, "容器大小：" + viewGroup.getWidth() + "," + viewGroup.getHeight());
            View view = viewGroup.findViewById(R.id.video_normal_id);
            if (view != null) {
                Log.d(hhPlayerCore.TAG, "视频View大小：" + view.getWidth() + "," + view.getHeight());
            } else {
                Log.d(hhPlayerCore.TAG, "视频View大小：0,0");
            }
        } else {
            Log.d(hhPlayerCore.TAG, "容器大小：0,0");
            Log.d(hhPlayerCore.TAG, "视频View大小：0,0");
        }
    }

    @Override
    protected void infoListener(Integer arg) {
        if (cvInterface != null) {
            cvInterface.getPlayBuffer(arg);
        }
    }

    @Override
    protected void playCompletionListener(Object o) {
        if (isChangeBrightness) {//播放完恢复原来的亮度，没设置亮度则不启用
            setScreenBrightness(getSystemScreenBrightness());
        }
        if (cvInterface != null) {
            cvInterface.getPlayState(hhPlayerCore.mPlayerState);
        }
    }

    @Override
    protected void errorListener(Object o) {
        if (cvInterface != null) {
            cvInterface.getPrePareState(PlayerPrePareState.ERROR);
        }
    }

    /***********************************初始化播放器部分**************************************************************/

    //使用外部的suraface
    public CVPlayer2(FragmentActivity context, HHPlayerCore hhPlayerCore, Surface surface) {
        this(context, hhPlayerCore, TvView.SURFACE, null, surface);
    }

    //使用外部的viewGroup，需要选择NEWSURFACE、NEWTEXTURE
    public CVPlayer2(FragmentActivity context, HHPlayerCore hhPlayerCore, TvView playerView, FrameLayout viewGroup) {
        this(context, hhPlayerCore, playerView, viewGroup, null);
    }

    //使用NEWSURFACE、NEWTEXTURE必须传viewGroup
    public CVPlayer2(FragmentActivity mActivity, HHPlayerCore hhPlayerCore, TvView playerView, FrameLayout viewGroup, Surface surface) {
        super(mActivity, hhPlayerCore);
        this.mActivity = mActivity;
        this.viewGroup = viewGroup;
        switch (playerView) {
            case NEWSURFACE:
                initSurfaceView();
                break;
            case NEWTEXTURE:
                initTextureView();
                break;
            case SURFACE:
                if (surface != null) {
                    initSurface(surface);
                } else {
                    Log.e(hhPlayerCore.ETAG, "surface为空");
                }
                break;
            case HHGLSURFACE:
//                initHHGLSurfaceView();
                break;
            case MUSIC:
                break;
        }
    }

    /***********************************添加其他view**************************************************************/
//    public void addLiveView() {
//        mode = 1;
//        addExtraView(new PLiveView(mActivity).initPlayer(this));
//    }
//
//    public void addTvView() {
//        mode = 2;
//        addExtraView(new PTVView(mActivity).initPlayer(this));
//    }

    public void addExtraView(View view) {
        addExtraView(view, viewGroup.getChildCount());
    }

    public void addExtraView(View view, int index) {
        if (viewGroup == null) {
            Log.e(hhPlayerCore.ETAG, "必须设置viewGroup");
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            viewGroup.addView(view, index, params);
        }
    }

    public void removeExtraView(String tag) {
        if (viewGroup == null) {
            Log.e(hhPlayerCore.ETAG, "必须设置viewGroup");
        } else {
            View view = viewGroup.findViewWithTag(tag);
            if (view != null) {
                removeExtraView(view);
            }
        }
    }

    public void removeExtraView(int index) {
        if (viewGroup == null) {
            Log.e(hhPlayerCore.ETAG, "必须设置viewGroup");
        } else {
            viewGroup.removeViewAt(index);
        }
    }

    public void removeExtraView(View view) {
        if (viewGroup == null) {
            Log.e(hhPlayerCore.ETAG, "必须设置viewGroup");
        } else {
            viewGroup.removeView(view);
        }
    }

    /**
     * 检查videoView是否已经添加
     *
     * @return 结果
     */
    public boolean checkVideoAdd() {
        View view = viewGroup.findViewById(R.id.video_normal_id);
        return view != null;
    }

    /***********************************设置部分**************************************************************/
    //设置播放是否屏幕常亮
    public void setScreenOnWhilePlaying(boolean isScreenOn) {
        hhPlayerCore.setScreenOnWhilePlaying(isScreenOn);
    }

    //设置屏幕亮度
    public void setScreenBrightness(int brightness) {
        Window localWindow = mActivity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = brightness / 100.0F;
        localWindow.setAttributes(localLayoutParams);
        isChangeBrightness = true;
    }

    //设置视频大小,hhsurfaceview设置显示比例时有用，待优化
    public void setVideoSize(int width, int height) {
        View old = viewGroup.findViewById(R.id.video_normal_id);
        if (old != null) {
            V_screen.setViewSize(old, width, height);
        }
    }

    private void rotateScreen() {
        //判断当前是否为横屏,判断是否旋转
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            V_screen.setViewSize(viewGroup, V_screen.getDeviceWidth(mActivity), V_screen.getDeviceHeight(mActivity));
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            V_screen.setViewSize(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT, defaultHeight);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
    }

    public void fullScreen() {
        screenType = PlayerViewState.SCREEN_FULL;
        screenScale = PlayerViewState.SCREEN_SCALE_DEFAULT;
        rotateScreen();
//        //由于屏幕旋转，view的宽高更新需要点时间
        viewGroup.postDelayed(() -> {
            changeScale(screenScale);
            if (screenChangeListener != null)
                screenChangeListener.onListen(screenType);
        }, 200);
    }

    public void exitFullScreen() {
        screenType = PlayerViewState.SCREEN_DEFAULT;
        screenScale = PlayerViewState.SCREEN_SCALE_DEFAULT;
        rotateScreen();
        viewGroup.postDelayed(() -> {
            changeScale(screenScale);
            if (screenChangeListener != null)
                screenChangeListener.onListen(screenType);
        }, 200);
    }

    /**
     * 改变显示比例
     *
     * @param screenScaleType 显示比例类型
     */
    public void changeScale(int screenScaleType) {
        Log.d(hhPlayerCore.TAG, PlayerViewState.getScaleStr(screenScaleType));
        switch (screenScaleType) {
            case PlayerViewState.SCREEN_SCALE_DEFAULT://0
                int height = (int) ((viewGroup.getWidth() * 1.0f) / (V_screen.getWHScale(getMediaWidth(), getMediaHeight())));
                if (height > viewGroup.getHeight()) {//高锁定
//                if (viewGroup.getWidth() >= viewGroup.getHeight()) {
                    setVideoSize((int) (V_screen.getWHScale(getMediaWidth(), getMediaHeight()) * viewGroup.getHeight()), viewGroup.getHeight());
                } else {
                    setVideoSize(viewGroup.getWidth(), (int) (viewGroup.getWidth() / V_screen.getWHScale(getMediaWidth(), getMediaHeight())));
                }
                break;
            case PlayerViewState.SCREEN_SCALE_DEFAULT_LANDSCAPE://0
                setVideoSize(V_screen.getHeightByScaleWH(getMediaHeight() * 1f / getMediaWidth(), viewGroup.getHeight()), viewGroup.getHeight());
                break;
            case PlayerViewState.SCREEN_SCALE_4_3:
            case PlayerViewState.SCREEN_SCALE_16_9:
            case PlayerViewState.SCREEN_SCALE_1_1:
            case PlayerViewState.SCREEN_SCALE_3_4:
            case PlayerViewState.SCREEN_SCALE_9_16:
                if (viewGroup.getWidth() >= viewGroup.getHeight()) {
                    setVideoSize((int) (PlayerViewState.getScaleByScreenType(screenScaleType) * viewGroup.getHeight()), viewGroup.getHeight());
                } else {
                    setVideoSize(viewGroup.getWidth(), (int) (viewGroup.getWidth() / PlayerViewState.getScaleByScreenType(screenScaleType)));
                }
                break;
            case PlayerViewState.SCREEN_SCALE_FULL:
                VLogUtil.showLog(viewGroup.getWidth() + "  " + viewGroup.getHeight());
                setVideoSize(viewGroup.getWidth(), viewGroup.getHeight());
                break;
            case PlayerViewState.SCREEN_SCALE_FULL_LANDSCAPE:
                setVideoSize(viewGroup.getHeight(), viewGroup.getWidth());
                break;
        }
    }

//    public void takeBitmap(OnPlayerListener<Bitmap> takeBitmapListener) {
//        if (mSurfaceView != null) {
//            mSurfaceView.takeBitmap(takeBitmapListener);
//        } else {
//            Log.e(hhPlayerCore.ETAG, "仅支持GLSurfaceView");
//        }
//    }
//
//    public void setEffect(ShaderInterface effect) {
//        if (mSurfaceView != null) {
//            mSurfaceView.setEffect(effect);
//        } else {
//            Log.e(hhPlayerCore.ETAG, "仅支持GLSurfaceView");
//        }
//    }

    //设置添加view的交互
    public void setCvInterface(ControlViewInterface cvInterface) {
        this.cvInterface = cvInterface;
    }

    //设置请求头，在prepare之前执行
    public void setHeader(Map<String, String> mHeaders) {
        hhPlayerCore.setHeaders(mHeaders);
    }

    //全屏状态切换监听
    public void setScreenChangeListener(OnPlayerListener<Integer> screenChangeListener) {
        this.screenChangeListener = screenChangeListener;
    }

    /***********************************播放控制部分**************************************************************/
    //准备-地址，标题为空
    @Override
    public void prepare(Object path) {
        currentPlayIndex = -1;
        prepare(path, "");
    }

    //准备-地址，标题
    public void prepare(Object path, String title) {
        this.title = title;
        if (cvInterface != null) {
            cvInterface.getPrePareState(hhPlayerCore.playerPrePareState);
        }
        super.prepare(path);
    }

    //准备-列表，第几个
    public void prepare(List<TvDataList> tvLists, int position) {
        if (tvLists != null && tvLists.size() > 0 && tvLists.size() > position) {
            this.tvLists = tvLists;
            currentPlayIndex = position;
            if (tvLists.get(position) != null && tvLists.get(position).getChannelURLs().size() != 0) {
                prepare(tvLists.get(position).getChannelURLs().get(0).getChannelURL(), tvLists.get(position).getChannelName());
            } else {
                coreErrorListener.onListen("播放频道为空数据");
            }
        } else {
            currentPlayIndex = -1;
            coreErrorListener.onListen("播放列表为空，或不存在");
        }
    }

    //刷新播放列表，刷新后自动播放当前列表第一个
    public void updatePlayList(List<TvDataList> tvLists) {
        if (cvInterface != null) {
            cvInterface.refreshPlayList(tvLists);
        }
        currentPlayIndex = 0;
        prepare(tvLists, currentPlayIndex);
    }


    public int getPlayIndex() {
        if (tvLists != null) {
            return currentPlayIndex;
        }
        return -1;
    }

    @Override
    public void pause() {
        super.pause();
        if (cvInterface != null) {
            cvInterface.getPlayState(hhPlayerCore.mPlayerState);
        }
    }

    @Override
    public void start() {
        super.start();
        if (cvInterface != null) {
            cvInterface.getPlayState(hhPlayerCore.mPlayerState);
        }
    }

    @Override
    public void release() {
        super.release();
        if (cvInterface != null) {
            cvInterface.getPrePareState(hhPlayerCore.playerPrePareState);
        }
    }

    /***********************************获取参数部分**************************************************************/
    //获取系统屏幕亮度
    private int getSystemScreenBrightness() {
        try {
            int brightNess = Settings.System.getInt(mActivity.getContentResolver(), "screen_brightness");
            return (int) ((float) (brightNess * 100) / 255.0F);
        } catch (Settings.SettingNotFoundException var4) {
            return -1;
        }
    }

    public ViewGroup getPlayerView() {
        return viewGroup;
    }

    //获取屏幕亮度
    public int getScreenBrightness() {
        if (mActivity != null) {
            Window localWindow = mActivity.getWindow();
            WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
            float screenBrightness = localLayoutParams.screenBrightness;
            if (screenBrightness > 1.0F || screenBrightness < 0.1) {
                return getSystemScreenBrightness();
            }
            return (int) (screenBrightness * 100.0F);
        }
        return getSystemScreenBrightness();
    }

    public FragmentActivity getActivity() {
        return mActivity;
    }

    public String getTitle() {
        return title;
    }

    public int getScreenScale() {
        return screenScale;
    }

    public int getScreenType() {
        return screenType;
    }

    //获取播放列表
    public List<TvDataList> getUrlDataList() {
        return tvLists;
    }

    /***********************************初始化view部分**************************************************************/
    public void initSurface(Surface surface) {
        if (surface != null && surface.isValid()) {
            hhPlayerCore.initView(surface);
        } else {
            Log.e(hhPlayerCore.ETAG, "surface无效");
        }
    }

    public void initSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null) {
            hhPlayerCore.initView(surfaceHolder);
        } else {
            Log.e(hhPlayerCore.ETAG, "surfaceHolder为空");
        }
    }

    private void initSurfaceView() {
        if (checkVideoAdd()) {
            Log.e(hhPlayerCore.ETAG, "视频View已存在");
            return;
        }
        SurfaceView mSurfaceView = new SurfaceView(mActivity.getApplicationContext());
        mSurfaceView.setId(R.id.video_normal_id);
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                hhPlayerCore.initView(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        V_view.addToParentCenter(viewGroup, mSurfaceView);
    }

    private void initTextureView() {
        if (checkVideoAdd()) {
            Log.e(hhPlayerCore.ETAG, "视频View已存在");
            return;
        }
        TextureView mTextureView = new TextureView(mActivity.getApplicationContext());
        mTextureView.setId(R.id.video_normal_id);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                hhPlayerCore.initView(new Surface(surfaceTexture));
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (surface != null) {
                    surface.release();
                }
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        V_view.addToParentCenter(viewGroup, mTextureView);
    }

//    private void initHHGLSurfaceView() {
//        if (checkVideoAdd()) {
//            Log.e(hhPlayerCore.ETAG, "视频View已存在");
//            return;
//        }
//        mSurfaceView = new HHGLSurfaceView(mActivity.getApplicationContext());
//        mSurfaceView.setId(R.id.video_normal_id);
//        mSurfaceView.setSurfaceCreateListener(new OnPlayerListener<Surface>() {
//            @Override
//            public void onListen(Surface surface) {
//                hhPlayerCore.initView(surface);
//            }
//        });
//        U_view.addToParentCenter(viewGroup, mSurfaceView);
//    }

    public boolean sendKeyDown(int keyCode) {
        if (screenType == PlayerViewState.SCREEN_FULL && keyCode == KeyEvent.KEYCODE_BACK) {
            exitFullScreen();
            return true;
        }
        if (cvInterface != null) {
            return cvInterface.receiveKeyDown(keyCode);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mActivity.onBackPressed();
            return false;
        }
        return false;
    }
}
