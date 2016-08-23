package net.qiujuer.sample.touchripple.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by JuQiu on 16/8/18.
 */

public class RippleDrawable extends Drawable {
    // Drawable 0~255 透明度
    private int mAlpha = 255;
    private int mRippleColor = 0;
    // 画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 圆心坐标
    private float mRipplePointX, mRipplePointY;
    // 半径
    private float mRippleRadius = 0;

    public RippleDrawable() {
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 防抖动
        mPaint.setDither(true);

        setRippleColor(Color.RED);

        // ARGB 0xFF FF FF FF
        // 设置滤镜
        // setColorFilter(new LightingColorFilter(0xFFFF0000,0x00330000));
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
        mRipplePointX = x;
        mRipplePointY = y;
        startEnterRunnable();
    }

    private void onTouchMove(float x, float y) {
    }

    private void onTouchUp(float x, float y) {
        //unscheduleSelf(mEnterRunnable);
    }

    private void onTouchCancel(float x, float y) {
    }

    public void setRippleColor(int color) {
        // 不建议直接设置
        // mPaint.setColor(color);
        mRippleColor = color;
        onColorOrAlphaChange();
    }

    private void startEnterRunnable() {
        mEnterProgress = 0;
        unscheduleSelf(mEnterRunnable);
        scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    }

    // 进入动画进度值
    private float mEnterProgress = 0;
    // 进入动画查值器,用于实现从快到慢的效果
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(2);
    // 动画的回调
    private Runnable mEnterRunnable = new Runnable() {
        @Override
        public void run() {
            mEnterProgress = mEnterProgress + 0.01f;

            if (mEnterProgress > 1) {
                return;
            }

            float realProgress = mEnterInterpolator.getInterpolation(mEnterProgress);

            mRippleRadius = 360 * realProgress;
            invalidateSelf();
            // 延迟16毫秒,保证界面刷新频率接近60FPS
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };

    @Override
    public void draw(Canvas canvas) {
        // 画上一个圆
        canvas.drawCircle(mRipplePointX, mRipplePointY,
                mRippleRadius,
                mPaint);
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
            //pAlpha = Color.alpha(mRippleColor);

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
