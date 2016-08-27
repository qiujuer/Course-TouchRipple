package net.qiujuer.sample.touchripple.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by JuQiu on 16/8/18.
 */

public class RippleDrawable extends Drawable {
    // 最大的透明度
    private static final int MAX_ALPHA_BG = 172;
    private static final int MAX_ALPHA_IRCLE = 255;

    // Drawable 0~255 透明度
    private int mAlpha = 255;
    private int mRippleColor = 0;
    // 画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 圆心坐标
    private float mRipplePointX, mRipplePointY;
    // 半径
    private float mRippleRadius = 0;

    // 背景透明度, 圆形透明度
    private int mBgAlpha = 0, mCircleAlpha = MAX_ALPHA_IRCLE;

    // 标示用户手是否抬起
    private boolean mTouchRelease;

    // 按下时的点击点
    private float mDonePointX, mDonePointY;
    // 控件的中心区域点
    private float mCenterPointX, mCenterPointY;
    // 开始和结束的半径
    private float mStartRadius, mEndRadius;

    public RippleDrawable(int color) {
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 防抖动
        mPaint.setDither(true);
        // 设置画笔为填充方式
        mPaint.setStyle(Paint.Style.FILL);
        // 设置涟漪颜色
        setRippleColor(color);
    }

    public void onTouch(MotionEvent event) {
        // 判断点击操作类型
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event.getX(), event.getY());
                break;
        }
    }

    private void onTouchDown(float x, float y) {
        mDonePointX = x;
        mDonePointY = y;
        // 按下,没有抬起
        mTouchRelease = false;
        startEnterRunnable();
    }

    private void onTouchMove(float x, float y) {
        //TODO
    }

    private void onTouchUp(float x, float y) {
        // 标示手抬起
        mTouchRelease = true;
        // 当,进入动画完成时,启动退出动画
        if (mEnterDone) {
            startExitRunnable();
        }
    }

    private void onTouchCancel(float x, float y) {
        // 标示手抬起
        mTouchRelease = true;
        // 当,进入动画完成时,启动退出动画
        if (mEnterDone) {
            startExitRunnable();
        }
    }

    /**
     * 设置涟漪的颜色
     * @param color Color
     */
    public void setRippleColor(int color) {
        mRippleColor = color;
        onColorOrAlphaChange();
    }

    /**
     * 启动进入动画
     */
    private void startEnterRunnable() {
        mCircleAlpha = MAX_ALPHA_IRCLE;

        mEnterProgress = 0;
        mEnterDone = false;
        // 取消事物的操作
        unscheduleSelf(mExitRunnable);
        unscheduleSelf(mEnterRunnable);
        // 注入一个进入动画
        scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    }

    /**
     * 启动退出动画
     */
    private void startExitRunnable() {
        mExitProgress = 0;
        // 取消事物的操作
        unscheduleSelf(mEnterRunnable);
        unscheduleSelf(mExitRunnable);
        // 注入一个退出动画
        scheduleSelf(mExitRunnable, SystemClock.uptimeMillis());
    }

    // 标示进入动画是否完成
    private boolean mEnterDone;
    // 进入动画进度值
    private float mEnterProgress = 0;
    // 每次递增的进度值
    private float mEnterIncrement = 16f / 360;
    // 进入动画查值器,用于实现从快到慢的效果
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(2);
    // 动画的回调
    private Runnable mEnterRunnable = new Runnable() {
        @Override
        public void run() {
            mEnterProgress = mEnterProgress + mEnterIncrement;

            if (mEnterProgress > 1) {
                onEnterProgress(1);
                onEnterDone();
                return;
            }

            float realProgress = mEnterInterpolator.getInterpolation(mEnterProgress);
            onEnterProgress(realProgress);

            // 延迟16毫秒,保证界面刷新频率接近60FPS
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };

    /**
     * 进入动画完成时触发
     */
    private void onEnterDone() {
        Log.e("TAG", "onEnterDone");
        mEnterDone = true;
        // 当用户手放开时,启动退出动画
        if (mTouchRelease) {
            startExitRunnable();
        }
    }

    /**
     * 进入动画刷新方法
     *
     * @param progress 进度值
     */
    private void onEnterProgress(float progress) {
        mRippleRadius = getProgressValue(mStartRadius, mEndRadius, progress);
        mRipplePointX = getProgressValue(mDonePointX, mCenterPointX, progress);
        mRipplePointY = getProgressValue(mDonePointY, mCenterPointY, progress);

        mBgAlpha = (int) getProgressValue(0, MAX_ALPHA_BG, progress);

        invalidateSelf();
    }

    // 退出动画进度值
    private float mExitProgress = 0;
    // 每次递增的进度值
    private float mExitIncrement = 16f / 300;
    // 退出动画查值器,用于实现从慢到快的效果
    private Interpolator mExitInterpolator = new AccelerateInterpolator(2);
    // 动画的回调
    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            // 进入时,首先判断进入动画是否具有
            if (!mEnterDone)
                return;

            mExitProgress = mExitProgress + mExitIncrement;

            if (mExitProgress > 1) {
                onExitProgress(1);
                onExitDone();
                return;
            }

            float realProgress = mExitInterpolator.getInterpolation(mExitProgress);
            onExitProgress(realProgress);

            // 延迟16毫秒,保证界面刷新频率接近60FPS
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };

    /**
     * 当退出动画完成时触发
     */
    private void onExitDone() {
        Log.e("TAG", "onExitDone");
        // TODO
    }

    /**
     * 退出动画刷新方法
     *
     * @param progress 进度值
     */
    private void onExitProgress(float progress) {
        Log.e("TAG", "onExitProgress:" + progress);
        // 背景减淡
        mBgAlpha = (int) getProgressValue(MAX_ALPHA_BG, 0, progress);
        // 圆形减淡
        mCircleAlpha = (int) getProgressValue(MAX_ALPHA_IRCLE, 0, progress);

        invalidateSelf();
    }

    private float getProgressValue(float start, float end, float progress) {
        return start + (end - start) * progress;
    }


    /**
     * 界面大小改变时触发,我们在这里运算中心点,以及半径
     * @param bounds Rect
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mCenterPointX = bounds.centerX();
        mCenterPointY = bounds.centerY();

        // 得到圆的最大半径
        float maxRadius = Math.max(mCenterPointX, mCenterPointY);
        mStartRadius = maxRadius * 0f;
        mEndRadius = maxRadius * 0.8f;
    }

    /**
     * 通过两块玻璃叠加后颜色更深,
     * 光线透过更少的算法反向推出其中一块玻璃块的值的算法
     * @param preAlpha Z值,结合后的值
     * @param bgAlpha Y值, 其中一块玻璃块的值
     * @return 返回另外一块玻璃块的值
     */
    private int getCircleAlpha(int preAlpha, int bgAlpha) {
        int dAlpha = preAlpha - bgAlpha;
        return (int) ((dAlpha * 255f) / (255f - bgAlpha));
    }

    @Override
    public void draw(Canvas canvas) {
        int preAlpha = mPaint.getAlpha();
        int bgAlpha = (int) (preAlpha * (mBgAlpha / 255f));
        int maxCircleAlpha = getCircleAlpha(preAlpha, bgAlpha);
        int circleAlpha = (int) (maxCircleAlpha * (mCircleAlpha / 255f));

        // 绘制背景区域颜色
        mPaint.setAlpha(bgAlpha);
        canvas.drawColor(mPaint.getColor());

        // 画上一个圆
        mPaint.setAlpha(circleAlpha);
        canvas.drawCircle(mRipplePointX, mRipplePointY,
                mRippleRadius,
                mPaint);

        mPaint.setAlpha(preAlpha);
    }

    @Override
    public void setAlpha(int alpha) {
        // 设置Drawable的透明度
        mAlpha = alpha;
        onColorOrAlphaChange();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    private void onColorOrAlphaChange() {
        // 0x30ffffff
        mPaint.setColor(mRippleColor);
        Log.e("TAG", "set:" + mPaint.getColor());
        if (mAlpha != 255) {
            // 得到颜色本身的透明度
            // 0x30
            int pAlpha = mPaint.getAlpha();
            int realAlpha = (int) (pAlpha * (mAlpha / 255f));
            mPaint.setAlpha(realAlpha);
            Log.e("TAG", "Old:" + mRippleColor + " new:" + mPaint.getColor());
        }

        // 刷新当前Drawable
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // 颜色滤镜
        if (mPaint.getColorFilter() != colorFilter) {
            mPaint.setColorFilter(colorFilter);
            // 刷新当前Drawable
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        int alpha = mPaint.getAlpha();
        if (alpha == 255) {
            // 不透明的drawable
            return PixelFormat.OPAQUE;
        } else if (alpha == 0) {
            // 全透明的drawable
            return PixelFormat.TRANSPARENT;
        } else {
            // 半透明的drawable
            return PixelFormat.TRANSLUCENT;
        }
    }
}
