package com.xmfcdz.jingjia;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmfcdz.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends FragmentActivity
        implements NewDataFragment.OnFragmentInteractionListener,
        AWeekTrendFragment.OnFragmentInteractionListener,
        SurroundingFragment.OnFragmentInteractionListener,
        CityRankingFragment.OnFragmentInteractionListener
{
    protected static final String TAG = "ResultsActivity";
    private Fragment[] fragments;
    public NewDataFragment newDataFragment;
    private AWeekTrendFragment aWeekTrendFragment;
    private SurroundingFragment surroundingFragment;
    private CityRankingFragment cityRankingFragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private ImageView iv_add;
    private ImageView iv_search;
    private TextView res_title;

    private ViewPager mViewPager;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动的偏移量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        initView();
        mAdapter = new FragmentAdapter(this.getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器

    }
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        newDataFragment = new NewDataFragment();
        aWeekTrendFragment = new AWeekTrendFragment();
        surroundingFragment = new SurroundingFragment();
        cityRankingFragment = new CityRankingFragment();
        fragments = new Fragment[] { newDataFragment, aWeekTrendFragment,
                surroundingFragment, cityRankingFragment };
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_weixin);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_contact_list);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_find);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);
        imagebuttons[0].setSelected(true);

        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_weixin);
        textviews[1] = (TextView) findViewById(R.id.tv_contact_list);
        textviews[2] = (TextView) findViewById(R.id.tv_find);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);
        //获取标题
        res_title = (TextView)findViewById(R.id.res_title);
        mFragmentList.add(newDataFragment);
        mFragmentList.add(aWeekTrendFragment);
        mFragmentList.add(surroundingFragment);
        mFragmentList.add(cityRankingFragment);
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_weixin:
                res_title.setText("最新数据");
                index = 0;
                break;
            case R.id.re_contact_list:
                res_title.setText("一周走势");
                index = 1;
                break;
            case R.id.re_find:
                res_title.setText("周边排行");
                index = 2;
                break;
            case R.id.re_profile:
                res_title.setText("城市排行");
                index = 3;
                break;
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
        mViewPager.setCurrentItem(currentTabIndex);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //退出
    public void back(View view){
        finish();
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            swithTab(position);
        }
    }

    private void swithTab(int position){

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
        mViewPager.setCurrentItem(currentTabIndex);
    }
}
