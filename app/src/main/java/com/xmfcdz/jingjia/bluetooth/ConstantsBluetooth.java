package com.xmfcdz.jingjia.bluetooth;

import java.util.UUID;

public class ConstantsBluetooth {
	public static final int REQUEST_CONNECT_DEVICE = 1;
	
	public static final String DEVICE_ADDRESS = "deviceaddress";
	public static final String CONNECTED_DEVICE_NAME = "connecteddevicename";

	public static final String SERVER_NAME = "BluetoothComm";
	public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;

	public static final int STATE_NONE = 0; // do nothing
	public static final int STATE_LISTEN = 1; // 监听连接
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
	public static final int STATE_CONNECTED = 3; // 已连接上远程设备

	public static final String CONNECTION_FAILED = "fail";
	public static final String CONNECTION_LOST = "lost";
	public static final String CONNECTION_CONNECTED = "connected";

	public static final String COMMAND_1 = "uLPCC_0000CD?";
	public static final byte[] COMMAND_ACK = new byte[] { (byte) 0x06 };
}
