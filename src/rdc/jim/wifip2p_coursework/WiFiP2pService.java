package rdc.jim.wifip2p_coursework;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * 实体类
 * 保存搜索到的设备的信息
 * @author Administrator
 *
 */

public class WiFiP2pService {
	
	public static final int SERVICE_CLIENT = 0x001;
	public static final int SERVICE_SERVICE = 0x002;
	
	WifiP2pDevice device;
    String instanceName = null;
    String serviceRegistrationType = null;
    String ip;
    boolean isConnect;
    int serviceRole;

}
