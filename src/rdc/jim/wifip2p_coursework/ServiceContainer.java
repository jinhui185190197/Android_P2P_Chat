package rdc.jim.wifip2p_coursework;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * 保存已发现的局域网设备 注意线程问题
 * 
 * @author 橘子哥
 *
 */

public class ServiceContainer {

	private static final ServiceContainer container = new ServiceContainer();
	private Hashtable<String, Integer> mServiceMap;

	private ServiceContainer() {
		mServiceMap = new Hashtable<String, Integer>();
	}

	public static ServiceContainer getInstance() {
		return container;
	}

	/**
	 * 添加设备 （若设备已经存在，则将计数器置0）
	 * 
	 * @param address
	 */
	public void addService(String address) {
		if (address != null) {
			// 第一次发现该设备
			if (!mServiceMap.containsKey(address)) {
				mServiceMap.put(address, 0);
			} else {
				Integer count = mServiceMap.get(address);
				mServiceMap.put(address, 0);
			}

		}
	}

	/**
	 * 获取延时次数
	 * 
	 * @param address
	 * @return
	 */
	public int getDelayTimes(String address) {
		return mServiceMap.get(address);
	}

	public int getServiceSize() {
		return mServiceMap.size();
	}

	/**
	 * 获取延时设备列表
	 * 
	 * @return
	 */
	public ArrayList<String> checkOutService() {
		ArrayList<String> delayList = new ArrayList<String>();
		Set<String> keySet = mServiceMap.keySet();
		for (String key : keySet) {
			int delay = mServiceMap.get(key);
			if (delay > 3) {
				delayList.add(key);
			}
		}

		return delayList;
	}

	/**
	 * 为所有计数器+1
	 */
	public void addTime() {
		Set<String> keySet = mServiceMap.keySet();
		for (String key : keySet) {
			mServiceMap.put(key, mServiceMap.get(key) + 1);
		}
	}

	public void removeServices(List serviceList) {
		// 移除相应的对象
		for (int i = 0; i < serviceList.size(); i++) {
			mServiceMap.remove(serviceList.get(i));
		}
	}

}
