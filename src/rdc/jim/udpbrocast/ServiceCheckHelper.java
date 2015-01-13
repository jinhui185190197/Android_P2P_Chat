package rdc.jim.udpbrocast;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import rdc.jim.wifip2p_coursework.ServiceContainer;

/**
 * 检查设备是否在线
 * @author 橘子哥
 *
 */

public class ServiceCheckHelper implements Runnable{
	public static final String DELAY_SERVICE = "DELAY_SERVICE";
	public static final String TAG = "ServiceCheckHelper";
	private volatile boolean flag;
	private Context mContext;
	
	public ServiceCheckHelper(Context context){
		flag = true;
		mContext = context;
	}
	@Override
	public void run() {
		Log.e(TAG, "Service Check start........");
		while(flag){
			try {
				Thread.sleep(3000);
				Log.w(TAG, "checking............");
				ArrayList<String> delayList = ServiceContainer.getInstance().checkOutService();
				for(int i = 0 ; i<delayList.size(); i++){
					Intent intent = new Intent();
					intent.setAction(DELAY_SERVICE);
					intent.putExtra("address", delayList.get(i));
					mContext.sendBroadcast(intent);
					
				}
				ServiceContainer.getInstance().removeServices(delayList);
				ServiceContainer.getInstance().addTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.e(TAG, "Service Check stop........");
	
	}
	public void stopThread(){
		flag = false;
	}
}
