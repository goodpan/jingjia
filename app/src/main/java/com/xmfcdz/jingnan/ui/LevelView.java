package com.xmfcdz.jingnan.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.xmfcdz.jingjia.R;

import java.util.jar.Attributes;

/**
 * Created by Administrator on 2016/5/2.
 */
public class LevelView extends View {
    Paint paint;
    private int viewWidth;
    public LevelView(Context context) {
        super(context);

    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawRoundRectForMain(canvas);
        drawLeftRoundRect(canvas);
        drawMiddleRect(canvas);
        drawTextUnderLine(canvas);
        drawTextAboveLine(canvas);
    }
    //绘制主圆角矩形
    private void drawRoundRectForMain(Canvas canvas){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        //绘制圆角矩形
        viewWidth = this.getWidth();
        RectF rel = new RectF(20, 100, viewWidth-20, 120);
        canvas.drawRoundRect(rel, 10, 10, paint);
    }
    //绘制左侧1/3矩形
    private void drawLeftRoundRect(Canvas canvas){
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        viewWidth = this.getWidth();
        RectF rel = new RectF(20, 100, viewWidth / 3, 120);
        canvas.drawRoundRect(rel, 10, 10, paint);
    }
    //绘制中间1/3矩形
    private void drawMiddleRect(Canvas canvas){
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        viewWidth = this.getWidth();
        RectF rel = new RectF(20 + viewWidth / 3 - 30, 100, viewWidth * 2/ 3, 120);
        canvas.drawRect(rel, paint);
    }

    //绘制文本
    private void drawTextUnderLine(Canvas canvas){
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(40);
        paint.setTextSize(26);
        viewWidth = this.getWidth();
        paint.setColor(Color.GREEN);
        canvas.drawText("优良", viewWidth * 1 / 6, 160, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawText("一般", viewWidth * 1 / 2, 160, paint);
        paint.setColor(Color.RED);
        canvas.drawText("偏差", viewWidth * 5 / 6, 160, paint);
    }
    //绘制刻度数字
    private void drawTextAboveLine(Canvas canvas){
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(40);
        paint.setTextSize(26);
        paint.setColor(Color.GREEN);
        canvas.drawText("0", 20, 60, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawText("100", viewWidth / 3 - 30, 60, paint);
        paint.setColor(Color.RED);
        canvas.drawText("300", viewWidth * 2 / 3 - 20, 60, paint);
        paint.setColor(Color.RED);
        canvas.drawText("1000+", viewWidth - 100, 60, paint);
    }
}
