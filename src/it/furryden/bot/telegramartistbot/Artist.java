package it.furryden.bot.telegramartistbot;

import java.util.ArrayList;

public class Artist {
	private String id;
	private long userId;
	private String nickname;
	private String profilePic;
	private String url;
	private boolean commStatus;
	private ArrayList<String> samples;
	
	public Artist(String id, long userId, String nickname, String profilePic, String url, boolean commStatus, ArrayList<String> samples) {
		this.id = id;
		this.userId = userId;
		this.nickname = nickname;
		this.profilePic = profilePic;
		this.url = url;
		this.commStatus = commStatus;
		this.samples = samples;
	}
	
	public String getId() {
		return id;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getProfilePic() {
		return profilePic;
	}
	
	public String getUrl() {
		return url;
	}
	
	public boolean getCommStatus() {
		return commStatus;
	}
	

	public ArrayList<String> getSamples() {
		return samples;
	}

	public void setPropic(String fileId) {
		this.profilePic = fileId;
	}

	public void setNick(String nickname) {
		this.nickname = nickname;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addSample(String fileId) {
		this.samples.add(fileId);
	}
}
