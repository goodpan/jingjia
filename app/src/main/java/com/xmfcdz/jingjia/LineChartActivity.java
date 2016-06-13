package com.xmfcdz.jingjia;


import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;


public class LineChartActivity extends AppCompatActivity {
    private LineChartView mChart;
//    private final String[] mLabelsThree= {"1", "2", "3", "4", "5", "6", "7"};
//    private final float[][] mValuesThree = {  {405f, 507f, 400f, 800f, 205f, 300f, 605f},
//            {105f, 205f, 150f, 500f, 505f, 505f, 300f},
//            {800f, 750f, 780f, 150f, 800f, 800f, 500f}};

    /** line chart */
    private BarChartView mChartOne;
    private final String[] mLabels= {"星期一", "星期二", "星期三", "星期四", "星期五","星期六","星期日"};
    private final float [][] mValues = {{950f, 750f, 550f, 450f, 100f,880f,501f}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //将标题置为空
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        LineChartView mLineChart = (LineChartView)findViewById(R.id.linechart);
        produceLineChar(mLineChart);
    }

    public void produceLineChar(LineChartView chart){
        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#47c0fc"))//设置直线颜色
                .setDotsStrokeThickness(Tools.fromDpToPx(1))
                .setDotsStrokeColor(Color.parseColor("#47c0fc"))//设置 圈圈颜色
                .setDotsColor(Color.parseColor("#eef1f6"));
//                .setFill(Color.parseColor("#47c0fc"));//设置填充颜色
        chart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));
        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 1000, 200);
        chart.setLabelsColor(Color.parseColor("#ffffff"));
        chart.setFontSize(32);
        chart.setAxisColor(Color.parseColor("#47c0fc"));
        chart.show();
    }

}
