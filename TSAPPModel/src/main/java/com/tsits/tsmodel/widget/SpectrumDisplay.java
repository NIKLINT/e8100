package com.tsits.tsmodel.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tsits.tsmodel.R;
import com.tsits.tsmodel.utils.DisplayUtil;

/**
 * Created by YY on 2019/6/7.
 */
public class SpectrumDisplay extends View {

    private int foregroundColor = Color.argb(0xFF, 0x45, 0x5A, 0x64);
    private int backgroundColor = Color.argb(0xFF, 0x25, 0x31, 0x37);
    @SuppressLint("Range")
    private int spectrumColor = Color.argb(0xFF, 0xC3F, 0xD2F, 0x3C);
    private int size = DisplayUtil.sp2px(getContext(), 6);
    private int lineSize = DisplayUtil.sp2px(getContext(), 2);
    private Paint mPaint;

    public SpectrumDisplay(Context context) {
        super(context);
        init(null, 0);
    }

    public SpectrumDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SpectrumDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SpectrumDisplay, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SpectrumDisplay_foregroundColor){
                foregroundColor = a.getColor(R.styleable.SpectrumDisplay_foregroundColor, foregroundColor);
            }else if(attr ==  R.styleable.SpectrumDisplay_backgroundColor){
                backgroundColor = a.getColor(R.styleable.SpectrumDisplay_backgroundColor, backgroundColor);
            }else{
                spectrumColor = a.getColor(R.styleable.SpectrumDisplay_spectrumColor, spectrumColor);
            }

//            Resource IDs这一类的变量在API14之后的库项目里是不能在switch case statement里面使用的

//            switch (attr) {
//                case R.styleable.SpectrumDisplay_foregroundColor:
//                    foregroundColor = a.getColor(R.styleable.SpectrumDisplay_foregroundColor, foregroundColor);
//                    break;
//                case R.styleable.SpectrumDisplay_backgroundColor:
//                    backgroundColor = a.getColor(R.styleable.SpectrumDisplay_backgroundColor, backgroundColor);
//                    break;
//                case R.styleable.SpectrumDisplay_spectrumColor:
//                    spectrumColor = a.getColor(R.styleable.SpectrumDisplay_spectrumColor, spectrumColor);
//                    break;
//            }
        }
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //垂直线数
        int vCount = getWidth() / (size);
        //水平线数
        int hCount = getHeight() / (size);

        //可显示宽
        int showWidth = vCount * (size);
        //可显示高
        int showHeight = hCount * (size);

        int left = (getWidth() - showWidth) / 2;
        int top = (getHeight() - showHeight) / 2;
        int right = left + showWidth;
        int bottom = top + showHeight;

        //绘背景
        mPaint.setColor(backgroundColor);
        canvas.drawRect(left, top, right, bottom, mPaint);

        //绘频谱
        //TODO:绘频谱

        //绘线条
        mPaint.setColor(foregroundColor);
        mPaint.setStrokeWidth(lineSize);
        //绘横线
        for (int i = 0; i < hCount + 1; i++) {
            canvas.drawLine(left, top + (size * i), right, top + (size * i), mPaint);
        }
        //绘竖线
        for (int i = 0; i < vCount + 1; i++) {
            canvas.drawLine(left + (size * i), top, left + (size * i), bottom, mPaint);
        }
    }
}
