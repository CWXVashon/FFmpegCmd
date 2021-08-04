package com.example.ffmpegcmd.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.example.ffmpegcmd.R;

import x.com.log.ViseLog;

/**
 * 用于选择区域，如裁剪图片时选择区域或裁剪视频时选择区域
 * mScaleWidthOfHeight = -1 或 mIsFocusAuto 为 true时，自由裁剪，否则按比例裁剪
 * getResult[左上右下]，或者 getResultLeft,getResultTop,getResultRight,getResultBottom方式获取选择结果
 */
public class ChooseAreaView extends View {

    private int mConnerColor = 0xffffffff;
    private int mConnerWidth = 6;
    private int mConnerLength = 80;
    private float mScaleWidthOfHeight = -1; //默认自由裁剪好了
    private boolean mIsFocusAuto;
    private Paint mPaint;
    private float cropLeft, cropTop, cropRight, cropBottom;
    private static final int mColorBlackTrans = 0x80000000;
    private int mLimitLeft, mLimitRight, mLimitTop, mLimitBottom;
    private boolean isLimitSizeOutSide;
    private int xAreaNum, yAreaNum;//x,y 方向划分成几块，如2*2，总共4块，x = 2,y = 2
    private boolean isShowLog = true;
    private float defaultChooseScale = -1;
    private int layoutWidth;
    private int layoutHeight;
    private float defaultLeftF, defaultRightF, defaultTopF, defaultBottomF;

    public ChooseAreaView(Context context) {
        super(context);
        init(context, null);
    }

