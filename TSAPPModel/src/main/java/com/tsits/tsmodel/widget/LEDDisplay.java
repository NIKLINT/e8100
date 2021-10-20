package com.tsits.tsmodel.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.tsits.tsmodel.R;
import com.tsits.tsmodel.utils.DisplayUtil;

import java.util.Calendar;

/**
 * Created by YY on 2019/6/7.
 */
public class LEDDisplay extends View {
    private static final String TAG = LEDDisplay.class.getSimpleName();

    //信号强场值 单位DB
    private float rssiValue = -110.0f;
    //信号强度图标
    private Drawable rssiIcon = null;
    private Drawable rssiIcon0 = null;
    private Drawable rssiIcon1 = null;
    private Drawable rssiIcon2 = null;
    private Drawable rssiIcon3 = null;
    private Drawable rssiIcon4 = null;
    //信标显示参数
    private BeaconTypes beaconType = BeaconTypes.Lost;
    //信标显示
    private Drawable beacon_false = null;
    private Drawable beacon_true = null;
    private Drawable beacon_icon = null;
    /**
     * 信标执行线程
     */
    private Thread beaconShowThread = null;

    private long lastBeaconTime = 0;

    private int beaconShowTimeLength = 500;

    private boolean isEnable = false;

    //超级设备图标
    private Drawable superDeviceIcon;
    //信息图标
    private Drawable messageIcon = null;
    //位置图标
    private Drawable locationIcon;
    //SOS图标
    private Drawable sosIcon = null;

    //是否显示超级设备
    private boolean isSuperDevice = true;
    //是否有未读消息
    private boolean haveMessage = true;
    //是否显示定位
    private boolean locationEnable = true;
    //是否显示SOS
    private boolean haveSOSAlarm = true;
    //是否显示RSSI
    private boolean showRSSI = true;

    //RSSI值显示
    private TextPaint mRSSITextPaint;
    private float mRSSITextWidth;
    private float mRSSITextHeight;
    private String rssiString = "-110dm";

    //顶部文字
    private TextPaint mTopTextPaint;
    private float mTopTextWidth;
    private float mTopTextHeight;
    private String mTopString = "常规模式";

    //单行文字
    private TextPaint mMainTextPaint;
    private float mMainTextWidth;
    private float mMainTextHeight;
    private String mainMessage = "初始化";

    //第二行文字
    private TextPaint mSecondTextPaint;
    private float mSecondTextWidth;
    private float mSecondTextHeight;
    private String secondMessage = "正在初始化数据";
    //是否显示第二行文字
    private boolean secondLineTextShow = true;

    // 下方文字显示
    private TextPaint mBottomTextPaint;
    private float mBottomTextWidth;
    private float mBottomTextHeight;
    private String bottomMessage = "10:46s";
    private int sessionTime = -1;

    //矩形Paint
    private Paint mPaint;
    //圆角
    private RectF mRectF;

    //第一行文字
    private TextPaint mFirstLineTextPaint;
    private float mFirstLineTextWidth;
    private float mFirstLineTextHeight;
    private String mFirstTextString = "Rx频率";

    //第二行文字
    private TextPaint mSecondLineTextPaint;
    private float mSecondLineTextWidth;
    private float mSecondLineTextHeight;
    private String mSecondTextString = "Tx频率";

    //第三行显示
    private TextPaint mThreeLineTextPaint;
    private float mThreeLineTextWidth;
    private float mThreeLineTextHeight;
    private String mThreeTextString = "当前组";

    //第四行显示
    private TextPaint mFourLineTextPaint;
    private float mFourLineTextWidth;
    private float mFourLineTextHeight;
    private String mFourTextString = "设备号";

