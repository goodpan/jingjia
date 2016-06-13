package com.xmfcdz.jingjia;

public class ConstantsEwins {

	public static final int REQUEST_ADDDELETE_SENSORS = 2;

	public static final float MAX_SPEED_VALUE = 100;
	public static final float MIN_SPEED_VALUE = 0;
	public static final int MIN_SPEED_DURATION = 500;
	public static final int SPEED_DURATION_STEP = 500;

	public static final float MAX_TEMPERATURE_VALUE = 53;
	public static final float MIN_TEMPERATURE_VALUE = -40;
	public static final float MIN_TEMPERATURE_Y = 0.02f;
	public static final float TEMPERATURE_STEP = 0.01185f;

	public static final float MIN_HUMIDITY_Y = 0.0f;
	public static final float HUMIDITY_STEP = 0.01118f;
	public static final float MAX_PRESSURE_VALUE = 120;
	public static final float MIN_PRESSURE_VALUE = 10;
	public static final float PRESSURE_STEP = 2.38f;
	
	public static final int DATAGRAM_INTERVAL = 500;
	// 报文的字节数
	public static final int DATAGRAM_BYTES_LENGTH = 20;
	// 需要使用的数据长度，例如将字节变成16进制字符串的长度，0x80变成80
	public static final int DATA_LENGTH = 40;

}
