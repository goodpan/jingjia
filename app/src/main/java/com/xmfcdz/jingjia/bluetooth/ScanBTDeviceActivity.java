package com.xmfcdz.jingjia.bluetooth;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xmfcdz.jingjia.R;
import com.xmfcdz.jingjia.bluetooth.data.DataTypeConverter;

public class ScanBTDeviceActivity extends Activity {

	private Button btnScan = null;
	private ListView lvNewDevices, lvPairedDevices;
	private ArrayAdapter<String> adapterNewDevices, adapterPairedDevice;

	private BluetoothAdapter btAdapter;
	private Set<BluetoothDevice> pairedDevices;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置标题显示进度
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		//获取视图按钮
		findAllViews();
		//获取本地蓝牙适配器
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			finish();
			return;
		}
		//获取已经匹配的蓝牙设备
		pairedDevices = btAdapter.getBondedDevices();
		Log.e("555",Integer.toString(pairedDevices.size() ));
		if (pairedDevices.size() > 0) {//有匹配过的设备
			for (BluetoothDevice device : pairedDevices) {
				adapterPairedDevice.add(device.getName() + "\n" + device.getAddress());
			}
			lvPairedDevices.setAdapter(adapterPairedDevice);
			lvPairedDevices.setOnItemClickListener(DeviceListClickListener);
		}

		// 注册BroadcastReceiver，用于接收蓝牙广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}

	private void findAllViews() {
		//新设备按钮
		btnScan = (Button) findViewById(R.id.btnScan);
		//Log.e("5555", Integer.toString(pairedDevices.size()));
		//新设备列表
		lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
		//设置新设备列表适配器
		adapterNewDevices = new ArrayAdapter<String>(ScanBTDeviceActivity.this, android.R.layout.simple_list_item_1);
		//为列表设置适配器
		lvNewDevices.setAdapter(adapterNewDevices);
		//设置列表监听事件
		lvNewDevices.setOnItemClickListener(DeviceListClickListener);
		//已匹配设备，同上
		lvPairedDevices = (ListView) findViewById(R.id.lvPairedDevices);
		adapterPairedDevice = new ArrayAdapter<String>(ScanBTDeviceActivity.this, android.R.layout.simple_list_item_1);
		lvPairedDevices.setAdapter(adapterPairedDevice);
		lvPairedDevices.setOnItemClickListener(DeviceListClickListener);
		//搜索按钮启动搜索蓝牙事件
		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//搜索设备
				discoverDevices();
			}
		});
	}
	//搜索设备
	private void discoverDevices() {
		btnScan.setEnabled(false);
		setTitle(R.string.scanning);
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
		//开始搜索
		btAdapter.startDiscovery();
	}

	private OnItemClickListener DeviceListClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			btAdapter.cancelDiscovery();
			String address = DataTypeConverter.getMacAddress(((TextView) arg1).getText().toString());
			Intent intent = new Intent();
			//存储地址，键：ConstantsBluetooth.DEVICE_ADDRESS，值address
			intent.putExtra(ConstantsBluetooth.DEVICE_ADDRESS, address);
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}
		unregisterReceiver(mReceiver);
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 发现设备
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 从Intent中获取设备对象
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 将设备名称和地址放入array adapter，以便在ListView中显示
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
					adapterNewDevices.add(device.getName() + "\n" + device.getAddress());
//					lvNewDevices.setAdapter(adapterNewDevices);
//					lvNewDevices.setOnItemClickListener(DeviceListClickListener);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				btnScan.setEnabled(true);
				setTitle(R.string.select_device);
			}
		}
	};
}
