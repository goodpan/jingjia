package com.xmfcdz.jingjia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xmfcdz.Constant;
import com.xmfcdz.others.LocalUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyinfoActivity extends Activity {
    private RelativeLayout re_avatar;
    private RelativeLayout re_name;
    private RelativeLayout re_fxid;
    private RelativeLayout re_sex;
    private RelativeLayout re_region;

    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_fxid;
    private TextView tv_sex;
    private TextView tv_sign;

    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_FXID = 4;// 结果
    private static final int UPDATE_NICK = 5;// 结果
    String hxid;
    String fxid;
    String sex;
    String sign;
    String nick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        initView();
    }

    private void initView() {
        sex = LocalUserInfo.getInstance(MyinfoActivity.this).getUserInfo(
                "sex");
        re_avatar = (RelativeLayout) this.findViewById(R.id.re_avatar);
        re_avatar.setOnClickListener(new MyListener());

        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_name.setOnClickListener(new MyListener());

        re_sex = (RelativeLayout) this.findViewById(R.id.re_sex);
        re_sex.setOnClickListener(new MyListener());

        // 头像
        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_sex = (TextView) this.findViewById(R.id.tv_sex);
        tv_sign = (TextView) this.findViewById(R.id.tv_sign);
        tv_name.setText(nick);
        if (sex.equals("1")) {
            tv_sex.setText("男");

        } else if (sex.equals("2")) {
            tv_sex.setText("女");

        } else {
            tv_sex.setText("");
        }
    }
    class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_avatar:
                    showPhotoDialog();
                    break;
                case R.id.re_name:
                    startActivityForResult(new Intent(MyinfoActivity.this,
                            UpdateNickActivity.class), UPDATE_NICK);
                    break;
                case R.id.re_sex:
                    showSexDialog();
                    break;
            }
        }

    }
    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍照");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {

                imageName = getNowTime() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File("/sdcard/fanxin/", imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("相册");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getNowTime();
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dlg.cancel();
            }
        });

    }

    private void showSexDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        LinearLayout ll_title = (LinearLayout) window
                .findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("性别");
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("男");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!sex.equals("1")) {
                    tv_sex.setText("男");
//                    updateSex("1");
                }

                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("女");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!sex.equals("2")) {
                    tv_sex.setText("女");
//                    updateSex("2");
                }

                dlg.cancel();
            }
        });

    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:

                    startPhotoZoom(
                            Uri.fromFile(new File("/sdcard/fanxin/", imageName)),
                            480);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        startPhotoZoom(data.getData(), 480);
                    break;

                case PHOTO_REQUEST_CUT:
                    // BitmapFactory.Options options = new BitmapFactory.Options();
                    //
                    // /**
                    // * 最关键在此，把options.inJustDecodeBounds = true;
                    // * 这里再decodeFile()，返回的bitmap为空
                    // * ，但此时调用options.outHeight时，已经包含了图片的高了
                    // */
                    // options.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/fanxin/"
                            + imageName);
                    iv_avatar.setImageBitmap(bitmap);
                    updateAvatarInServer(imageName);
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @SuppressLint("SdCardPath")
    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File("/sdcard/fanxin/", imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @SuppressLint("SdCardPath")
    private void updateAvatarInServer(final String image) {
        Map<String, String> map = new HashMap<String, String>();
        if ((new File("/sdcard/fanxin/" + image)).exists()) {
            map.put("file", "/sdcard/fanxin/" + image);
            map.put("image", image);
        } else {
            return;
        }
        map.put("hxid", hxid);

//        LoadDataFromServer task = new LoadDataFromServer(
//                MyUserInfoActivity.this, Constant.URL_UPDATE_Avatar, map);
//
//        task.getData(new DataCallBack() {
//
//            @SuppressLint("ShowToast")
//            @Override
//            public void onDataCallBack(JSONObject data) {
//                try {
//                    int code = data.getInteger("code");
//                    if (code == 1) {
//                        LocalUserInfo.getInstance(MyUserInfoActivity.this)
//                                .setUserInfo("avatar", image);
//                    } else if (code == 2) {
//
//                        Toast.makeText(MyUserInfoActivity.this, "更新失败...",
//                                Toast.LENGTH_SHORT).show();
//                    } else if (code == 3) {
//
//                        Toast.makeText(MyUserInfoActivity.this, "图片上传失败...",
//                                Toast.LENGTH_SHORT).show();
//
//                    } else {
//
//                        Toast.makeText(MyUserInfoActivity.this, "服务器繁忙请重试...",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//
//                    Toast.makeText(MyUserInfoActivity.this, "数据解析错误...",
//                            Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//
//            }
//
//        });

    }


    //退出
    public void back(View view){
        finish();
    }
}
