package net.qiujuer.sample.touchripple.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by JuQiu on 16/8/18.
 */
public class RippleButton extends Button {
    private RippleDrawable mRippleDrawable;

    public RippleButton(Context context) {
        this(context, null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrawable = new RippleDrawable();
        // 设置刷新接口, View 中已经实现
        mRippleDrawable.setCallback(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 设置Drawable绘制和刷新的区域
        mRippleDrawable.setBounds(0, 0, getWidth(), getHeight());
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        // 验证Drawable是否OK
        return who == mRippleDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制自己的Drawable
        mRippleDrawable.draw(canvas);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRippleDrawable.onTouch(event);
        super.onTouchEvent(event);
        return true;
    }
}
