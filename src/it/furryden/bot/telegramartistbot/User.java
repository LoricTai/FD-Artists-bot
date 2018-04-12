package it.furryden.bot.telegramartistbot;

public class User {
	private String nickname;
	private long chat_id;
	private Role role;
	
	public User(long chat_id, String nickname, Role role) {
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
		if(obj instanceof User) {
			return ((User)obj).getChatId() == this.chat_id;
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
