package it.furryden.bot.telegramartistbot;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager {
	private static HashMap<Long, User> users = new HashMap<>();
	private static HashMap<Long, ArrayList<Artist>> followed = new HashMap<>();

	public static void loadUsers() throws DatabaseException {
		ArrayList<User> usrs = DatabaseManager.loadUsers();
		for(User u: usrs) {
			users.put(u.getChatId(), u);
		}
	}

	public static User getUser(long chat_id) {
		if(users.containsKey(chat_id)) return users.get(chat_id);
		else return null;
	}

	public static User getUser(String nickname) {
		for(User u: users.values()) {
			if(u.getNickname().equals(nickname)) return u;
		}
		return null;
	}

	public static boolean isAdmin(long chat_id) {
		return (getUser(chat_id)).getRole().equals(Role.ADMIN);
	}

	public static void setRole(User u, Role role) throws DatabaseException {
		for(User ux: users.values()) {
			if(u.equals(ux)) {
				u.setRole(role);
				DatabaseManager.updateUser(u);
				if(role.equals(Role.ARTIST)) ArtistManager.addArtist(u);
				Utility.sendMessage(u.getChatId(), "Sei stato promosso al ruolo di: "+role.toString());
			}
		}
	}

	public static boolean follows(long chat_id, Artist a) {
	    	return followed.get(chat_id).contains(a);
	}

	public static void updateUser(long chatId, String nickname) throws DatabaseException {
		User u = getUser(chatId);
		if(u == null) {
			u = new User(chatId, nickname, Role.USER);
			DatabaseManager.addUser(u);
			users.put(u.getChatId(), u);
		}
		else {
			if(!(u.getNickname().equals(nickname))) DatabaseManager.updateUser(u);
			u.setNickname(nickname);
		}
	}

	public static String[] getAdminList() {
		ArrayList<String> admins = new ArrayList<>();
		for(User u: users.values()) {
			if(u.getRole().equals(Role.ADMIN)) admins.add(u.getNickname());
		}
		return admins.toArray(new String[admins.size()]);
	}

	public static void follow(long chat_id, Artist artist) throws DatabaseException {
		followed.get(chat_id).add(artist);
		DatabaseManager.addFollow(users.get(chat_id), artist);
	}
	
	public static void unfollow(long chat_id, Artist a) throws DatabaseException {
		followed.get(chat_id).remove(a);
		DatabaseManager.removeFollow(users.get(chat_id), a);
	}

	public static void loadFollows() throws DatabaseException {
		for(User u: users.values()) {
			followed.put(u.getChatId(), DatabaseManager.loadFollows(u));
		}
	}
}
