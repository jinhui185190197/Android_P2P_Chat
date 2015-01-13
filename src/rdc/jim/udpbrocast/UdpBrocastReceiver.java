package rdc.jim.udpbrocast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;

import rdc.jim.wifip2p_coursework.ServiceContainer;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

/**
 * 用于接受UDP广播
 * 
 * @author 橘子哥
 * 
 */

public class UdpBrocastReceiver implements Runnable {

	public static final String TAG = "UdpBrocastReceiver";
	private static WifiManager.MulticastLock lock;
	public static final int PORT = 8900;
	public static final int MSG_SIZE = 256;
	private Context mContext;
	private String mSelfIP;
	private DatagramSocket datagramSocket;
	private volatile boolean flag;

	@SuppressWarnings("deprecation")
	public UdpBrocastReceiver(WifiManager manager, Context context) {
		this.flag = true;
		this.lock = manager.createMulticastLock("UDP_WIFI");
		this.mContext = context;
		this.mSelfIP = Formatter.formatIpAddress(manager.getConnectionInfo()
				.getIpAddress());
	}

	public void startListen() {
		// int port = 8900;
		// 设定接收字节大小，客户端发送的信息不能大于该大小
		byte[] msg = new byte[MSG_SIZE];
		try {

			// 建立UDP连接
			datagramSocket = new DatagramSocket(PORT);
			datagramSocket.setBroadcast(true);
			DatagramPacket packet = new DatagramPacket(msg, MSG_SIZE);
			try {
				Log.w(TAG, "UDPReceiver Thread start..........");
				while (flag) {
					
					lock.acquire();

					datagramSocket.receive(packet);
					// 排除自身IP
					if (!packet.getAddress().getHostAddress().equals(mSelfIP)) {
						String strMsg = new String(packet.getData(),0,packet.getLength(),
								Charset.forName("UTF-8"));
						
						ServiceContainer.getInstance().addService(packet.getAddress().getHostAddress().toString());
						Intent intent = new Intent();
						intent.setAction("peers");
						intent.putExtra("address", packet.getAddress()
								.getHostAddress().toString());
						intent.putExtra("name", strMsg);
						mContext.sendBroadcast(intent);
					}

					lock.release();

				}
				Log.e(TAG, "ReceiverThread stop!...........");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(datagramSocket != null){
				datagramSocket.close();
			}
			//释放锁
			if(lock.isHeld()){
				lock.release();
			}
		}
	}

	public void stopThread() {
		flag = false;
		if (datagramSocket != null) {
			datagramSocket.close();
		}
		// 释放锁
		if (lock.isHeld()) {
			lock.release();
		}

	}

	@Override
	public void run() {
		startListen();
	}

}
