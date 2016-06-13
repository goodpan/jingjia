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

public class BarChartActivity extends AppCompatActivity {
    private BarChartView mChartOne;
    private final String[] mLabelsOne= {"福州", "厦门", "北京", "大连", "广州","上海","济南"};
    private final float [][] mValuesOne = {{950f, 750f, 550f, 450f, 100f,880f,501f}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
//        ActionBar ab = getSupportActionBar();
//        // 返回箭头（默认不显示）
//        ab.setDisplayHomeAsUpEnabled(true);
//        // 左侧图标点击事件使能
//        ab.setHomeButtonEnabled(true);
//        // 显示标题
//        ab.setDisplayShowTitleEnabled(false);
//        //显示自定义视图
//        ab.setDisplayShowCustomEnabled(true);
//        //设置BarChart
        // 标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //将标题置为空
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        BarChartView mBarChartView = (BarChartView)findViewById(R.id.barchart);
        produceBarChart(mBarChartView);
    }

    /**
     *
     * barChart  绘制方法
     *
     */
    public void produceBarChart(ChartView chart){
        BarChartView barChart = (BarChartView) chart;

        BarSet barSet = new BarSet(mLabelsOne, mValuesOne[0]);
        barSet.setColor(Color.parseColor("#47c0fc"));//设置 图形填充颜色
        barChart.addData(barSet);

        barChart.setSetSpacing(Tools.fromDpToPx(15));//....
        barChart.setBarSpacing(Tools.fromDpToPx(55));//柱状间距
        barChart.setRoundCorners(Tools.fromDpToPx(2));//柱子 四角弧度

        barChart.setBorderSpacing(1) //设置等分X轴等分数量
                .setAxisBorderValues(0, 1000, 200) //设置X轴 数字范围
                .setXAxis(true)
                .setYAxis(true)//设置是否显示 Y坐标
//                .setXLabels(XController.LabelPosition.OUTSIDE) //设置 X轴数字 显示位置
//                .setYLabels(YController.LabelPosition.NONE); //同上
                .setLabelsColor(Color.parseColor("#FFFFFF")) //设置 X Y 轴文字 的颜色
                .setAxisColor(Color.parseColor("#47c0fc")); //设置 X Y 轴的颜色
        barChart.show();

    }
}
