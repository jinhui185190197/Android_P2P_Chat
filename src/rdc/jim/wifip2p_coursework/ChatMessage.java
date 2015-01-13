package rdc.jim.wifip2p_coursework;

public class ChatMessage {
	
	//消息时间
		private String chatDate;
		//消息名称
		private String chatName;
		//消息内容
		private String chatContent;
		//消息内容类型 
		
		
		//消息类型
		public static enum Type{
			INCOME,
			OUTCOME,
			
		};
		
		private Type type ;

		public String getChatDate() {
			return chatDate;
		}

		public void setChatDate(String chatDate) {
			this.chatDate = chatDate;
		}

		public String getChatName() {
			return chatName;
		}

		public void setChatName(String chatName) {
			this.chatName = chatName;
		}

		public String getChatContent() {
			return chatContent;
		}

		public void setChatContent(String chatContent) {
			this.chatContent = chatContent;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

}
