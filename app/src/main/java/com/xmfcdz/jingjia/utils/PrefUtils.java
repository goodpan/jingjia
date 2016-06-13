package com.xmfcdz.jingjia.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * SharePreference封装
 * 
 * @author Kevin
 * 
 */
public class PrefUtils {

	public static final String PREF_NAME = "config";

	public static boolean getBoolean(Context ctx, String key,
			boolean defaultValue) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	/* byte转十六进制字符串
	* */
	public static String bytes2HexString(byte[] bytes) {
		String ret = "";
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(aByte & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase(Locale.CHINA);
		}
		return ret;
	}

	/** * 将16进制的字符串转换为字节数组 *
	 * @param message * @return 字节数组 */
	public static byte[] getHexBytes(String message) {
		int len = message.length() / 2;
		char[] chars = message.toCharArray();
		String[] hexStr = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i += 2, j++) {
			hexStr[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
		}
		return bytes;
	}
}
