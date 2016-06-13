package com.xmfcdz.jingjia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    //退出
    public void back(View view){
        finish();
    }
}
