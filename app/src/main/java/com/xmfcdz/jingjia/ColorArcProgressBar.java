package com.xmfcdz.jingjia;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * colorful arc progress bar
 * Created by shinelw on 12/4/15.
 */
public class ColorArcProgressBar extends View{

    private int mWidth;
    private int mHeight;
    private int diameter;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint allArcPaint;
    private Paint progressPaint;
    private Paint vTextPaint;
    private Paint hintPaint;
    private Paint degreePaint;
    private Paint curSpeedPaint;

    private Paint detailPaint;

//    private TextPaint detailPaint;

    private RectF bgRect;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 135;
    private float sweepAngle = 270;
    private float currentAngle = 0;
    private float lastAngle;
    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    private float maxValues = 60;
    private float curValues = 0;
    private float bgArcWidth = dipToPx(2);
    private float progressWidth = dipToPx(10);
    private float textSize = dipToPx(60);
    private float hintSize = dipToPx(22);
    private float curSpeedSize = dipToPx(22);
    private float detailTextSize = dipToPx(16);
    private int aniSpeed = 1000;
    private float longdegree = dipToPx(13);
    private float shortdegree = dipToPx(5);
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);

    private String hintColor = "#ffffff";
    private String longDegreeColor = "#ffffff";
    private String shortDegreeColor = "#111111";
    private String bgArcColor = "#47c0fc";//动态旋转圆弧颜色
    private String titleString;
    private String hintString;
    private String detailString;

    private boolean isShowCurrentSpeed = true;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedDial;
    private boolean isNeedContent;
    private boolean isNeedDetail;

    // sweepAngle / maxValues 的值
    private float k;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_front_color2, color1);
        int color3 = a.getColor(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_front_color3, color1);
        colors = new int[]{color1, color2, color3, color3};

        sweepAngle = a.getInteger(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_total_engle, 270);
        bgArcWidth = a.getDimension(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_back_width, dipToPx(2));
        progressWidth = a.getDimension(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_front_width, dipToPx(10));
        isNeedTitle = a.getBoolean(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedDetail = a.getBoolean(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_is_need_detail, false);
        isNeedContent = a.getBoolean(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_is_need_unit, false);
        isNeedDial = a.getBoolean(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_is_need_dial, false);
        hintString = a.getString(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_string_unit);
        titleString = a.getString(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_string_title);
        detailString = a.getString(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_string_detail);
        curValues = a.getFloat(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_current_value, 0);
        maxValues = a.getFloat(com.xmfcdz.jingjia.R.styleable.ColorArcProgressBar_max_value, 60);
        setCurrentValues(curValues);
        setMaxValues(maxValues);
        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        int height= (int) (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        //设置view的大小
        setMeasuredDimension(width, height);
    }

    //初始化各种画笔
    private void initView() {
//        diameter = getScreenWidth() /2+20;
        diameter = 3 * getScreenWidth() / 5;
        //弧形的矩阵区域
        bgRect = new RectF();
        bgRect.top = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.left = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.right = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);
        bgRect.bottom = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);

        //圆心
        centerX = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;
        centerY = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;

        //外部刻度线
        degreePaint = new Paint();
        degreePaint.setColor(Color.parseColor(longDegreeColor));

        //整个弧形
        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);//设置填充风格
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor(Color.parseColor(bgArcColor));
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);


        //当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);//设置画笔为无锯齿
//        progressPaint.setStyle(Paint.Style.STROKE);;//设置填充风格
        progressPaint.setStyle(Paint.Style.FILL);;//设置填充风格
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(Color.parseColor("#ffffff"));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);


        //内容显示文字
        vTextPaint = new Paint();
        vTextPaint.setTextSize(textSize);
        vTextPaint.setColor(Color.parseColor("#ffffff"));

        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(Color.parseColor("#ffffff"));
        hintPaint.setTextAlign(Paint.Align.CENTER);


        //显示标题文字
        curSpeedPaint = new Paint();
        curSpeedPaint.setTextSize(curSpeedSize);
        curSpeedPaint.setColor(Color.parseColor(hintColor));
        curSpeedPaint.setTextAlign(Paint.Align.CENTER);

        //显示描述文字
        detailPaint = new Paint();
        detailPaint.setTextSize(detailTextSize);
        detailPaint.setColor(Color.parseColor(hintColor));
        detailPaint.setTextAlign(Paint.Align.CENTER);
        //显示描述文字
