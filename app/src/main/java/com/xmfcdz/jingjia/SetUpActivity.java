package com.xmfcdz.jingjia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SetUpActivity extends Activity implements View.OnClickListener {
    /**
     * 设置新消息通知布局
     */
    private RelativeLayout rl_switch_notification;
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 设置震动布局
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * 设置扬声器布局
     */
    private RelativeLayout rl_switch_speaker;

    /**
     * 打开新消息通知imageView
     */
    private ImageView iv_switch_open_notification;
    /**
     * 关闭新消息通知imageview
     */
    private ImageView iv_switch_close_notification;
    /**
     * 打开声音提示imageview
     */
    private ImageView iv_switch_open_sound;
    /**
     * 关闭声音提示imageview
     */
    private ImageView iv_switch_close_sound;
    /**
     * 打开消息震动提示
     */
    private ImageView iv_switch_open_vibrate;
    /**
     * 关闭消息震动提示
     */
    private ImageView iv_switch_close_vibrate;
    /**
     * 打开扬声器播放语音
     */
    private ImageView iv_switch_open_speaker;
    /**
     * 关闭扬声器播放语音
     */
    private ImageView iv_switch_close_speaker;
    /**
     * 退出按钮
     */
    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        rl_switch_notification = (RelativeLayout) this.findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) this.findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) this.findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout) this.findViewById(R.id.rl_switch_speaker);

        iv_switch_open_notification = (ImageView) this.findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) this.findViewById(R.id.iv_switch_close_notification);
        iv_switch_open_sound = (ImageView) this.findViewById(R.id.iv_switch_open_sound);
        iv_switch_close_sound = (ImageView) this.findViewById(R.id.iv_switch_close_sound);
        iv_switch_open_vibrate = (ImageView) this.findViewById(R.id.iv_switch_open_vibrate);
        iv_switch_close_vibrate = (ImageView) this.findViewById(R.id.iv_switch_close_vibrate);
        iv_switch_open_speaker = (ImageView) this.findViewById(R.id.iv_switch_open_speaker);
        iv_switch_close_speaker = (ImageView) this.findViewById(R.id.iv_switch_close_speaker);
        logoutBtn = (Button) this.findViewById(R.id.btn_logout);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_notification:
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);

                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.rl_switch_sound:
                if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
                    iv_switch_open_sound.setVisibility(View.INVISIBLE);
                    iv_switch_close_sound.setVisibility(View.VISIBLE);

                } else {
                    iv_switch_open_sound.setVisibility(View.VISIBLE);
                    iv_switch_close_sound.setVisibility(View.INVISIBLE);

                }
                break;
            case R.id.rl_switch_vibrate:
                if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.VISIBLE);

                } else {
                    iv_switch_open_vibrate.setVisibility(View.VISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.INVISIBLE);

                }
                break;
            case R.id.rl_switch_speaker:
                if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
                    iv_switch_open_speaker.setVisibility(View.INVISIBLE);
                    iv_switch_close_speaker.setVisibility(View.VISIBLE);

                } else {
                    iv_switch_open_speaker.setVisibility(View.VISIBLE);
                    iv_switch_close_speaker.setVisibility(View.INVISIBLE);

                }
                break;
            case R.id.btn_logout: //退出登陆
                logout();
                break;

            default:
                break;
        }
    }

    private void logout() {
        final ProgressDialog pd = new ProgressDialog(SetUpActivity.this);
        pd.setMessage("正在退出登陆..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

    }
    //退出
    public void back(View view){
        finish();
    }
}
