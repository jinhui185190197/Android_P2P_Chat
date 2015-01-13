package rdc.jim.wifip2p_coursework;

import java.util.HashMap;

import rdc.jim.socket.connect.ClientHandler;

import android.util.Log;

/**
 * 保存连接Clientsocket
 * @author jim
 *
 */

public class ClientSocketContainer {
	private static final String TAG = "ClientSocketContainer";
	
	public static final ClientSocketContainer  container = new ClientSocketContainer(); 
	private HashMap<String, ClientHandler> mSocketMap;
	
	
	private ClientSocketContainer(){
		mSocketMap = new HashMap<String, ClientHandler>();
	}
	
	public static ClientSocketContainer getInstance(){
		return container;
	}
	
	/**
	 * 保存ClientHandler
	 * @param address 服务地址
	 * @param handler 相应连接的子线程
	 */
	public void addClientSocket(String address, ClientHandler handler){
		
		Log.w(TAG, address);
		mSocketMap.put(address, handler);
	}
	
	/**
	 * 删除ClientHandler
	 * @param address 服务地址
	 */
	public void deleteClientSocket(String address){
		if(address != null){
			if(mSocketMap.containsKey(address)){
				mSocketMap.remove(address);
			}
		}
		
	}
	
	/**
	 * 獲取ClientHandler
	 * @param address 服務地址
	 */
	public ClientHandler getClientSocket(String address){
		
		return mSocketMap.get(address);
		
	}
	
	

}
