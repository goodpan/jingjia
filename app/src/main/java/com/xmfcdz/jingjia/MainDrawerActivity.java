package com.xmfcdz.jingjia;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;


import com.xmfcdz.jingjia.R;
import com.xmfcdz.jingjia.mockedActivity.Settings;
import com.xmfcdz.jingjia.mockedFragments.FragmentButton;
import com.xmfcdz.jingjia.mockedFragments.FragmentIndex;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

public class MainDrawerActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        //设置后退按钮
        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        //设置抽屉显示
//        this.disableLearningPattern();
        enableToolbarElevation();

        //添加标题
        MaterialAccount account = new MaterialAccount(this.getResources(),"逝去的岁月","suwen0603@163.com", R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);

//        MaterialAccount account2 = new MaterialAccount(this.getResources(),"Hatsune Miky","hatsune.miku@example.com",R.drawable.photo2,R.drawable.mat2);
//        this.addAccount(account2);
//
//        MaterialAccount account3 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
//        this.addAccount(account3);

//        // 创建列表项
//        this.addSection(newSection("TDS笔", new FragmentIndex()));
//        this.addSection(newSection("ORP笔",new FragmentIndex()));
        this.addSection(newSection("PH笔",R.drawable.ic_mic_white_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("TDS2笔",R.drawable.ic_hotel_grey600_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#03a9f4")));

        // create bottom section
        this.addBottomSection(newSection("添加设备",R.drawable.ic_settings_black_24dp,new Intent(this,AddDevicesActivity.class)));
    }

}
