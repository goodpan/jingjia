package com.xmfcdz.jingjia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.iwgang.countdownview.CountdownView;

public class LvXinActivity extends AppCompatActivity implements CountdownView.OnCountdownEndListener{
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<ArrayList<ArrayList<String>>>();
    private TextView tvTime, tvOptions;
    private ImageView warningView;
    private long offsetDay;
    private Context ctx;
    private SharedPreferences sp;
    private CountdownView mCvCountdownViewTest;
    SharedPreferences.Editor editor;
    //半圆
    com.xmfcdz.jingjia.view.CircleChartView halfchart = null;
    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    View vMasker;
    Button tvBtn;

    //servies
    private ServiceConnection sc;
    private boolean isBind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv_xin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //将标题置为空
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        mCvCountdownViewTest = (CountdownView)findViewById(R.id.cv_countdownViewTest);
        mCvCountdownViewTest.customTimeShow(true,true,true,false,false);
        //获取SharedPreferences对象
        ctx = LvXinActivity.this;
        sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);

        vMasker=findViewById(R.id.vMasker);
        tvTime=(TextView) findViewById(R.id.tvTime);
        tvBtn = (Button)findViewById(R.id.btn_time);
        warningView = (ImageView)findViewById(R.id.warning);
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        initStartTime();

        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                inintCountdownView(date);
            }
        });
        //弹出时间选择器
        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
        //选项选择器
        pvOptions = new OptionsPickerView(this);

        //圆形
        halfchart = (com.xmfcdz.jingjia.view.CircleChartView)findViewById(R.id.halfcircle_view);
        halfchart.setPercentage(22);

        //services操作
        sc = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                CountdownService.SimpleBinder sBinder = (CountdownService.SimpleBinder)service;
                sBinder.startCountDown();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        //绑定service
        bindService(new Intent(LvXinActivity.this, CountdownService.class), sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveCountdownDate();
        super.onStop();
    }
    @Override
    protected void onResume() {
        initCountdownView();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        saveCountdownDate();
        super.onDestroy();
    }

    private  void inintCountdownView(Date date){
        //滤芯使用周期天数
        long lifeDay = 30;
        editor = sp.edit();
        editor.putString("STARTTIME", getTime(date));
        editor.commit();
        long diff = caleOffsetDay(date, new Date());
        if (diff >= 30) {
            new AlertDialog.Builder(LvXinActivity.this).setTitle("设置提醒")
                    .setMessage("启用时间设置已超过日期前30天，如果正确，说明滤芯已经过期，请更换滤芯！")
                    .setIcon(R.drawable.alert_list_5).show();
            //清空本地存储
            sp.edit().clear().commit();
            tvTime.setText("起始时间：" + getTime(date) + "(警示状态)");
            tvTime.setTextColor(Color.parseColor("#ff0000"));
            mCvCountdownViewTest.start(1);
            mCvCountdownViewTest.stop();
            mCvCountdownViewTest.allShowZero();
            //warningView.setVisibility(View.VISIBLE);
            //tvTime.setText("启用时间设置已超过日期前30天，如果正确，说明滤芯已经过期，请更换滤芯！");
        } else {
            //滤芯剩余时间
            offsetDay = lifeDay - diff;
            //倒计时
            long time = (long) offsetDay * 24 * 60 * 60 * 1000;
            tvTime.setTextColor(Color.parseColor("#ffffff"));
            //存入数据
            editor = sp.edit();
            editor.putLong("OFFSETDAY", offsetDay);
            editor.commit();
            tvTime.setText("起始时间：" + getTime(date));
            //开始倒计时
            mCvCountdownViewTest.start(time);
        }
    }

    //保存倒计时
    public void saveCountdownDate(){
        long day =(long)mCvCountdownViewTest.getDay();
        long hour = (long)mCvCountdownViewTest.getHour();
        long minute = (long)mCvCountdownViewTest.getMinute();
        long second =(long) mCvCountdownViewTest.getSecond();
        Log.e("putdate", Long.toString(day) + Long.toString(hour) + Long.toString(minute) + Long.toString(second));
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("DAY", day);
        editor.putLong("HOUR", hour);
        editor.putLong("MINUTE", minute);
        editor.putLong("SECOND", second);
        editor.commit();
    }
    //获取本地倒计时数据
    public void initCountdownView(){
        //返回STRING_KEY的值
        long res = sp.getLong("OFFSETDAY", 0);
        Log.e("res", Long.toString(res));
        if(res == 0){
            Log.e("b","bbbb");
            return;
        }else{
            long day = sp.getLong("DAY", 0);
            long hour = sp.getLong("HOUR", 0);
            long minute = sp.getLong("MINUTE", 0);
            long second = sp.getLong("SECOND", 0);
//            long time = (long)res * 24 * 60 * 60 * 1000;
            //mDay = (int)(ms / (1000 * 60 * 60 * 24));
            Log.e("getdate",Long.toString(day));
            long ms = (day+1)*(1000 * 60 * 60 * 24);
            mCvCountdownViewTest.start(ms);
        }
    }

    @Override
    public void onEnd(CountdownView cv) {
        Object tag = cv.getTag();
        if (null != tag) {
            Log.i("wg", "tag = " + tag.toString());
        }

    }
    //获取当前时间
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    //计算时间差
    public static String calcOffsetDate(Date start,Date end){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStartString = df.format(start);
        String dateEndString = df.format(end);
        String offsetDate = null;
        try
        {
            Date startDate = df.parse(dateStartString);
            Date endDate = df.parse(dateEndString);
            long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            offsetDate = (""+days+"天"+hours+"小时"+minutes+"分");
        }
        catch (Exception e)
        {
        }
        return offsetDate;
    }

    public static long caleOffsetDay(Date start,Date end){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStartString = df.format(start);
        String dateEndString = df.format(end);
        long days = 0;
        try
        {
            Date startDate = df.parse(dateStartString);
            Date endDate = df.parse(dateEndString);
            long diff = endDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别
            days = diff / (1000 * 60 * 60 * 24);
        }
        catch (Exception e)
        {
        }
        return days;
    }

    private void initStartTime(){
        String starTimeStr = sp.getString("STARTTIME","未设置");
        tvTime.setText("起始时间："+starTimeStr);
    }

}

