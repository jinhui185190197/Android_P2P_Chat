package rdc.jim.wifip2p_coursework;

import java.util.HashMap;

import rdc.jim.socket.connect.ChatManager;



/**
 * 保存于当前设备（服务器）相连的会话
 * @author 橘子哥
 *
 */
public class ServerSocketContainer {
	private static final String TAG = "ServerSocketContainer";

	private static ServerSocketContainer container= new ServerSocketContainer();
	private HashMap<String, ChatManager> mSocketMap;
	
	private ServerSocketContainer(){
		mSocketMap = new HashMap<String, ChatManager>();
	}
	public static ServerSocketContainer getInstance(){
		return container; 
	}
	
	/**
	 * 添加session
	 * @param addr
	 * @param session
	 */
	public void addServerSocket(String addr, ChatManager session){
		mSocketMap.put(addr, session);
	}
	
	/**
	 * 删除session
	 * @param addr
	 */
	public void deleteServerSocket(String address){
		if(address != null){
			if(mSocketMap.containsKey(address)){
				mSocketMap.remove(address);
			}
		}
	}
	
	/**
	 * 获得session
	 * @param addr
	 * @return
	 */
	public ChatManager getServerSocket(String addr){
		return mSocketMap.get(addr);
	}
}
