package it.furryden.bot.telegramartistbot;

import java.util.HashMap;

public class ActionManager {
	public static HashMap<Long, ActionWrapper> queue = new HashMap<>();
	
	public static void addAction(long chat_id, Action a, Integer panelId, Integer messageId) {
		queue.put(chat_id, new ActionWrapper(a, panelId, messageId));
	}
	
	public static ActionWrapper getAction(long chat_id) {
		return queue.get(chat_id);
	}
	
	public static void clearAction(long chat_id) {
		queue.remove(chat_id);
	}
}
