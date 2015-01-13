package rdc.jim.service;

import java.io.IOException;
import java.nio.charset.Charset;

import rdc.jim.socket.connect.ServerHandler;
import rdc.jim.udpbrocast.ServiceCheckHelper;
import rdc.jim.udpbrocast.UdpBrocastReceiver;
import rdc.jim.udpbrocast.UdpBrocastSender;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class UdpBrocastService extends Service {

	private IBinder binder = new UdpBrocastService.LocalBinder();
	public static final String TAG = "UdpBrocastService";
	private WifiManager manager;
	private ServerHandler mServerHandler;
	private String mNickName;
	private UdpBrocastSender mSender;
	private UdpBrocastReceiver mReceiver;
	private ServiceCheckHelper mCheckHelper;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "Service create");
		super.onCreate();
		// 启动服务端
		try {
			mServerHandler = new ServerHandler(this);
			mServerHandler.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 启动udp接收
		manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mReceiver = new UdpBrocastReceiver(manager, this);
		new Thread(mReceiver).start();
		
		//启动设备检查
		mCheckHelper = new ServiceCheckHelper(this);
		new Thread(mCheckHelper).start();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "Service onStartCommand");
		if (intent.getStringExtra("nickName") != null) {
			mNickName = intent.getStringExtra("nickName");
		} else {
			mNickName = "Test";
		}

		// 启动udp广播
		mSender = new UdpBrocastSender(new String(mNickName.getBytes(),
				Charset.forName("UTF-8")));
		new Thread(mSender).start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "Service destory");
		//关闭相关线程
		if(mReceiver != null){
			Log.e(TAG, "stopping Receiver......");
			mReceiver.stopThread();
		}
		if(mSender != null){
			Log.e(TAG, "stopping Sender......");
			mSender.stopThread();
		}
		if (mServerHandler != null) {
			Log.e(TAG, "stopping Server......");
			mServerHandler.stopThread();
		}
		if(mCheckHelper != null){
			Log.e(TAG, "stopping CheckHelper......");
			mCheckHelper.stopThread();
		}
		super.onDestroy();
	}

	public class LocalBinder extends Binder {

		UdpBrocastService getService() {
			return UdpBrocastService.this;
		}
	}

}
