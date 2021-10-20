/*
 * Copyright (c) 2016. made by Huangyaobin
 */

/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsits.tsmodel.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;

import java.util.Random;


/**
 * Created by minif on 2016/7/4.
 */
public class RoundScreenView extends androidx.appcompat.widget.AppCompatImageView {
    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = Color.WHITE;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    //无图像下的
    private int mColorGreen = Color.parseColor("#FF8BC34A");
    private int mColorYellow = Color.parseColor("#FFFFD54F");
    private int mColorOrange = Color.parseColor("#FFFF8A65");
    private int mColorBlue = Color.parseColor("#FF81D4FA");
    private int mColorDark = Color.parseColor("#11000000");

    private int mColorNumber = COLOR_RANDOM;  //传入一个数确定颜色，如果为-1则随机
    private int[] mBgColors = new int[]{mColorGreen, mColorYellow, mColorOrange, mColorBlue};
    private String mText = "测";
    private float mTextSize;
    private Paint mPaint;

    public final static int COLOR_RANDOM = -1;
    public final static int COLOR_DARK = -2;

    public RoundScreenView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextSize = sp2px(mContext, 20);
        mBorderThickness = dp2px(mContext,2);
//        mBorderOutsideColor = defaultColor;
        mBorderInsideColor = defaultColor;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        //this.measure(0, 0);

        if (defaultWidth == 0) {
            defaultWidth = getWidth();

        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        //获取半径和圆心位置，如果长宽不一致，取最短的边的一半
        int radius = 0, cx = defaultWidth / 2, cy = defaultHeight / 2;
        radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;

        //没有设置图片的时候，使用纯色加文字
        if (drawable == null) {
            switch (mColorNumber) {
                case COLOR_RANDOM:
                    mPaint.setColor(mBgColors[new Random().nextInt(mBgColors.length - 1)]);
                    break;
                case COLOR_DARK:
                    mPaint.setColor(mColorDark);
                    break;
                default:
                    //根据传入的数字分配固定的随机颜色
                    mPaint.setColor(mBgColors[mColorNumber % mBgColors.length]);
                    break;
            }
            canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(mTextSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            canvas.drawText(mText, cx, cy + fontHeight / 4, mPaint);
            drawCircleBorder(canvas,radius,defaultColor);
        } else {
            if (drawable.getClass() == NinePatchDrawable.class) return;
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
            canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
                    / 2 - radius, null);
        }

    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius 半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius-mBorderThickness+1, paint);
    }


    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        if (mText.length()>1){
            mText=mText.substring(0,1);
        }
        this.mText = mText;
    }

    public void setmColorNumber(int mColorNumber) {
        this.mColorNumber = mColorNumber;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private  int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    private  int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
