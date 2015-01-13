package rdc.jim.udpbrocast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

/**
 * 用于发送udp广播
 * 
 * @author 橘子哥
 *
 */

public class UdpBrocastSender implements Runnable {

	public static final String TAG = "UdpBrocastSender";
	public static final int PORT = 8900;
	public static final int MSG_SIZE = 256;
	private volatile boolean flag;
	private String sMsg;

	public UdpBrocastSender(String msg) {
		this.sMsg = msg;
		this.flag = true;
	}

	/**
	 * 发送消息
	 */
	public void sendMsg() {
		// Log.d(TAG, sMsg);
		
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();

		} catch (Exception e) {
			e.printStackTrace();
		}
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getByName("255.255.255.255");

		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] msgByte = sMsg.getBytes();
		DatagramPacket packet = new DatagramPacket(msgByte, msgByte.length,
				localAddress, PORT);
		try {
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void stopThread(){
		flag = false;
	}

	@Override
	public void run() {
		Log.w(TAG, "UDPSender Thread start..........");
		while (flag) {

			try {
				sendMsg();
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.e(TAG, "SenderThread stop!...........");
	};

}