//        detailPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        detailPaint.setColor(Color.parseColor(hintColor));
//        detailPaint.setAntiAlias(true);
//        detailPaint.setTextSize(23);
//        detailPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        //实例化sweepGradient角度渐变
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        //创建Matrix矩阵工具类
        rotateMatrix = new Matrix();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        if (isNeedDial) {
            //画刻度线
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9, centerX, centerY);
                    continue;
                }
                if (i % 5 == 0) {
                    degreePaint.setStrokeWidth(dipToPx(2));
                    degreePaint.setColor(Color.parseColor(longDegreeColor));
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - longdegree, degreePaint);
                } else {
                    degreePaint.setStrokeWidth(dipToPx(1.4f));
                    degreePaint.setColor(Color.parseColor(shortDegreeColor));
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longdegree - shortdegree) / 2,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longdegree - shortdegree) / 2 - shortdegree, degreePaint);
                }

                canvas.rotate(9, centerX, centerY);
            }
        }

        //整个弧
        canvas.drawArc(bgRect, startAngle, sweepAngle, false, allArcPaint);

        //控制Matrix进行旋转。设置以centerX, centerY为轴心进行旋转，第一个参数控制旋转的角度。
        rotateMatrix.setRotate(130, centerX, centerY);
        //设置渐变效果
        sweepGradient.setLocalMatrix(rotateMatrix);
        //将旋转及渐变效果应用于progressPaint画笔中
        progressPaint.setShader(sweepGradient);

        //当前进度
        canvas.drawArc(bgRect, startAngle, currentAngle, true, progressPaint);
        //标题
        if (isNeedTitle) {
            canvas.drawText(titleString, centerX, centerY -  textSize, curSpeedPaint);
        }
        //TDS值
        if (isNeedContent) {
            canvas.drawText(String.format("%.0f", curValues), centerX, centerY, vTextPaint);
        }

        if (isNeedUnit) {
            canvas.drawText(hintString, centerX, centerY + 2* textSize / 3, hintPaint);
        }

        //绘制描述文字
        if (isNeedDetail) {
            canvas.drawText(detailString, centerX, centerY + 4* textSize / 3, detailPaint);
        }
        invalidate();

    }

    /**
     * 设置最大值
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = sweepAngle/maxValues;
    }

    /**
     * 设置当前值
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues > maxValues) {
            currentValues = maxValues;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        lastAngle = currentAngle;
        setAnimation(lastAngle, currentValues * k, aniSpeed);
    }

    /**
     * 设置整个圆弧宽度
     * @param bgArcWidth
     */
    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    /**
     * 设置进度宽度
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    /**
     * 设置速度文字大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置单位文字大小
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    /**
     * 设置直径大小
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        this.titleString = title;
    }

    /**
     * 设置描述
     * @param title
     */
    public void setDetail(String title){
        this.detailString = title;
    }
    /**
     * 设置是否显示标题
     * @param isNeedTitle
     */
    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    /**
     * 设置是否显示单位文字
     * @param isNeedDetail
     */
    private void setIsNeedDetail(boolean isNeedDetail) {
        this.isNeedDetail = isNeedDetail;
    }

    /**
     * 设置是否显示单位文字
     * @param isNeedUnit
     */
    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }
    /**
     * 设置是否显示外部刻度盘
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle= (float) animation.getAnimatedValue();
                curValues = currentAngle/k;
            }
        });
        progressAnimator.start();
    }

    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
    * 设置文字的行高
    * */

}
