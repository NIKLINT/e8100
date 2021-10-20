package com.tsits.tsmodel.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * create by z on 20/7/9
 *
 * @author z
 */
@SuppressLint("AppCompatCustomView")
public class ButtonIconCenter extends Button {
    private final static String TAG = "RightDrawableButton";
    private Drawable[] drawables;
    private float textWidth;
    private float bodyWidth;

    public ButtonIconCenter(Context context) {
        super(context);
        init();
    }

    public ButtonIconCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonIconCenter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawables = getCompoundDrawables();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textWidth = getPaint().measureText(getText().toString());
        int drawLWith = 0, drawRWith = 0;
        int drawablePadding = getCompoundDrawablePadding();
        if ("".equals(getText().toString())){
            drawablePadding=0;
        }
        Drawable drawableLeft = drawables[0];
        Drawable drawableRight = drawables[2];
        int totalWidth = getWidth();
        if (drawableLeft != null) {
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            drawLWith = drawableWidth + drawablePadding;
        }
        if (drawableRight != null) {
            int drawableWidth = drawableRight.getIntrinsicWidth();
            drawRWith = drawableWidth + drawablePadding;
        }
        bodyWidth=drawLWith+drawRWith+textWidth;
        setPadding(0, 0, (int) (totalWidth - bodyWidth), 0);
    }

    public void setText(String text) {
        if (text.equals(getText().toString())) {
            return;
        }
        super.setText(text);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        canvas.translate((width - bodyWidth) / 2, 0);
        super.onDraw(canvas);
    }
}
