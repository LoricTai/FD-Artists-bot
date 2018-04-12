package it.furryden.bot.telegramartistbot;

public class ActionWrapper {
	private Action action;
	private Integer panelId;
	private Integer messageId;
	
	public ActionWrapper(Action action, Integer panelId, int messageId) {
		this.action = action;
		this.messageId = messageId;
		this.panelId = panelId;
	}
	
	public Action getAction() {
		return action;
	}
	
	public Integer getMessageId() {
		return messageId;
	}
	
	public Integer getPanelId() {
		return panelId;
	}
}
