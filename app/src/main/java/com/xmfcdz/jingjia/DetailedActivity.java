package com.xmfcdz.jingjia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.xmfcdz.jingnan.ui.LevelView;

import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends Activity {
    List<CityItem> cityList;
    RelativeLayout itmel;
    GridView gridView;
    GridView gridViewCity;
    TextView mTextView = null;
    SpannableString msp = null;
    RelativeLayout level_view;
    private BarChartView mChartOne;
    private final String[] mLabels= {"星期一", "星期二", "星期三", "星期四", "星期五","星期六","星期日"};
    private final float [][] mValues = {{950f, 750f, 550f, 450f, 100f,880f,501f}};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        //设置TDS值字体
        initTextFontStyle();
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        gridView = (GridView) findViewById(R.id.grid);
        gridViewCity = (GridView) findViewById(R.id.grid_city);
        setData();
        setGridView(gridView);
        setGridView(gridViewCity);

        LineChartView mLineChart = (LineChartView)findViewById(R.id.linechart_week);
        produceLineChar(mLineChart);
        //绘制等级图
        initLevelView();

    }

    private void initLevelView() {
        LevelView levelView = new LevelView(this);
        level_view =(RelativeLayout) findViewById(R.id.level_view);
        //通知view组件重绘
        levelView.invalidate();
        level_view.addView(levelView);

    }
    private void initTextFontStyle(){
        mTextView = (TextView)findViewById(R.id.value_of_tds);
        //创建一个 SpannableString对象
        msp = new SpannableString("122");
        //设置字体(default,default-bold,monospace,serif,sans-serif)
        msp.setSpan(new TypefaceSpan("sans-serif"), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       //设置字体大小（绝对值,单位：像素）
        msp.setSpan(new AbsoluteSizeSpan(100,true), 0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
       //设置字体样式正常，粗体，斜体，粗斜体
        msp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //正常
        //SpannableString对象设置给TextView
        mTextView.setText(msp);
    }
    private void setData() {
        cityList = new ArrayList<CityItem>();
        CityItem item = new CityItem();
        item.setCityName("深圳");
        item.setCityTDS("20");
        cityList.add(item);
        item = new CityItem();
        item.setCityName("上海");
        item.setCityTDS("60");
        cityList.add(item);
        item = new CityItem();
        item.setCityName("广州");
        item.setCityTDS("69");
        cityList.add(item);
        item = new CityItem();
        item.setCityName("北京");
        item.setCityTDS("111");
        cityList.add(item);
        item = new CityItem();
        item.setCityName("武汉");
        item.setCityTDS("233");
        cityList.add(item);
        item = new CityItem();
        item.setCityName("孝感");
        item.setCityTDS("108");
        cityList.add(item);
        cityList.addAll(cityList);
    }
    /**设置GirdView参数，绑定数据*/
    private void setGridView(GridView gridView) {
        int size = cityList.size();
        int length = 50;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(38); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),
                cityList);
        gridView.setAdapter(adapter);
    }

    /**GirdView 数据适配器*/
    public class GridViewAdapter extends BaseAdapter {
        Context context;
        List<CityItem> list;
        public GridViewAdapter(Context _context, List<CityItem> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.gridview_item, null);
            TextView tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            TextView tvCode = (TextView) convertView.findViewById(R.id.tvCode);
            CityItem city = list.get(position);
            tvCity.setText(city.getCityName());
            tvCode.setText(city.getCityTDS());
            return convertView;
        }
    }
    public class CityItem {
        private String cityName;
        private String cityTDS;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCityTDS() {
            return cityTDS;
        }

        public void setCityTDS(String cityTDS) {
            this.cityTDS = cityTDS;
        }
    }
    public void produceLineChar(LineChartView chart){
        LineSet dataset = new LineSet(mLabels, mValues[0]);
        dataset.setColor(Color.parseColor("#47c0fc"))//设置直线颜色
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
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
    //退出
    public void back(View view){
        finish();
    }
}
