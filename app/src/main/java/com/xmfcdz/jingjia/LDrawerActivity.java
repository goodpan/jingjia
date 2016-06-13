package com.xmfcdz.jingjia;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.app.ActionBar;
import android.widget.SimpleAdapter;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LDrawerActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ldrawer);
        ActionBar ab = getSupportActionBar();
        // 返回箭头（默认不显示）
        ab.setDisplayHomeAsUpEnabled(true);
        // 左侧图标点击事件使能
        ab.setHomeButtonEnabled(true);
        // 显示标题
        ab.setDisplayShowTitleEnabled(false);
        //显示自定义视图
        ab.setDisplayShowCustomEnabled(true);
        //加载自定义actionbar布局
//        View actionbarLayout = LayoutInflater.from(this).inflate(
//                R.layout.actionbar_layout, null);
//        ab.setCustomView(actionbarLayout);
        //去掉阴影
        ab.setElevation(0);

        //主布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //左侧菜单列表
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        //创建drawerarrow
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };



        //创建ActionBarDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this, //宿主
                mDrawerLayout,//DrawerLayout 对象
                drawerArrow,//替换actionbar上的'Up'图标
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);//设置监听drawer切换
        mDrawerToggle.syncState();//该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标

        drawerArrow.setProgress(0f); // normal position
        drawerArrow.setProgress(1f); // back arrow position
        drawerArrow.setColor(R.color.ldrawer_color); // to set color
        //设置LDrawer填充内容，通过Adapter填充
        String[] names = new String[]{
                "TDS设备",
                "排行榜",
                "一周走势图",
                "蓝牙配对"
        };
        int[] imageIds = new int[]{
                R.drawable.icon_150
                ,R.drawable.icon_150
                ,R.drawable.icon_150
                ,R.drawable.icon_150
        };
        List<Map<String,Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++){
            Map<String,Object> listItem = new HashMap<String, Object>();
            listItem.put("decName",names[i]);
            listItem.put("header",imageIds[i]);
            listItems.add(listItem);
        }
        //创建一个simpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems,R.layout.simple_list_item
        ,new String[]{"decName","header"}
        ,new int[]{R.id.decName,R.id.header});
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.simple_list_item,android.R.id.text1, names);
        mDrawerList.setAdapter(simpleAdapter);
        //抽屉侧栏设置点击事件
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        mDrawerToggle.setAnimateEnabled(false);//设置停止或开启动画
                        drawerArrow.setProgress(1f);//设置普通菜单图标模式
                        openMainDrawer();
                        break;
                    case 1:
                        mDrawerToggle.setAnimateEnabled(false);
                        drawerArrow.setProgress(0f);//设置返回箭头模式
                        openBarChart();
                        break;
                    case 2:
                        mDrawerToggle.setAnimateEnabled(true);
                        mDrawerToggle.syncState();
                        openLineChart();
                        break;
                    case 3:
                        mDrawerToggle.setAnimateEnabled(true);
                        mDrawerToggle.syncState();
                        addDevices();
                        break;
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    //启动添加设备activity
    private void addDevices() {
        Intent intent = new Intent(LDrawerActivity.this,AddDevicesActivity.class);
        startActivity(intent);
    }

    //启动一周走势图
    private void openLineChart() {
        Intent intent = new Intent(LDrawerActivity.this,LineChartActivity.class);
        startActivity(intent);
    }
    //启动水质排行柱状图
    private void openBarChart() {
        Intent intent = new Intent(LDrawerActivity.this,BarChartActivity.class);
        startActivity(intent);
    }

    //启动水质排行柱状图
    private void openMainDrawer() {
        Intent intent = new Intent(LDrawerActivity.this, MainDrawerActivity.class);
        startActivity(intent);
    }
}
