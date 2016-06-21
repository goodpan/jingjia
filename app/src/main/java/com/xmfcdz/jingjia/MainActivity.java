package com.xmfcdz.jingjia;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.andexert.library.RippleView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.xmfcdz.friends.FriendsListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private static final int PROFILE_SETTING = 1;
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer drawerLeft = null;
    private Drawer drawerRight = null;
    private IProfile profile;
    private IProfile profile2;
    private IProfile profile3;
    private IProfile profile4;
    private IProfile profile5;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private Button btToDetail;
    private Button recBtn;
    private TextView temperatureView;
    private TextView humidityView;
    private TextView windPowerView;
    private TextView windDirectionView;
    private TextView locationAddress;
    private RippleView rippleView;
    private ListView drawerRightListView;
    private ArrayAdapter arrayBTAdapter;
    private  ColorArcProgressBar cpb;
    private String strAddress;
    private View customView;
    final int REQUEST_CODE = 0;
    //蓝牙
    private final static int REQUEST_ENABLE_BT = 1;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明天气查询对象
    public WeatherSearch weather = null;
    public WeatherSearchQuery query = null;
    private String cityStr = null;

    //右侧抽屉列表数据
    protected View mHeaderView;
    private String[] proTitle;
    private String[] descs;
    private int[] proImagesId;
    private SimpleAdapter simpleAdapter;
    private ListView list;
    private  List<Map<String,Object>> listItems;
    private Map<String,Object> listItem;
    private View drawRightHeader;

    //蓝牙相关
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final static String TAG = MainActivity.class.getSimpleName();
    private boolean mConnected = false;
    private String mDeviceName;
    private String mDeviceAddress;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        //与BluetoothLeService连接后触发
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("isConnect","onServiceConnected");
            //service由BluetoothLeService的onBind返回
            //获取BluetoothLeService对象
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            //连接蓝牙设备,将设备地址传递给BluetoothLeService进行连接
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        //与BluetoothLeService断开连接后触发
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    //接收的BluetoothLeService蓝牙广播操作
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            //已连接
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            }
            //断开连接
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            }
            //发现设备
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mConnected = true;
                //发现连接设备的service
                readValue(mBluetoothLeService.getSupportedGattServices());
            }
            //读数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //获取BluetoothLeService传过来的数据
                try {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
//                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                        String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                        Log.e("mainData1", data);
                        //initArcProgressBar(9999,data,"优");
//                    Log.e("mainData1",data);
                        //显示到界面
                        if (data != null&&!data.equals("")) {
                            Log.e("mainData2", data);
                            //cpb.setUnit(data);
                            //初始化圆形进度条
                            int tds = Integer.parseInt(data);
                            cpb.setCurrentValues(tds);
                            //initArcProgressBar(9999,Integer.parseInt(data),"优");
                        }
                    }
                }catch (Exception e){
                    System.err.println(e);
                }
            }
        }
    };
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //进度条
                cpb = (ColorArcProgressBar) findViewById(R.id.bar);
                cpb.setDetail(getResources().getString(resourceId));
            }
        });
    }
    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //界面恢复初始值
        initArcProgressBar(9999,0,"...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置标题栏
        setToolBar();
        //初始化定位信息
        initLocationInfo();
        //启动定位
        mLocationClient.startLocation();
        initDrawerHeader();
        // Create the AccountHeader创建抽屉头部
        buildHeader(false, savedInstanceState);
        //创建抽屉左侧
        creatDrawerLeft(savedInstanceState);
        //创建抽屉右侧
        createDrawerRight(savedInstanceState);
        initDrawerRightListView();
        //设置顶部返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawerLeft.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        //set the back arrow in the toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        //初始化圆形进度条
        initArcProgressBar(9999,0,"...");
        //设置地址定位信息控件
        locationAddress = (TextView)findViewById(R.id.locationAddress);
        //获取温度控件
        temperatureView = (TextView)findViewById(R.id.temperature);
        //获取湿度控件
        humidityView = (TextView)findViewById(R.id.humidity);
        //获取风力和风向控件
        windPowerView = (TextView)findViewById(R.id.windPower);
        //获取天气控件
        windDirectionView = (TextView)findViewById(R.id.windDirection);

        rippleView = (RippleView) findViewById(R.id.rippleView);
        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: onRippleViewClick
                Log.e("Sample", "Click Rect !");
            }
        });
        //检测报告
        btToDetail = (Button)findViewById(R.id.btToDetail);
        btToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailedActivity();
            }
        });
        recBtn = (Button)findViewById(R.id.recBtn);
        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesFragment();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawerLeft.saveInstanceState(outState);
        //add the values which need to be saved from the apppended drawer to the bundle
        outState = drawerRight.saveInstanceState(outState);
        super.onSaveInstanceState(outState);

    }

   @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawerLeft != null && drawerLeft.isDrawerOpen()) {
            drawerLeft.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }
    //创建抽屉左侧栏
    private void creatDrawerLeft(Bundle savedInstanceState){
        drawerLeft = new DrawerBuilder()
                .withActivity(this)//设置应用的activty
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
//                .withHeader(R.layout.header)//设置抽屉的头部
                .withSavedInstance(savedInstanceState)
                .withSliderBackgroundColor(Color.parseColor("#FFFFFF"))
                .addDrawerItems(//设置抽屉列表项
                        new SecondaryDrawerItem().withName("我的设备").withIcon(FontAwesome.Icon.faw_gavel).withIconColor(Color.parseColor("#333333")),
                        new SecondaryDrawerItem().withName("我的订单").withIcon(FontAwesome.Icon.faw_columns).withIconColor(Color.parseColor("#333333")),
                        new SecondaryDrawerItem().withName("我的积分").withIcon(FontAwesome.Icon.faw_star).withIconColor(Color.parseColor("#333333")),
                        new SecondaryDrawerItem().withName("我的好友").withIcon(FontAwesome.Icon.faw_users).withIconColor(Color.parseColor("#333333")),
                        new SecondaryDrawerItem().withName("检测报告").withIcon(FontAwesome.Icon.faw_list).withIconColor(Color.parseColor("#333333")),
                        new SecondaryDrawerItem().withName("设置").withIcon(FontAwesome.Icon.faw_cog).withIconColor(Color.parseColor("#333333"))
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        Toast.makeText(MainActivity.this, "onDrawerOpened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Toast.makeText(MainActivity.this, "onDrawerClosed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem
                        Log.e("click", Integer.toString(position));
                        if (drawerItem != null) {
                            if (drawerItem instanceof Nameable) {
                                Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                            }

                            if (drawerItem instanceof Badgeable) {
                                Badgeable badgeable = (Badgeable) drawerItem;
                                if (badgeable.getBadge() != null) {
                                    //note don't do this if your badge contains a "+"
                                    //only use toString() if you set the test as String
                                    int badge = Integer.valueOf(badgeable.getBadge().toString());
                                    if (badge > 0) {
                                        badgeable.withBadge(String.valueOf(badge - 1));
                                        drawerLeft.updateItem(drawerItem);
                                    }
                                }
                            }
                            switch (position) {
                                case 3:
                                    openMarkActivity();
                                    break;
                                case 4:
                                    openFriendsListActivity();
                                    break;
                                case 5:
                                    openDetailedActivity();
                                    break;
                                case 6:
                                    openSetUpActivity();
                                    break;
                                default:
                                    break;
                            }
                        }

                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(MainActivity.this, ((SecondaryDrawerItem) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (drawerView == drawerLeft.getSlider()) {
                            Log.e("sample", "left opened");
                        } else if (drawerView == drawerRight.getSlider()) {
                            Log.e("sample", "right opened");
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if (drawerView == drawerLeft.getSlider()) {
                            Log.e("sample", "left closed");
                        } else if (drawerView == drawerRight.getSlider()) {
                            Log.e("sample", "right closed");
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

    }


    //创建抽屉右侧栏
    private void createDrawerRight(Bundle savedInstanceState){
        //now we add the second drawer on the other site.
        //use the .append method to add this drawer to the first one
        drawerRight = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.header)
                .withFullscreen(true)
                .withSliderBackgroundColor(Color.parseColor("#FFFFFF"))
                        .withSavedInstance(savedInstanceState)
                        .addDrawerItems(
                                new SecondaryDrawerItem().withName("产品1"),
                                new SecondaryDrawerItem().withName("产品2"),
                                new SecondaryDrawerItem().withName("产品3"),
                                new SecondaryDrawerItem().withName("产品4"),
                                new SecondaryDrawerItem().withName("产品5"),
                                new SecondaryDrawerItem().withName("产品6")

                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (drawerItem instanceof Nameable) {
                                    Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                                }
                                return false;
                            }
                        })
                        .withDrawerGravity(Gravity.END)
                        .append(drawerLeft);
    }
    //初始化圆形进度条
    private void initArcProgressBar(int maxVal,int currentVal,String level){
        //进度条
        cpb = (ColorArcProgressBar) findViewById(R.id.bar);
//        cpb.setDiameter(300);
//        cpb.setBackgroundResource(R.drawable.shape_progress);
        cpb.setDrawingCacheBackgroundColor(Color.parseColor("#47c0fc"));
        //设置当前值
        cpb.setMaxValues(maxVal);
        cpb.setCurrentValues(currentVal);
        cpb.setHintSize(20);
        cpb.setProgressWidth(20);
        cpb.setBgArcWidth(100);
//        cpb.setBgArcWidth();
        //设置优、差、中
        cpb.setUnit(level);
    }
    //初始化抽屉头部信息
    private void initDrawerHeader(){
        //first create the main drawer (this one
        // will be used to add the second drawer on the other side)
        // Create a few sample profile
        profile = new ProfileDrawerItem().withNameShown(true).withName("逝去的岁月").withEmail("batman@163.com").withIcon(getResources().getDrawable(R.drawable.profile5));

    }

    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withSelectionListEnabledForSingleProfile(false)
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(compact)
                .withHeightDp(150)
                .addProfiles(
                        profile
                )

                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {

                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withEmail("batman@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile5));

                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        openMyinfo();
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        //点击头部打开个人信息
        headerResult.getHeaderBackgroundView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyinfo();
            }
        });
    }
    //设置标题栏右侧下拉菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_1:
                Login();
                return true;
            case R.id.menu_2:
                Register();
                return true;
            case R.id.menu_scan:
                //show the hamburger icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                drawerLeft.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                return true;
            case R.id.menu_stop:
                closeBluetooth();
                return true;
            case R.id.menu_5:
                openImagesFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //积分
    private void openMarkActivity(){
        Intent intent = new Intent(MainActivity.this,MarkActivity.class);
        startActivity(intent);
    }
    //推荐方案
    private void openImagesFragment(){
        Intent intent = new Intent(MainActivity.this,TestActivity.class);
        startActivity(intent);
    }
    //检测报告
    private void openDetailedActivity(){
        Intent intent = new Intent(MainActivity.this,ResultsActivity.class);
        startActivity(intent);
    }

    //登录
    private void Login(){
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    //注册
    private void Register(){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    //个人信息
    private void openMyinfo(){
        Intent intent = new Intent(MainActivity.this,MyinfoActivity.class);
        startActivity(intent);
    }

    //蓝牙操作
    private void toControlDevice(){
        Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //启动一周走势图
    public void openLineChart(View view) {
        Intent intent = new Intent(MainActivity.this,LineChartActivity.class);
        startActivity(intent);
    }
    //滤芯管理
    public void openLvXinActivity(View view) {
        Intent intent = new Intent(MainActivity.this,LvXinActivity.class);
        startActivity(intent);
    }

    //启动水质排行柱状图
    public void openBarChart(View view) {
        Intent intent = new Intent(MainActivity.this,BarChartActivity.class);
        startActivity(intent);
    }

    //启动百度地图
    public void openBaiduMap(View view) {
        Intent intent = new Intent(MainActivity.this,BaiduMapActivity.class);
        startActivity(intent);
    }

    //打开右侧抽屉
    public void  openDrawerRight(View view){
        if(drawerRight!=null){
            drawerRight.openDrawer();
        }
    }

    //打开好友列表
    public void openFriendsListActivity() {
        Intent intent = new Intent(MainActivity.this,FriendsListActivity.class);
        startActivity(intent);
    }
    //打开设置页
    public void openSetUpActivity() {
        Intent intent = new Intent(MainActivity.this,SetUpActivity.class);
        startActivity(intent);
    }
    //启动蓝牙测试
    public void StartMeasuring(View view){
        toControlDevice();
    }

    //关闭蓝牙
    private void closeBluetooth(){
        if(mBluetoothAdapter == null){
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(MainActivity.this, "正在关闭蓝牙", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
        Log.e("onResume","onresume...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //处理蓝牙
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //获取蓝牙地址，根据键：ConstantsBluetooth.DEVICE_ADDRESS
                    mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                    Log.e("address",mDeviceAddress);
                    //绑定蓝牙服务
                    //绑定BluetoothLeService
                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.e("Amap", aMapLocation.getAddress().toString());
                    strAddress = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet()+aMapLocation.getStreetNum();
                    locationAddress.setText(strAddress);
                    cityStr = aMapLocation.getCity();//获取所在城市
                    searchWeather(cityStr);
                    //定位成功回调信息，设置相关消息
//                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                    aMapLocation.getLatitude();//获取纬度
//                    aMapLocation.getLongitude();//获取经度
//                    aMapLocation.getAccuracy();//获取精度信息
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = new Date(aMapLocation.getTime());
//                    df.format(date);//定位时间
                    //aMapLocation.getAddress()地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    aMapLocation.getCountry();//国家信息
//                    aMapLocation.getProvince();//省信息
//                    aMapLocation.getCity();//城市信息
//                    aMapLocation.getDistrict();//城区信息
//                    aMapLocation.getStreet();//街道信息
//                    aMapLocation.getStreetNum();//街道门牌号信息
//                    aMapLocation.getCityCode();//城市编码
//                    aMapLocation.getAdCode();//地区编码
//                    aMapLocation.getAoiName();//获取当前定位点的AOI信息
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    //监听天气
    public  WeatherSearch.OnWeatherSearchListener weatherSearchListener = new  WeatherSearch.OnWeatherSearchListener(){
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int rCode) {
            Log.e("rCode", Integer.toString(rCode));
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive liveWeather = localWeatherLiveResult.getLiveResult();
                temperatureView.setText(liveWeather.getTemperature()+"℃"+"\n"+"温度");
                humidityView.setText(liveWeather.getHumidity()+"%"+"\n"+"湿度");
                windPowerView.setText(liveWeather.getWindPower() + "级"+"\n"+liveWeather.getWindDirection()+"风");
                windDirectionView.setText(liveWeather.getWeather());
            }else {
                Toast.makeText(MainActivity.this, "查询不到天气数据", Toast.LENGTH_SHORT).show();
            }
            //rCode总是等于1000,但查询得到结果????????
            if (rCode == 0) {
                Log.e("rCode2", Integer.toString(rCode));
            } else {
                Log.e("rCode3", Integer.toString(rCode));
//                Toast.makeText(MainActivity.this, "数据正在更新", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

        }
    };
    //设置标题栏toolbar
    private void setToolBar(){
        // 标题栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //将标题置为空
        getSupportActionBar().setTitle("");
        //去掉阴影
        getSupportActionBar().setElevation(0);
    }
    //初始化定位信息
    private void initLocationInfo(){
        //初始化定位
        mLocationClient  = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(200000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }


    //搜索天气信息，需要在定位城市后根据城市获取
    private void searchWeather(String city){
        //初始化天气
        weather = new WeatherSearch(getApplicationContext());
        //定义天气信息搜索的参数
        //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        query = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        weather=new WeatherSearch(this);
        weather.setOnWeatherSearchListener(weatherSearchListener);
        weather.setQuery(query);
        weather.searchWeatherAsyn(); //异步搜索
    }

    //初始化右侧抽屉ListView
    private void initDrawerRightListView(){
        drawRightHeader = drawerRight.getHeader();
        proTitle = new String[]{
                "TDS水质测试笔",
                "PH水质测试笔",
                "ORP水质测试笔",
                "RO能量水机"
        };
        descs = new String[]{
                "水质测试笔测试溶解性固体含量",
                "水质测试笔测试PH值",
                "水质测试笔测试ORP值",
                "净化水质净化水质"
        };
        proImagesId = new int[]{
          R.drawable.icon_150,
          R.drawable.icon_150,
          R.drawable.icon_150,
          R.drawable.icon_150
        };
        //创建一个list集合，list集合的元素是Map
        listItems = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < proTitle.length; i++){
            listItem = new HashMap<String,Object>();
            listItem.put("header",proImagesId[i]);
            listItem.put("title",proTitle[i]);
            listItem.put("desc",descs[i]);
            listItems.add(listItem);
        }
        //创建一个一个SimpleAdapter
        simpleAdapter = new SimpleAdapter(this,listItems,R.layout.simple_item,
                new  String[]{"header","title","desc"},
                new int[]{R.id.header,R.id.title,R.id.desc});
       list = (ListView)drawRightHeader.findViewById(R.id.proList);
        //为ListView设置Adapter
        list.setAdapter(simpleAdapter);
    }
    //读数据
    protected void readValue(List<BluetoothGattService> gattServices){
        if (gattServices == null) return;
        String uuid = null;
        Log.e("read_start", "start reading...");
        for (BluetoothGattService gattService : gattServices) {
            Log.e("getservices","start getservices");
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                Log.e("uuid","get uuid success:"+uuid);
                if(uuid.equals(SampleGattAttributes.HEART_RATE_MEASUREMENT)){
                    Log.e("equal","equal success");
                    final int charaProp = gattCharacteristic.getProperties();
                    Log.e("charaProp",String.valueOf(charaProp));
                    //读操作
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {
                            //通知
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(gattCharacteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = gattCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                }
            }
        }
    }
    //接收蓝牙广播注册
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
