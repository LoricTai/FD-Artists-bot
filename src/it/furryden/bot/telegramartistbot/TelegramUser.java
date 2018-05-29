package it.furryden.bot.telegramartistbot;

public class TelegramUser {
	private String nickname;
	private long chat_id;
	private Role role;
	
	public TelegramUser(long chat_id, String nickname, Role role) {
		this.nickname = nickname;
		this.chat_id = chat_id;
		this.role = role;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public long getChatId() {
		return chat_id;
	}
	
	public Role getRole() {
		return role;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof TelegramUser) {
			return ((TelegramUser)obj).getChatId() == this.chat_id;
		}
		else return false;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
