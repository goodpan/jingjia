package com.xmfcdz.jingjia.bluetooth.data;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class DataTypeConverter {

	public static String double2Str(double d) {
		return String.format("%.1f", d);
	}

	public static double str2Double(String str) {
		return Double.parseDouble(str);
	}

	public static String getMacAddress(String pStr) {
		return pStr.substring(pStr.length() - 17);
	}

	public static double hexStr2Double(String pStr) {
		int i = Integer.parseInt(pStr, 16);
		double d;
		if (pStr.startsWith("F")) {
			d = (double) (i - 65536) / 100;
		} else {
			d = (double) i / 100;
		}
		d = ((double) Math.round(d * 10)) / 10;
		return d;
	}

	public static float hexStr2Float(String pStr) {
		return (float) (hexStr2Double(pStr));
	}

	public static int hexStr2Int(String pStr) {
		return Integer.parseInt(pStr, 16);
	}
}