    public ChooseAreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChooseAreaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChooseAreaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    private void init(Context context, AttributeSet attrs) {
        mConnerColor = context.getResources().getColor(R.color.skyBlue);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChooseAreaView);
            mConnerColor = a.getColor(R.styleable.ChooseAreaView_connerColorCa, mConnerColor);
            mConnerWidth = a.getDimensionPixelOffset(R.styleable.ChooseAreaView_connerWidthCa, 6);
            mConnerLength = a.getDimensionPixelOffset(R.styleable.ChooseAreaView_connerLengthCa, 40);
            mScaleWidthOfHeight = a.getFloat(R.styleable.ChooseAreaView_scaleWidthOfHeightCa, mScaleWidthOfHeight);
            mIsFocusAuto = a.getBoolean(R.styleable.ChooseAreaView_isFocusAutoCa, mIsFocusAuto);
            a.recycle();
        }

        mIsFocusAuto = mScaleWidthOfHeight == -1 || mIsFocusAuto;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        setClickable(true);
    }

    public void setLayoutSize(int layoutWidth, int layoutHeight) {
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = layoutWidth;
        lp.height = layoutHeight;
        setLayoutParams(lp);

        initArea();
    }

    /**
     * x,y 方向划分成几块，如2*2，总共4块，x = 2,y = 2
     * 分割线
     */
    public void setXYAreaNum(int xAreaNum, int yAreaNum) {
        this.xAreaNum = xAreaNum;
        this.yAreaNum = yAreaNum;
        invalidate();
    }

    public void setDefaultChooseScale(float defaultChooseScale) {
        this.defaultChooseScale = defaultChooseScale;
        initArea();
    }

    public void setScaleWidthOfHeight(float mScaleWidthOfHeight) {
        this.mScaleWidthOfHeight = mScaleWidthOfHeight;
        mIsFocusAuto = mScaleWidthOfHeight <= 0 || mIsFocusAuto;
        initArea();
    }

    public void setFocusAuto(boolean mIsFocusAuto) {
        this.mIsFocusAuto = mIsFocusAuto;
        initArea();
    }

    public void setFocusAutoAndDefaultChooseScale(boolean mIsFocusAuto, float defaultChooseScale) {
        this.mIsFocusAuto = mIsFocusAuto;
        this.defaultChooseScale = defaultChooseScale;
        initArea();
    }

    /**
     * 设置默认选择区域，参数0-1
     */
    public void setDefaultSelectFloatArea(float defaultLeftF, float defaultTopF, float defaultRightF, float defaultBottomF) {
        this.defaultLeftF = defaultLeftF;
        this.defaultTopF = defaultTopF;
        this.defaultRightF = defaultRightF;
        this.defaultBottomF = defaultBottomF;
        if (this.defaultLeftF < 0) {
            this.defaultLeftF = 0;
        }
        if (this.defaultLeftF > 1) {
            this.defaultLeftF = 1;
        }
        if (this.defaultTopF < 0) {
            this.defaultTopF = 0;
        }
        if (this.defaultTopF > 1) {
            this.defaultTopF = 1;
        }
        if (this.defaultRightF < 0) {
            this.defaultRightF = 0;
        }
        if (this.defaultRightF > 1) {
            this.defaultRightF = 1;
        }
        if (this.defaultBottomF < 0) {
            this.defaultBottomF = 0;
        }
        if (this.defaultBottomF > 1) {
            this.defaultBottomF = 1;
        }
        initArea();
    }

    public float[] getResult() {
        return new float[]{cropLeft, cropTop, cropRight, cropBottom};
    }

    public float[] getResultF() {
        float left, right, top, bottom;
        int width = getWidth();
        if (width != 0) {
            left = cropLeft / width;
            right = cropRight / width;
        } else {
            left = right = 0;
        }
        int height = getHeight();
        if (height != 0) {
            top = cropTop / height;
            bottom = cropBottom / height;
        } else {
            top = bottom = 0;
        }
        return new float[]{left, top, right, bottom};
    }

    /**
     * 是否选择了全部，即，无需裁剪等其他操作
     */
    public boolean isChooseAll() {
        return mLimitRight > mLimitLeft && mLimitBottom > mLimitTop && cropLeft == mLimitLeft && cropTop == mLimitTop && cropRight == mLimitRight && cropBottom == mLimitBottom;
    }

    public float getResultLeft() {
        return cropLeft;
    }

    public float getResultTop() {
        return cropTop;
    }

    public float getResultRight() {
        return cropRight;
    }

    public float getResultBottom() {
        return cropBottom;
    }

    public int getLayoutWidth() {
        return layoutWidth == 0 ? getWidth() : layoutWidth;
    }

    public int getLayoutHeight() {
        return layoutHeight == 0 ? getHeight() : layoutHeight;
    }

    private void initArea() {
        int width = getLayoutWidth();
        int height = getLayoutHeight();
        if (height == 0 || width == 0) {
            return;
        }
        if (!isLimitSizeOutSide) {
            mLimitLeft = 0;
            mLimitRight = width;
            mLimitTop = 0;
            mLimitBottom = height;
        }

        if (defaultLeftF != 0 || defaultTopF != 0 || defaultRightF != 0 || defaultBottomF != 0) {
            cropLeft = defaultLeftF * width;
            cropRight = defaultRightF * width;
            cropTop = defaultTopF * height;
            cropBottom = defaultBottomF * height;
        } else {
            int canChooseWidth = mLimitRight - mLimitLeft;
            int canChooseHeight = mLimitBottom - mLimitTop;

            if ((mIsFocusAuto && defaultChooseScale <= 0) || mScaleWidthOfHeight <= 0) {
                //默认全选
                cropLeft = mLimitLeft;
                cropTop = mLimitTop;
                cropRight = mLimitRight;
                cropBottom = mLimitBottom;
            } else {
                defaultChooseScale = mScaleWidthOfHeight;
                float chooseHeightIfWidthMath = canChooseWidth / defaultChooseScale;
                if (chooseHeightIfWidthMath > canChooseHeight) {
                    //高度撑满
                    cropTop = height / 2f - canChooseHeight / 2f;
                    cropBottom = height / 2f + canChooseHeight / 2f;
                    cropLeft = width / 2f - canChooseHeight * defaultChooseScale / 2;
                    cropRight = width / 2f + canChooseHeight * defaultChooseScale / 2;
                } else {
                    //宽度撑满
                    cropLeft = width / 2f - canChooseWidth / 2f;
                    cropRight = width / 2f + canChooseWidth / 2f;
                    cropTop = height / 2f - chooseHeightIfWidthMath / 2;
                    cropBottom = height / 2f + chooseHeightIfWidthMath / 2;
                }
            }
        }
        invalidate();
    }

    /**
     * 设置可选区域，如没有设置，则就是控件本身大小
     */
    public void setLimitSize(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        isLimitSizeOutSide = true;
        mLimitLeft = limitLeft;
        mLimitTop = limitTop;
        mLimitRight = limitRight;
        mLimitBottom = limitBottom;
        initArea();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initArea();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getLayoutWidth();
        int height = getLayoutHeight();
        if (width == 0 || height == 0) return;
        if (cropLeft == 0 && cropTop == 0 && cropRight == 0 && cropBottom == 0) {
            initArea();
        }

        //绘制半透明遮挡
        mPaint.setColor(mColorBlackTrans);
        canvas.drawRect(0, 0, width, cropTop, mPaint);
        canvas.drawRect(0, cropTop, cropLeft, cropBottom, mPaint);
        canvas.drawRect(0, cropBottom, width, height, mPaint);
        canvas.drawRect(cropRight, cropTop, width, cropBottom, mPaint);

        //绘制四个角
        mPaint.setColor(mConnerColor);
        //左上角
        canvas.drawRect(cropLeft, cropTop, cropLeft + mConnerWidth, Math.min(cropTop + mConnerLength, cropBottom), mPaint);
        canvas.drawRect(cropLeft + mConnerWidth, cropTop, Math.min(cropLeft + mConnerLength, cropRight), cropTop + mConnerWidth, mPaint);
        //右上角
        canvas.drawRect(cropRight - mConnerWidth, cropTop, cropRight, Math.min(cropTop + mConnerLength, cropBottom), mPaint);
        canvas.drawRect(Math.max(cropRight - mConnerLength, cropLeft), cropTop, cropRight - mConnerWidth, cropTop + mConnerWidth, mPaint);
        //左下角
        canvas.drawRect(cropLeft, Math.max(cropBottom - mConnerLength, cropTop), cropLeft + mConnerWidth, cropBottom, mPaint);
        canvas.drawRect(cropLeft + mConnerWidth, cropBottom - mConnerWidth, Math.min(cropLeft + mConnerLength, cropRight), cropBottom, mPaint);
        //右下角
        canvas.drawRect(cropRight - mConnerWidth, Math.max(cropBottom - mConnerLength, cropTop), cropRight, cropBottom, mPaint);
        canvas.drawRect(Math.max(cropRight - mConnerLength, cropLeft), cropBottom - mConnerWidth, cropRight - mConnerWidth, cropBottom, mPaint);
//        //左上角
//        canvas.drawRect(cropLeft, cropTop, cropLeft + mConnerWidth, cropTop + mConnerLength, mPaint);
//        canvas.drawRect(cropLeft + mConnerWidth, cropTop, cropLeft + mConnerLength, cropTop + mConnerWidth, mPaint);
//        //右上角
//        canvas.drawRect(cropRight - mConnerWidth, cropTop, cropRight, cropTop + mConnerLength, mPaint);
//        canvas.drawRect(cropRight - mConnerLength, cropTop, cropRight - mConnerWidth, cropTop + mConnerWidth, mPaint);
//        //左下角
//        canvas.drawRect(cropLeft, cropBottom - mConnerLength, cropLeft + mConnerWidth, cropBottom, mPaint);
//        canvas.drawRect(cropLeft + mConnerWidth, cropBottom - mConnerWidth, cropLeft + mConnerLength, cropBottom, mPaint);
//        //右下角
//        canvas.drawRect(cropRight - mConnerWidth, cropBottom - mConnerLength, cropRight, cropBottom, mPaint);
//        canvas.drawRect(cropRight - mConnerLength, cropBottom - mConnerWidth, cropRight - mConnerWidth, cropBottom, mPaint);

        //绘制选择区域内的线条
        mPaint.setColor(mConnerColor);
        if (xAreaNum > 1) {
            float chooseWidth = cropRight - cropLeft;
            float singleWidth = chooseWidth / xAreaNum;
            for (int i = 1; i < xAreaNum; i++) {
                float x = cropLeft + singleWidth * i;
                canvas.drawRect(x, cropTop, x + 2, cropBottom, mPaint);//线条宽度 2
            }
        }
        if (yAreaNum > 1) {
            float chooseHeight = cropBottom - cropTop;
            float singleHeight = chooseHeight / yAreaNum;
            for (int i = 1; i < yAreaNum; i++) {
                float y = cropTop + singleHeight * i;
                canvas.drawRect(cropLeft, y, cropRight, y + 2, mPaint);//线条宽度 2
            }
        }
        mPaint.setColor(mColorBlackTrans);
        if (xAreaNum > 1) {
            float chooseWidth = cropRight - cropLeft;
            float singleWidth = chooseWidth / xAreaNum;
            for (int i = 1; i < xAreaNum; i++) {
                float x = cropLeft + singleWidth * i;
                canvas.drawRect(x, cropTop, x + 1, cropBottom, mPaint);//线条宽度 2
            }
        }
        if (yAreaNum > 1) {
            float chooseHeight = cropBottom - cropTop;
            float singleHeight = chooseHeight / yAreaNum;
            for (int i = 1; i < yAreaNum; i++) {
                float y = cropTop + singleHeight * i;
                canvas.drawRect(cropLeft, y, cropRight, y + 1, mPaint);//线条宽度 2
            }
        }
    }

    /**
     * @return -1：未判断，0：不响应，1：左上角，2：右上角，3：左下角，4：右下角，5：中间
     */
    private int getTouchAction(float downX, float downY, float touchX, float touchY) {
        boolean isDownInLeftTop = isDownInLeftTop(downX, downY);
        boolean isDownInRightTop = isDownInRightTop(downX, downY);
        boolean isDownInLeftBottom = isDownInLeftBottom(downX, downY);
        boolean isDownInRightBottom = isDownInRightBottom(downX, downY);
        if (isShowLog)
            ViseLog.showLog("getTouchAction " + isDownInLeftTop + ", " + isDownInRightTop + ", " + isDownInLeftBottom + ", " + isDownInRightBottom);
        if (isShowLog)
            ViseLog.showLog("getTouchAction " + downX + ", " + downY + ", " + touchX + ", " + touchY);
        if (isDownInLeftTop && isDownInRightTop && isDownInLeftBottom && isDownInRightBottom) {
            if (touchX < downX) {
                if (touchY < downY) {
                    return 1;
                } else {
                    return 3;
                }
            } else {
                if (touchY < downY) {
                    return 2;
                } else {
                    return 4;
                }
            }
        } else if (isDownInLeftTop && isDownInLeftBottom) {
            if (touchY < downY) {
                return 1;
            } else {
                return 3;
            }
        } else if (isDownInRightTop && isDownInRightBottom) {
            if (touchY < downY) {
                return 2;
            } else {
                return 4;
            }
        } else if (isDownInLeftTop && isDownInRightTop) {
            if (touchX < downX) {
                return 1;
            } else {
                return 2;
            }
        } else if (isDownInLeftBottom && isDownInRightBottom) {
            if (touchX < downX) {
                return 3;
            } else {
                return 4;
            }
        } else if (isDownInLeftTop) {
            return 1;
        } else if (isDownInRightTop) {
            return 2;
        } else if (isDownInLeftBottom) {
            return 3;
        } else if (isDownInRightBottom) {
            return 4;
        }
        return 5;
    }

    private boolean isDownInLeftTop(float downX, float downY) {
        return downX >= cropLeft - mConnerLength && downX <= cropLeft + mConnerLength && downY >= cropTop - mConnerLength && downY <= cropTop + mConnerLength;
    }

    private boolean isDownInRightTop(float downX, float downY) {
        return downX >= cropRight - mConnerLength && downX <= cropRight + mConnerLength && downY >= cropTop - mConnerLength && downY <= cropTop + mConnerLength;
    }

    private boolean isDownInLeftBottom(float downX, float downY) {
        return downX >= cropLeft - mConnerLength && downX <= cropLeft + mConnerLength && downY >= cropBottom - mConnerLength && downY <= cropBottom + mConnerLength;
    }

    private boolean isDownInRightBottom(float downX, float downY) {
        return downX >= cropRight - mConnerLength && downX <= cropRight + mConnerLength && downY >= cropBottom - mConnerLength && downY <= cropBottom + mConnerLength;
    }

    private boolean isDownTouchEnable(float downX, float downY) {
        return (downX >= cropLeft && downX <= cropRight && downY >= cropTop && downY <= cropBottom)
                || isDownInLeftTop(downX, downY) || isDownInLeftBottom(downX, downY) || isDownInRightTop(downX, downY) || isDownInRightBottom(downX, downY);
    }

    private float downX, downY;
    private boolean isTouchMoveEnable;
    private int touchAction = -1;//-1：未判断，0：不响应，1：左上角，2：右上角，3：左下角，4：右下角，5：中间

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //仅限单指操作
        int pointerCount = event.getPointerCount();
        if (pointerCount != 1) {
            resetValue();
            return super.onTouchEvent(event);
        }
        //屏幕上的坐标系
