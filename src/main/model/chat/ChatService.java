package model.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {

	// key - login, value - message
	private static Map<String, String> converations = new ConcurrentHashMap<>();
	
	public void addNewConversations() {
		
	}
}
