package com.xmfcdz.jingjia.bluetooth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class BluetoothCommService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String NAME = "BluetoothComm";
	// SPP协议UUID
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// Member fields
	private final BluetoothAdapter mAdapter;
	private Handler mHandler, receiveHandler;
	private int mState;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	public BluetoothCommService(Context context, Handler handler, Handler p_receiveHandler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		receiveHandler = p_receiveHandler;
		mState = ConstantsBluetooth.STATE_NONE;
	}

	public void setHandler(Handler pHandler, Handler p_receiveHandler) {
		mHandler = pHandler;
		receiveHandler = p_receiveHandler;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		mState = state;
		mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * 开始服务，主要是开始AcceptThread线程，打开一个监听回话由BaowenActivity的onResume方法调用，
	 * ***开启监听线程****
	 */
	public synchronized void start() {
		// 取消所有正在尝试连接的线程
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// 取消所有已经连接上的线程
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// 打开一个监听BluetoothServerSocket的线程
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();// 开启监听线程
		}
		setState(ConstantsBluetooth.STATE_LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device) {
		// Cancel any thread attempting to make a connection取消任何线程试图建立连接
		if (mState == ConstantsBluetooth.STATE_CONNECTING) {// 正在连接状态״̬
			if (mConnectThread != null) {// 有ConnectThread，结束
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection取消任何线程正在运行一个连接
		if (mConnectedThread != null) {// 有处于连接中的线程，结束
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device启动新线程连接设备
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();// 开启新的连接请求线程
		setState(ConstantsBluetooth.STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();// 和客户端开始通信

		// 把已连接的设备的名称发给UI线程
		Message msg = mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_STATE_CHANGE);
		msg.arg1 = ConstantsBluetooth.STATE_CONNECTED;
		msg.obj = device.getName();
		mHandler.sendMessage(msg);

		mState = ConstantsBluetooth.STATE_CONNECTED;
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		setState(ConstantsBluetooth.STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != ConstantsBluetooth.STATE_CONNECTED)
				return;
			r = mConnectedThread;// 得到连接线程
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(ConstantsBluetooth.STATE_LISTEN);

		// // Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_STATE_CHANGE);
		msg.obj = "fail";
		mHandler.sendMessage(msg);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	@SuppressWarnings("unused")
	private void connectionLost() {
		setState(ConstantsBluetooth.STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_STATE_CHANGE);
		msg.obj = "lost";
		mHandler.sendMessage(msg);
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled). 监听准备进入的连接，类似于服务端，
	 */
	private class AcceptThread extends Thread {
		// 本地的socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			// 创建一个新的监听服务器socket
			try {
				// 根据名称，UUID创建并返回BluetoothServerSocket，这是创建BluetoothSocket服务器端的第一步
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, SPP_UUID);
			} catch (IOException e) {
			}
			// 得到BluetoothServerSocket对象
			mmServerSocket = tmp;
		}

		public void run() {
			setName("AcceptThread");// set the name of thread
			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != ConstantsBluetooth.STATE_CONNECTED) {// 若没有连接，一直执行
				try {
					// 注意：BluetoothServerSocket的accept是一个阻塞性线程，一直到用户成功的配对，因此不要放在Activity中,
					// 远程蓝牙设备请求跟本设备建立连接请求时，
					// BluetoothServerSocket会在连接被接收时返回一个被连接的BluetoothSocket对象。
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}

				// 连接建立成功
				if (socket != null) {
					synchronized (BluetoothCommService.this) {
						switch (mState) {
						case ConstantsBluetooth.STATE_LISTEN:
						case ConstantsBluetooth.STATE_CONNECTING:
							// 已经连接上，开始ConnectedThread线程
							connected(socket, socket.getRemoteDevice());
							break;
						case ConstantsBluetooth.STATE_NONE:
						case ConstantsBluetooth.STATE_CONNECTED:
							// 或者设备没有准备好，或者已经连接，都将这个新的BluetoothSocket关掉
							// 释放服务套接字，但不会关闭accept()返回的BluetoothSocket
							try {
								socket.close();
							} catch (IOException e) {
							}
							break;
						}
					}
				}
			}
		}

		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails. 监听准备出去的连接
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;// 远程设备

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				// 通过远程设备直接创建一个BluetoothSocket，要求该设备已经打开了BluetoothServerSocket获得一个BluetoothSocket对象
				tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			setName("ConnectThread");

			// 取消搜索
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
				}
				// Start the service over to restart listening mode
				BluetoothCommService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothCommService.this) {
				mConnectThread = null;
			}

			// Start the connected thread，已连接上，管理连接
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions. 监听
	 */
	private class ConnectedThread extends Thread {
			private final BluetoothSocket mmSocket;
			private final OutputStream mmOutStream;
			private final BufferedInputStream reader;

			public ConnectedThread(BluetoothSocket socket) {
				mmSocket = socket;
				InputStream tmpIn = null;
				OutputStream tmpOut = null;

				// Get the BluetoothSocket input and output streams
				try {
					//获取输出、读取流
					tmpIn = socket.getInputStream();
					tmpOut = socket.getOutputStream();
				} catch (IOException e) {
				}
				//读取数据
				reader = new BufferedInputStream(tmpIn);
				mmOutStream = tmpOut;
			}

			@SuppressLint("NewApi")
			public void run() {
				byte[] buffer = new byte[20], temp = new byte[127];
				StringBuffer sb;
				String b;
				int numReadedBytes;
				boolean cleaning = true;
				long startTime = System.currentTimeMillis();

				// 保持对InputStream的监听
				while (true) {
					try {
						if (cleaning) {
							if (System.currentTimeMillis() - startTime < 2000) {
								numReadedBytes = reader.read(temp);
								// System.out.println("cleaning:" + numReadedBytes);
								continue;
							} else
								cleaning = false;
						}

						numReadedBytes = reader.read(buffer);
						// System.out.println("numReaded0:" + numReadedBytes);
						sb = new StringBuffer();
						for (int i = 0; i < numReadedBytes; i++) {
							b = Integer.toHexString(buffer[i] & 0xFF);
							if (b.length() < 2)
								sb.append("0");
							sb.append(b);
						}
						//将读取到的数据传递给handeler对象处理
						mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_READ, numReadedBytes, 0, sb.toString())
								.sendToTarget();
						buffer = new byte[20];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
				mHandler.obtainMessage(ConstantsBluetooth.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