//        float touchX = event.getRawX();
//        float touchY = event.getRawY();
        //控件上的坐标系
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = touchX;
                downY = touchY;
                if (isShowLog) ViseLog.showLog("ACTION_DOWN downX = " + downX);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN downY = " + downY);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN cropLeft = " + cropLeft);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN cropRight = " + cropRight);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN cropTop = " + cropTop);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN cropBottom = " + cropBottom);
                if (isShowLog) ViseLog.showLog("ACTION_DOWN mConnerLength = " + mConnerLength);
                isTouchMoveEnable = isDownTouchEnable(downX, downY);
                touchAction = isTouchMoveEnable ? -1 : 0;
                if (isShowLog)
                    ViseLog.showLog("ACTION_DOWN isTouchMoveEnable = " + isTouchMoveEnable + ",touchAction = " + touchAction);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchMoveEnable) {
                    if (touchAction == -1) {
                        touchAction = getTouchAction(downX, downY, touchX, touchY);
                        if (isShowLog) ViseLog.showLog("ACTION_MOVE touchAction = " + touchAction);
                    }
                    doMove(touchAction, touchX - downX, touchY - downY);
                    downX = touchX;
                    downY = touchY;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isShowLog) ViseLog.showLog("ACTION_UP or ACTION_CANCEL");
                resetValue();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void doMove(int touchAction, float moveX, float moveY) {
        if (touchAction == 5) {
            if (cropLeft + moveX < mLimitLeft) {
                moveX = mLimitLeft - cropLeft;
            } else if (cropRight + moveX > mLimitRight) {
                moveX = mLimitRight - cropRight;
            }
            cropLeft += moveX;
            cropRight += moveX;

            if (cropTop + moveY < mLimitTop) {
                moveY = mLimitTop - cropTop;
            } else if (cropBottom + moveY > mLimitBottom) {
                moveY = mLimitBottom - cropBottom;
            }
            cropTop += moveY;
            cropBottom += moveY;
        } else if (mIsFocusAuto) {
            //不限比例自由裁剪
            if (touchAction == 1) {
                //左上点
                cropLeft += moveX;
                if (cropLeft < mLimitLeft) {
                    cropLeft = mLimitLeft;
                }
                if (cropLeft > cropRight - mConnerLength) {
                    cropLeft = cropRight - mConnerLength;
                }
                cropTop += moveY;
                if (cropTop < mLimitTop) {
                    cropTop = mLimitTop;
                }
                if (cropTop > cropBottom - mConnerLength) {
                    cropTop = cropBottom - mConnerLength;
                }
            } else if (touchAction == 3) {
                //左下点
                cropLeft += moveX;
                if (cropLeft < mLimitLeft) {
                    cropLeft = mLimitLeft;
                }
                if (cropLeft > cropRight - mConnerLength) {
                    cropLeft = cropRight - mConnerLength;
                }
                cropBottom += moveY;
                if (cropBottom > mLimitBottom) {
                    cropBottom = mLimitBottom;
                }
                if (cropBottom < cropTop + mConnerLength) {
                    cropBottom = cropTop + mConnerLength;
                }
            } else if (touchAction == 2) {
                //右上点
                cropRight += moveX;
                if (cropRight > mLimitRight) {
                    cropRight = mLimitRight;
                }
                if (cropRight < cropLeft + mConnerLength) {
                    cropRight = cropLeft + mConnerLength;
                }
                cropTop += moveY;
                if (cropTop < mLimitTop) {
                    cropTop = mLimitTop;
                }
                if (cropTop > cropBottom - mConnerLength) {
                    cropTop = cropBottom - mConnerLength;
                }
            } else if (touchAction == 4) {
                //右下点
                cropRight += moveX;
                if (cropRight > mLimitRight) {
                    cropRight = mLimitRight;
                }
                if (cropRight < cropLeft + mConnerLength) {
                    cropRight = cropLeft + mConnerLength;
                }
                cropBottom += moveY;
                if (cropBottom > mLimitBottom) {
                    cropBottom = mLimitBottom;
                }
                if (cropBottom < cropTop + mConnerLength) {
                    cropBottom = cropTop + mConnerLength;
                }
            }
        } else {
            //以X为标准
            if (touchAction == 1) {
                //左上点
                cropLeft += moveX;
                if (cropLeft < mLimitLeft) {
                    cropLeft = mLimitLeft;
                }
                if (cropLeft > cropRight - mConnerLength) {
                    cropLeft = cropRight - mConnerLength;
                }
                cropTop = (int) (cropBottom - (cropRight - cropLeft) / mScaleWidthOfHeight);
                if (cropTop < mLimitTop) {
                    cropTop = mLimitTop;
                    cropLeft = (int) (cropRight - (cropBottom - cropTop) * mScaleWidthOfHeight);
                }
            } else if (touchAction == 3) {
                //左下点
                cropLeft += moveX;
                if (cropLeft < mLimitLeft) {
                    cropLeft = mLimitLeft;
                }
                if (cropLeft > cropRight - mConnerLength) {
                    cropLeft = cropRight - mConnerLength;
                }
                cropBottom = (int) (cropTop + (cropRight - cropLeft) / mScaleWidthOfHeight);
                if (cropBottom > mLimitBottom) {
                    cropBottom = mLimitBottom;
                    cropLeft = (int) (cropRight - (cropBottom - cropTop) * mScaleWidthOfHeight);
                }
            } else if (touchAction == 2) {
                //右上点
                cropRight += moveX;
                if (cropRight > mLimitRight) {
                    cropRight = mLimitRight;
                }
                if (cropRight < cropLeft + mConnerLength) {
                    cropRight = cropLeft + mConnerLength;
                }
                cropTop = (int) (cropBottom - (cropRight - cropLeft) / mScaleWidthOfHeight);
                if (cropTop < mLimitTop) {
                    cropTop = mLimitTop;
                    cropRight = (int) (cropLeft + (cropBottom - cropTop) * mScaleWidthOfHeight);
                }
            } else if (touchAction == 4) {
                //右下点
                cropRight += moveX;
                if (cropRight > mLimitRight) {
                    cropRight = mLimitRight;
                }
                if (cropRight < cropLeft + mConnerLength) {
                    cropRight = cropLeft + mConnerLength;
                }
                cropBottom = (int) (cropTop + (cropRight - cropLeft) / mScaleWidthOfHeight);
                if (cropBottom > mLimitBottom) {
                    cropBottom = mLimitBottom;
                    cropRight = (int) (cropLeft + (cropBottom - cropTop) * mScaleWidthOfHeight);
                }
            }
        }
        invalidate();
    }

    private void resetValue() {
        isTouchMoveEnable = false;
        touchAction = -1;
        downX = downY = 0;
    }
}
