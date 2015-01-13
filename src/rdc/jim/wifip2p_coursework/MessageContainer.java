package rdc.jim.wifip2p_coursework;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 保存消息
 * @author 橘子哥
 *
 */

public class MessageContainer {
	
	public static final String TAG = "MessageContainer";
	public static final int MAX_CHAT_SIZE = 10;
	private HashMap<String, ArrayList<ChatMessage>> mChatMap;
	public static MessageContainer container = new MessageContainer();
	
	private MessageContainer() {
		mChatMap = new HashMap<String, ArrayList<ChatMessage>>();
	}
	
	public static MessageContainer getInstance(){
		return container;
	}
	
	/**
	 * 保存消息
	 */
	public void addChatMessage(String addr, ChatMessage msg){
		//检查对应的list信息条数是否超过最大条数
		//超过，清空
		ArrayList<ChatMessage> list = mChatMap.get(addr);
		if(list.size() > MAX_CHAT_SIZE){
			list.clear();
		}
		list.add(msg);
	}
	
	/**
	 * 获取对应消息队列
	 * @param addr
	 * @return
	 */
	public ArrayList<ChatMessage> getChatMessages(String addr){
		if(addr != null){
			return mChatMap.get(addr);
		}
		return null;
	}
	
	/**
	 * 添加对应列表
	 * @param addr
	 */
	public void addChatList(String addr){
		if(addr != null)
		mChatMap.put(addr, new ArrayList<ChatMessage>());
	}
	
	/**
	 * 清除IP为addr的消息列表
	 * @param addr
	 */
	public void removeChatList(String addr){
		if(addr != null){
			if(mChatMap.containsKey(addr))
			mChatMap.remove(addr);
		}
	}

}