    public LEDDisplay(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public LEDDisplay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public LEDDisplay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        if (!isInEditMode()) {

            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.LEDDisplay, defStyleAttr, 0);
            Resources res = context.getResources();

            //指定图标默认值
            messageIcon = res.getDrawable(R.drawable.ic_message,null);
            messageIcon.setCallback(this);

            sosIcon = res.getDrawable(R.drawable.ic_add_alert_red_24dp, null);
            sosIcon.setCallback(this);

            locationIcon = res.getDrawable(R.drawable.ic_location, null);
            locationIcon.setCallback(this);

            superDeviceIcon = res.getDrawable(R.drawable.ic_super_device, null);
            superDeviceIcon.setCallback(this);

            rssiIcon0 = res.getDrawable(R.drawable.ic_rssi_0, null);
            rssiIcon1 = res.getDrawable(R.drawable.ic_rssi_1,null);
            rssiIcon2 = res.getDrawable(R.drawable.ic_rssi_2, null);
            rssiIcon3 = res.getDrawable(R.drawable.ic_rssi_3, null);
            rssiIcon4 = res.getDrawable(R.drawable.ic_rssi_4, null);

            rssiIcon = rssiIcon0;

            beacon_false = res.getDrawable(R.drawable.ic_beacon_false, null);
            beacon_true = res.getDrawable(R.drawable.ic_beacon_true, null);

            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.LEDDisplay_locationIcon){
                    if (a.hasValue(R.styleable.LEDDisplay_locationIcon)) {
                        locationIcon = a.getDrawable(R.styleable.LEDDisplay_locationIcon);
                        locationIcon.setCallback(this);
                    }
                }else if(attr == R.styleable.LEDDisplay_messageIcon){
                    if (a.hasValue(R.styleable.LEDDisplay_messageIcon)) {
                        messageIcon = a.getDrawable(R.styleable.LEDDisplay_messageIcon);
                        messageIcon.setCallback(this);
                    }
                }
//                Resource IDs这一类的变量在API14之后的库项目里是不能在switch case statement里面使用的

//                switch (attr) {
//                    case R.styleable.LEDDisplay_locationIcon:
//                        if (a.hasValue(R.styleable.LEDDisplay_locationIcon)) {
//                            locationIcon = a.getDrawable(R.styleable.LEDDisplay_locationIcon);
//                            locationIcon.setCallback(this);
//                        }
//                        break;
//                    case R.styleable.LEDDisplay_messageIcon:
//                        if (a.hasValue(R.styleable.LEDDisplay_messageIcon)) {
//                            messageIcon = a.getDrawable(R.styleable.LEDDisplay_messageIcon);
//                            messageIcon.setCallback(this);
//                        }
//                        break;
//                }
            }
            a.recycle();

            //设置默认TextPaint
            mRSSITextPaint = new TextPaint();
            mRSSITextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mRSSITextPaint.setTextAlign(Paint.Align.LEFT);

            mTopTextPaint = new TextPaint();
            mTopTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mTopTextPaint.setTextAlign(Paint.Align.LEFT);

            mMainTextPaint = new TextPaint();
            mMainTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mMainTextPaint.setTextAlign(Paint.Align.LEFT);

            mSecondTextPaint = new TextPaint();
            mSecondTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mSecondTextPaint.setTextAlign(Paint.Align.LEFT);

            mBottomTextPaint = new TextPaint();
            mBottomTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mBottomTextPaint.setTextAlign(Paint.Align.LEFT);

            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            // 设置“空心”的外框的宽度
            mPaint.setStrokeWidth(2);
            // 设置Paint的颜色
            mPaint.setColor(Color.GRAY);
            // 设置Paint为无锯齿
            mPaint.setAntiAlias(true);

            mFirstLineTextPaint = new TextPaint();
            mFirstLineTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mFirstLineTextPaint.setTextAlign(Paint.Align.LEFT);

            mSecondLineTextPaint = new TextPaint();
            mSecondLineTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mSecondLineTextPaint.setTextAlign(Paint.Align.LEFT);

            mThreeLineTextPaint = new TextPaint();
            mThreeLineTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mThreeLineTextPaint.setTextAlign(Paint.Align.LEFT);

            mFourLineTextPaint = new TextPaint();
            mFourLineTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mFourLineTextPaint.setTextAlign(Paint.Align.LEFT);

            invalidateTextPaintAndMeasurements();
        }
    }

    private void invalidateTextPaintAndMeasurements() {
        if (!isInEditMode()) {
            //工作模式文字
            mTopTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
            mTopTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mTopTextWidth = mTopTextPaint.measureText(mTopString);
            Paint.FontMetrics fontMetrics = mTopTextPaint.getFontMetrics();
            mTopTextHeight = fontMetrics.bottom - fontMetrics.top;

            //RSSI场强值文字
            if (showRSSI) {
                mRSSITextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 7));
                mRSSITextPaint.setColor(Color.argb(0xAA, 0x54, 0x6E, 0x7A));
                mRSSITextWidth = mRSSITextPaint.measureText(rssiString);
                fontMetrics = mRSSITextPaint.getFontMetrics();
                mRSSITextHeight = fontMetrics.bottom - fontMetrics.top;
            }

            //主信息文字
            mMainTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 16));
            mMainTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mMainTextWidth = mMainTextPaint.measureText(mainMessage);
            fontMetrics = mMainTextPaint.getFontMetrics();
            mMainTextHeight = fontMetrics.bottom;

            //第二行信息文字
            mSecondTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 16));
            mSecondTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mSecondTextWidth = mSecondTextPaint.measureText(secondMessage);
            fontMetrics = mSecondTextPaint.getFontMetrics();
            mSecondTextHeight = fontMetrics.descent - fontMetrics.ascent;

            //底部文字
            mBottomTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 16));
            mBottomTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mBottomTextWidth = mBottomTextPaint.measureText(bottomMessage);
            fontMetrics = mBottomTextPaint.getFontMetrics();
            mBottomTextHeight = fontMetrics.bottom;

            //第一行文字
            mFirstLineTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 14));
            mFirstLineTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mFirstLineTextWidth = mFirstLineTextPaint.measureText(mFirstTextString);
            fontMetrics = mFirstLineTextPaint.getFontMetrics();

            mFirstLineTextHeight = fontMetrics.bottom;

            //第二行文字
            mSecondLineTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 14));
            mSecondLineTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mSecondLineTextWidth = mSecondLineTextPaint.measureText(mSecondTextString);
            fontMetrics = mSecondLineTextPaint.getFontMetrics();
            mSecondLineTextHeight = fontMetrics.bottom;

            //第三行文字
            mThreeLineTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 14));
            mThreeLineTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mThreeLineTextWidth = mThreeLineTextPaint.measureText(mThreeTextString);
            fontMetrics = mThreeLineTextPaint.getFontMetrics();
            mThreeLineTextHeight = fontMetrics.bottom;

            //第四行文字
            mFourLineTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 14));
            mFourLineTextPaint.setColor(Color.rgb(0x54, 0x6E, 0x7A));
            mFourLineTextWidth = mFourLineTextPaint.measureText(mFourTextString);
            fontMetrics = mFourLineTextPaint.getFontMetrics();
            mFourLineTextHeight = fontMetrics.bottom;

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int contentWidth = getWidth() - paddingLeft - paddingRight;
            int contentHeight = getHeight() - paddingTop - paddingBottom;

            int iconTop = DisplayUtil.sp2px(getContext(), 5);
            int iconBottom = DisplayUtil.sp2px(getContext(), 20);
            int leftSideIconLeftPadding = paddingLeft + DisplayUtil.sp2px(getContext(), 5);

            //画信号强度
            if (rssiIcon != null) {
                if (rssiValue <= -110) {
                    rssiIcon = rssiIcon0;
                } else if (rssiValue > -110 && rssiValue <= -100) {
                    rssiIcon = rssiIcon1;
                } else if (rssiValue > -100 && rssiValue <= -90) {
                    rssiIcon = rssiIcon2;
                } else if (rssiValue > -90 && rssiValue <= -80) {
                    rssiIcon = rssiIcon3;
                } else {
                    rssiIcon = rssiIcon4;
                }
                rssiIcon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                rssiIcon.draw(canvas);
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 15);
                //隔离空间
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 10);
            }

            //画信标
            if (beaconType != BeaconTypes.Lost) {
                if (beaconType == BeaconTypes.ExplainSuccess) {
                    beacon_icon = beacon_true;
                }
                if (beaconType == BeaconTypes.ExplainFailure) {
                    beacon_icon = beacon_false;
                }
                beacon_icon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                beacon_icon.draw(canvas);
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 15);
                //隔离空间
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 10);
            }

            // 是否超级设备
            if (superDeviceIcon != null && isSuperDevice) {
                superDeviceIcon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                superDeviceIcon.draw(canvas);

                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 15);
                //隔离空间
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 10);
            }

            // 是否已经定位
            if (locationIcon != null && locationEnable) {
                locationIcon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                locationIcon.draw(canvas);

                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 15);
                //隔离空间
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 10);
            }

            //是否未读信息
            if (messageIcon != null && haveMessage) {
                messageIcon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                messageIcon.draw(canvas);
                //隔离空间
                leftSideIconLeftPadding += DisplayUtil.sp2px(getContext(), 25);
            }

            //SOS图标
            if (sosIcon != null && haveSOSAlarm) {
                sosIcon.setBounds(leftSideIconLeftPadding,
                        iconTop,
                        leftSideIconLeftPadding + DisplayUtil.sp2px(getContext(), 15),
                        iconBottom);
                sosIcon.draw(canvas);
            }

            //绘制工作模式
            canvas.drawText(mTopString,
                    paddingLeft + (contentWidth - (mTopTextWidth + mTopTextHeight / 2)),
                    paddingTop + mTopTextHeight,
                    mTopTextPaint);

            //显示RSSI场强值
            if (showRSSI && !"".equals(rssiString)) {
                canvas.drawText(rssiString,
                        rssiIcon.getBounds().left,
                        rssiIcon.getBounds().bottom + DisplayUtil.sp2px(getContext(), 8),
                        mRSSITextPaint);
            }

            /********************* 右侧内容区域 ***************/
            // 绘制空心矩形
            mRectF = new RectF(
                    contentWidth / 2
                    , contentHeight / 5
                    , contentWidth - mTopTextHeight / 2
                    , contentHeight - mTopTextHeight * 2
            );
            canvas.drawRoundRect(mRectF, 10, 10, mPaint);

            if (secondLineTextShow){
                //显示一行
                canvas.drawText(mainMessage,
                        (mRectF.left + mRectF.right - mMainTextWidth) / 2,
                        (mRectF.top + mRectF.bottom - mMainTextHeight - mSecondTextHeight) / 2,
                        mMainTextPaint);
            }else{
                //显示一行
                canvas.drawText(mainMessage,
                        (mRectF.left + mRectF.right - mMainTextWidth) / 2,
                        (mRectF.top + mRectF.bottom - mMainTextHeight) / 2,
                        mMainTextPaint);
            }

            if (!"".equals(secondMessage) && secondLineTextShow) {
                canvas.drawText(secondMessage,
                        (mRectF.left + mRectF.right - mSecondTextWidth) / 2,
                        paddingTop + (contentHeight / 2 + mSecondTextHeight ),
                        mSecondTextPaint);
            }

            //绘制下方文字
            if (!"".equals(bottomMessage)) {
                canvas.drawText(bottomMessage,
                        paddingLeft + (contentWidth - (mBottomTextWidth + mTopTextHeight / 2)),
                        paddingTop + contentHeight - mBottomTextHeight * 2,
                        mBottomTextPaint);
            }

            /********************* 左侧内容区域 ***************/
            if (!"".equals(mFirstTextString)) {
                canvas.drawText(mFirstTextString,
                        paddingLeft + (contentWidth / 2) / 7,
                        (float) (paddingTop + contentHeight / 3.1) - mFirstLineTextHeight,
                        mFirstLineTextPaint);
            }

            if (!"".equals(mSecondTextString)) {
                canvas.drawText(mSecondTextString,
                        paddingLeft + (contentWidth / 2) / 7,
                        (float) (paddingTop + contentHeight / 2.1) + mSecondLineTextHeight,
                        mSecondLineTextPaint);
            }

            if (!"".equals(mThreeTextString)) {
                canvas.drawText(mThreeTextString,
                        paddingLeft + (contentWidth / 2) / 7,
                        (float) (paddingTop + contentHeight / 1.5) + mThreeLineTextHeight,
                        mThreeLineTextPaint);
            }

            if (!"".equals(mFourTextString)) {
                canvas.drawText(mFourTextString,
                        paddingLeft + (contentWidth / 2) / 7,
                        (float) (paddingTop + contentHeight / 1.1) - mFourLineTextHeight,
                        mFourLineTextPaint);
            }

        }
    }

    /**
     * 信标类型
     */
    public enum BeaconTypes {
        /**
         * 信标丢失
         */
        Lost,
        /**
         * 解释失败
         */
        ExplainFailure,
        /**
         * 解释成功
         */
        ExplainSuccess,
    }

    /**
     * 刷新屏幕
     */
    public void refreshScreen() {
        invalidateTextPaintAndMeasurements();
        this.postInvalidate();
    }

    /***************** 右侧数据显示 调用的方法 *****************/
    /**
     * 显示第二行文字
     * @param mainMessage
     * @param secondMessage
     * @param secondLineTextShow
     */
    public void showMessage(String mainMessage, String secondMessage, boolean secondLineTextShow) {
        this.mainMessage = mainMessage;
        this.secondMessage = secondMessage;
        this.secondLineTextShow = secondLineTextShow;
    }

    /**
     * 单行主文字
     * @param mainMessage
     */
    public void showMessage(String mainMessage) {
        showMessage(mainMessage,"",false);
    }

    /**
     * 顶部文字
     * @param topString
     */
    public void setTopString(String topString){
        this.mTopString = topString;
    }

    /**
     * 会话时间
     * @param time
     */
    public void setSessionTime(final int time) {
        this.sessionTime = time;
        if (sessionTime >= 0 ){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (sessionTime >= 0){
                        sessionTime++;
                        bottomMessage = String.format("%02d:%02d s", (sessionTime /60), sessionTime % 60 );
                        invalidateTextPaintAndMeasurements();
                        postInvalidate();
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    bottomMessage = "";
                    postInvalidate();
                }
            });
            thread.start();
        }else{
            bottomMessage = "";
            postInvalidate();
        }
    }

    /***************** 左侧数据显示 调用的方法 *****************/

    /**
     * 是否定位
     * @param locationEnable
     */
    public void setLocationEnable(boolean locationEnable) {
        this.locationEnable = locationEnable;
    }

    /**
     * 是否有未读短信
     * @param haveMessage
     */
    public void setHaveMessage(boolean haveMessage) {
        this.haveMessage = haveMessage;
    }

    /**
     * 是否超级设备
     * @param superDevice
     */
    public void setSuperDevice(boolean superDevice) {
        isSuperDevice = superDevice;
    }

    /**
     * 是否收到SOS
     * @param haveSOSAlarm
     */
    public void setHaveSOSAlarm(boolean haveSOSAlarm) {
        this.haveSOSAlarm = haveSOSAlarm;
    }

    /**
     * 是否开启信标
     * @param enable
     */
    public void startBeaconCheck(boolean enable) {
        isEnable = enable;
        startBeaconCheck();
    }

    /***
     * 是否显示RSSI值
     * @param showRSSI
     */
    public void setShowRSSI(boolean showRSSI) {
        this.showRSSI = showRSSI;
    }

    /**
     * 设置RSSI值
     * @param rssiValue
     */
    public void setRssiValue(float rssiValue) {
        this.rssiValue = rssiValue;
        this.rssiString = ((int) rssiValue) + " dBm";
    }

    /**
     * 设置信标状态
     * @param beaconType
     */
    public void setBeaconType(BeaconTypes beaconType) {
        this.beaconType = beaconType;
        if (beaconType != BeaconTypes.Lost) {
            lastBeaconTime = Calendar.getInstance().getTimeInMillis();
        }
    }

    /**
     * 开启信标执行线程
     */
    private void startBeaconCheck(){
        if (beaconShowThread == null){
            beaconShowThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnable) {
                        if (Calendar.getInstance().getTimeInMillis() - lastBeaconTime > beaconShowTimeLength && beaconType != BeaconTypes.Lost) {
                            beaconType = BeaconTypes.Lost;
                            postInvalidate();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            beaconShowThread.start();
        }
    }

    /***
     * 常规模式显示内容
     */
    public void showMessage(String mFirstTextString, String mSecondTextString, String mThreeTextString, String mFourTextString){
        this.mFirstTextString = mFirstTextString;
        this.mSecondTextString = mSecondTextString;
        this.mThreeTextString = mThreeTextString;
        this.mFourTextString = mFourTextString;
    }

    /**
     * 集群模式下显示内容
     * @param mSecondTextString
     * @param mThreeTextString
     */
    public void showMessage(String mSecondTextString, String mThreeTextString){
        this.mSecondTextString = mSecondTextString;
        this.mThreeTextString = mThreeTextString;
    }

    //设置常规默认组
    public void setDMDefaultGroupName(String defaultGroupName){
        this.mFourTextString = defaultGroupName;
    }
}
