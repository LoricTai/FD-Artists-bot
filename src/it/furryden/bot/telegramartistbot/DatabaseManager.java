package it.furryden.bot.telegramartistbot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {
	public static ArrayList<Artist> loadArtists() throws DatabaseException {
		SQLQuery q = new SQLQuery("select * from (telegram_user as u join telegram_user_artist as ua on u.chat_id=ua.telegram_user) join artist as a on ua.info=a.id");
		try {
			ResultSet r = q.executeQuery();
			ArrayList<Artist> artists = new ArrayList<>();
			while(r.next()) {
				String[] tempP = new String[] {r.getString("id")};
				SQLQuery temp = new SQLQuery("select * from sample as s join artist as a on s.artist=a.id where a.id like ?", tempP);
				ArrayList<String> samples = new ArrayList<>();
				ResultSet smp = temp.executeQuery();
				while(smp.next()) samples.add(smp.getString("sample"));
				artists.add(new Artist(r.getString("id"), r.getLong("chat_id"), r.getString("nickname"), r.getString("profile_pic"), r.getString("faurl"), r.getBoolean("comm_status"), samples));
			}
			return artists;
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "load artist");
		}
	}

	public static void updateUser(TelegramUser u) throws DatabaseException {
		String[] params = new String[] {u.getNickname(), String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("update telegram_user set nickname=?, role='"+u.getRole().toString().toLowerCase()+"' where chat_id = ?;", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, u.getNickname());
		}
	}
	
	public static void addUser(TelegramUser u) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId()), u.getNickname()};
		SQLQuery q = new SQLQuery("insert into telegram_user(chat_id, nickname) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, u.getNickname());
		}
	}

	public static ArrayList<TelegramUser> loadUsers() throws DatabaseException {
		SQLQuery q = new SQLQuery("select * from telegram_user");
		try {
			ResultSet r = q.executeQuery();
			ArrayList<TelegramUser> result = new ArrayList<>();
			while (r.next()) {
				System.out.println(""+r.getLong("chat_id")+"-"+ r.getString("nickname")+"-"+r.getString("role"));
				result.add(new TelegramUser(r.getLong("chat_id"), r.getString("nickname"), Role.valueOf(r.getString("role").toUpperCase())));
			}
			return result;
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "loadUsers");
		}
	}

	public static void addArtist(Artist a) throws DatabaseException {
		String[] params = new String[] {a.getId()};
		String[] params2 = new String[] {String.valueOf(a.getUserId()), a.getId()};
		SQLQuery q = new SQLQuery("insert into artist(id) values(?)", params);
		SQLQuery q2 = new SQLQuery("insert into telegram_user_artist(telegram_user,info) values(?,?)", params2);
		try {
			q.executeUpdate();
			q2.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addArtist-"+a.getId());
		}
	}

	public static void updateArtist(Artist a) throws DatabaseException {
		String[] params = new String[] {a.getProfilePic(), a.getNickname(), a.getUrl(), a.getId()};
		SQLQuery q = new SQLQuery("update artist set profile_pic=?, nickname=?, faurl=?, comm_status='"+a.getCommStatus()+"' where id like ?", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "updateArtist-"+a.getId());
		}
	}

	public static void addSample(Artist a, String fileId) throws DatabaseException {
		String[] params = new String[] {fileId, a.getId()};
		SQLQuery q = new SQLQuery("insert into sample(sample,artist) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addSample-"+a.getId());
		}
	}
	
	public static ArrayList<Artist> loadFollows(TelegramUser u) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("select * from follow where telegram_user like ?", params);
		try {
			ResultSet r = q.executeQuery();
			ArrayList<Artist> result = new ArrayList<>();
			while(r.next()) {
				result.add(ArtistManager.getArtist(r.getString("artist")));
			}
			return result;
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "loadFollows");
		}
	}
	
	public static void addFollow(TelegramUser u, Artist a) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId()), a.getId()};
		SQLQuery q = new SQLQuery("insert into follow(telegram_user,artist) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addFollow-"+u.getNickname()+"-"+a.getId());
		}
	}
	
	public static void removeFollow(TelegramUser u, Artist a) throws DatabaseException {
		String[] params = new String[] {a.getId(), String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("delete from follow where artist like ? and telegram_user like ?", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "removeFollow-"+u.getNickname()+"-"+a.getId());
		}
	}
}
