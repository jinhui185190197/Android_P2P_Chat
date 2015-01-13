package rdc.jim.receiver;

import rdc.jim.socket.connect.ServerHandler;
import rdc.jim.udpbrocast.ServiceCheckHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 处理wifiP2P广播事件接收器
 * 
 * @author Administrator
 * 
 */

public class WifiBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "WifiBroadcastReceiver";

	private Handler mHandler;

	// private WifiP2pManager.PeerListListener listener;

	public WifiBroadcastReceiver(Context context, WifiP2pManager manager,
			Channel channel, Handler handler) {

		this.mHandler = handler;
		// this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			// 判断wifip2p是否可以使用
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// WiFi P2P 可以使用
				Log.w(TAG, "wifip2p正常使用");
			} else {
				// // WiFi P2P 不可以使用
				Log.w(TAG, "wifip2p不能正常使用");
			}

		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {
			// 这台设备发生变化
			// 这里可以拿到自身设备的信息
			/**
			 * AVAILABLE 3 CONNECTED 0 FAILED 2 INVITED 1 UNAVAILABLE 4
			 */
			Log.w(TAG, "设备状态发生变化");
			WifiP2pDevice device = (WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			Log.d(TAG, "Device status -" + device.status);
			Log.d(TAG, "Device name -" + device.deviceName);
			Log.d(TAG, "Device addr -" + device.deviceAddress);
		} else if ("peers".equals(action)) {
			// 找到局域网中的设备
			// Log.d(TAG, "address : " + intent.getStringExtra("address"));
			// Log.d(TAG, "name : " + intent.getStringExtra("name"));
			// 通知主线程
			Message message = new Message();
			message.arg1 = 0;
			Bundle bundle = new Bundle();
			bundle.putString("address", intent.getStringExtra("address"));
			bundle.putString("name", intent.getStringExtra("name"));
			message.setData(bundle);
			mHandler.sendMessage(message);
		} else if ("message".equals(action)) {
			// 收到消息
			Log.e(TAG, "Message receiver.........");
			Message message = new Message();
			message.arg1 = 1;
			Bundle bundle = new Bundle();
			bundle.putString("msg", intent.getStringExtra("msg"));
			bundle.putString("type", intent.getStringExtra("type"));
			bundle.putString("address", intent.getStringExtra("address"));
			message.setData(bundle);
			mHandler.sendMessage(message);
		} else if (ServerHandler.NEW_CONNECTION.equals(action)) {
			// 有新的连接
			// 通知主线程更新列表状态
			Log.e(TAG, "NEW_CONNECTION");
			Message message = new Message();
			message.arg1 = 2;
			Bundle bundle = new Bundle();
			bundle.putString("address", intent.getStringExtra("address"));
			message.setData(bundle);
			mHandler.sendMessage(message);

		} else if (ServiceCheckHelper.DELAY_SERVICE.equals(action)) {
			//有设备离开了局域网
			//从列表中清除
			Log.e(TAG, "DELAY_SERVICE : " + intent.getStringExtra("address"));
			Message message = new Message();
			message.arg1 = 4;
			Bundle bundle = new Bundle();
			bundle.putString("address", intent.getStringExtra("address"));
			message.setData(bundle);
			mHandler.sendMessage(message);
		}

	}
}
