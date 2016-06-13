package com.xmfcdz.jingjia.bluetooth.data;

import java.text.DecimalFormat;

import com.xmfcdz.jingjia.ConstantsEwins;

public class Datagram {
	private float speed1;
	private float speed2;
	private float speed3;
	private float speed4;
	private float speed5;
	private float speed6;

	private int direction1;
	private int direction2;

	private float temperature;
	private int humidity;
	private float pressure;
	DecimalFormat df = new DecimalFormat(".00");

	public float getSpeed1() {
		return speed1;
	}

	public void setSpeed1(float speed1) {
		this.speed1 = speed1 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed1 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed1);
	}

	public float getSpeed2() {
		return speed2;
	}

	public void setSpeed2(float speed2) {
		this.speed2 = speed2 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed2 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed2);
	}

	public float getSpeed3() {
		return speed3;
	}

	public void setSpeed3(float speed3) {
		this.speed3 = speed3 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed3 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed3);
	}

	public float getSpeed4() {
		return speed4;
	}

	public void setSpeed4(float speed4) {
		this.speed4 = speed4 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed4 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed4);
	}

	public float getSpeed5() {
		return speed5;
	}

	public void setSpeed5(float speed5) {
		this.speed5 = speed5 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed5 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed5);
	}

	public float getSpeed6() {
		return speed6;
	}

	public void setSpeed6(float speed6) {
		this.speed6 = speed6 > ConstantsEwins.MAX_SPEED_VALUE ? ConstantsEwins.MAX_SPEED_VALUE
				: (speed6 < ConstantsEwins.MIN_SPEED_VALUE ? ConstantsEwins.MIN_SPEED_VALUE : speed6);
	}

	public int getDirection1() {
		return direction1;
	}

	public void setDirection1(int direction1) {
		this.direction1 = direction1;
	}

	public int getDirection2() {
		return direction2;
	}

	public void setDirection2(int direction2) {
		this.direction2 = direction2;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature > ConstantsEwins.MAX_TEMPERATURE_VALUE ? ConstantsEwins.MAX_TEMPERATURE_VALUE
				: (temperature < ConstantsEwins.MIN_TEMPERATURE_VALUE ? ConstantsEwins.MIN_TEMPERATURE_VALUE
						: temperature);
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity > 100 ? 100 : (humidity < 0 ? 0 : humidity);
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure > ConstantsEwins.MAX_PRESSURE_VALUE ? ConstantsEwins.MAX_PRESSURE_VALUE
				: (pressure < ConstantsEwins.MIN_PRESSURE_VALUE ? ConstantsEwins.MIN_PRESSURE_VALUE : pressure);
	}

	public void parseDatagram(String datagram) {
		// datagram = "01076954204231804B0A08072102C3046307F209";
		if (datagram == null || datagram == "") {
			System.out.println("kong");
			return;
		}
		setSpeed1(calcSpeed(datagram.substring(4, 6)));
		setSpeed2(calcSpeed(datagram.substring(6, 8)));
		setSpeed3(calcSpeed(datagram.substring(8, 10)));
		setSpeed4(calcSpeed(datagram.substring(10, 12)));
		setSpeed5(calcSpeed(datagram.substring(12, 14)));
		setSpeed6(calcSpeed(datagram.substring(14, 16)));

		setDirection1(calcDirection(datagram.substring(22, 24) + datagram.substring(20, 22)));
		setDirection2(calcDirection(datagram.substring(26, 28) + datagram.substring(24, 26)));

		setTemperature(calcTemperature(datagram.substring(30, 32) + datagram.substring(28, 30)));
		setHumidity(calcHumidity(datagram.substring(34, 36) + datagram.substring(32, 34)));
		setPressure(calcPressure(datagram.substring(38, 40) + datagram.substring(36, 38)));
	}

	private float calcSpeed(String pStrSpeedHex) {
		float speed = DataTypeConverter.hexStr2Int(pStrSpeedHex);
		return speed * 0.765f + 0.35f;
	}

	private int calcDirection(String pStrDirectionHex) {
		int direction = DataTypeConverter.hexStr2Int(pStrDirectionHex);
		return (int) (direction * 0.094230277);
	}

	private float calcTemperature(String pStrTempHex) {
		float temperature = DataTypeConverter.hexStr2Int(pStrTempHex);
		return (float) (temperature * 0.088695557 - 86.38);
	}

	private int calcHumidity(String pStrHumidityHex) {
		int humidity = DataTypeConverter.hexStr2Int(pStrHumidityHex);
		return (int) (humidity * 0.031933594);
	}

	private float calcPressure(String pStrPressureHex) {
		float pressure = DataTypeConverter.hexStr2Int(pStrPressureHex);
		return (float) (pressure * 0.03479165 + 10.55);
	}
}
