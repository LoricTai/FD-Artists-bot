package it.furryden.bot.telegramartistbot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {
	public static ArrayList<Artist> loadArtists() throws DatabaseException {
		SQLQuery q = new SQLQuery("select * from (£.user as u join £.user_artist as ua on u.chat_id=ua.user) join £.artist as a on ua.info=a.idArtist");
		try {
			ResultSet r = q.executeQuery();
			ArrayList<Artist> artists = new ArrayList<>();
			while(r.next()) {
				String[] tempP = new String[] {r.getString("idArtist")};
				SQLQuery temp = new SQLQuery("select * from £.sample as s join £.artist as a on s.artist=a.idArtist where a.idArtist like ?", tempP);
				ArrayList<String> samples = new ArrayList<>();
				ResultSet smp = temp.executeQuery();
				while(smp.next()) samples.add(smp.getString("sample"));
				artists.add(new Artist(r.getString("idArtist"), r.getLong("chat_id"), r.getString("nickname"), r.getString("profilePic"), r.getString("faurl"), ((r.getInt("commStatus")==0)?false:true), samples));
			}
			return artists;
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "load artist");
		}
	}

	public static void updateUser(User u) throws DatabaseException {
		String[] params = new String[] {u.getNickname(), u.getRole().toString().toLowerCase(), String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("update £.user set nickname=?, role=? where chat_id like ?", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, u.getNickname());
		}
	}
	
	public static void addUser(User u) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId()), u.getNickname()};
		SQLQuery q = new SQLQuery("insert into £.user(chat_id, nickname) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, u.getNickname());
		}
	}

	public static ArrayList<User> loadUsers() throws DatabaseException {
		SQLQuery q = new SQLQuery("select * from £.user");
		try {
			ResultSet r = q.executeQuery();
			ArrayList<User> result = new ArrayList<>();
			while (r.next()) {
				System.out.println(""+r.getLong("chat_id")+"-"+ r.getString("nickname")+"-"+r.getString("role"));
				result.add(new User(r.getLong("chat_id"), r.getString("nickname"), Role.valueOf(r.getString("role").toUpperCase())));
			}
			return result;
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "loadUsers");
		}
	}

	public static void addArtist(Artist a) throws DatabaseException {
		String[] params = new String[] {a.getId()};
		String[] params2 = new String[] {String.valueOf(a.getUserId()), a.getId()};
		SQLQuery q = new SQLQuery("insert into £.artist(idArtist) values(?)", params);
		SQLQuery q2 = new SQLQuery("insert into £.user_artist(user,info) values(?,?)", params2);
		try {
			q.executeUpdate();
			q2.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addArtist-"+a.getId());
		}
	}

	public static void updateArtist(Artist a) throws DatabaseException {
		String[] params = new String[] {a.getProfilePic(), a.getNickname(), a.getUrl(), ((a.getCommStatus())?"1":"0"), a.getId()};
		SQLQuery q = new SQLQuery("update £.artist set profilePic=?, nickname=?, faurl=?, commStatus=? where idArtist like ?", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "updateArtist-"+a.getId());
		}
	}

	public static void addSample(Artist a, String fileId) throws DatabaseException {
		String[] params = new String[] {fileId, a.getId()};
		SQLQuery q = new SQLQuery("insert into £.sample(sample,artist) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addSample-"+a.getId());
		}
	}
	
	public static ArrayList<Artist> loadFollows(User u) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("select * from £.follow where user like ?", params);
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
	
	public static void addFollow(User u, Artist a) throws DatabaseException {
		String[] params = new String[] {String.valueOf(u.getChatId()), a.getId()};
		SQLQuery q = new SQLQuery("insert into £.follow(user,artist) values(?,?)", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "addFollow-"+u.getNickname()+"-"+a.getId());
		}
	}
	
	public static void removeFollow(User u, Artist a) throws DatabaseException {
		String[] params = new String[] {a.getId(), String.valueOf(u.getChatId())};
		SQLQuery q = new SQLQuery("delete from £.follow where artist like ? and user like ?", params);
		try {
			q.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(q, e, "removeFollow-"+u.getNickname()+"-"+a.getId());
		}
	}
}
