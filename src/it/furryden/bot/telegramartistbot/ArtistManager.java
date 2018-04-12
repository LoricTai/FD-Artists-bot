package it.furryden.bot.telegramartistbot;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistManager {
	private static HashMap<String,Artist> artists = new HashMap<>();
	private static HashMap<Long, String> search = new HashMap<>();
	
	public static Artist getArtist(String id) {
		return artists.get(id);
	}
	
	public static String getArtistId(long chatId) {
		return search.get(chatId);
	}

	public static boolean isArtist(long chat_id) {
		return search.containsKey(chat_id);
	}
	
	public static void loadArtists() throws DatabaseException {
		ArrayList<Artist> artistL = DatabaseManager.loadArtists();
		for(Artist a: artistL) {
			artists.put(a.getId(), a);
			search.put(a.getUserId(), a.getId());
		}
	}

	public static void addArtist(User u) throws DatabaseException {
		Artist a = new Artist(u.getNickname(), u.getChatId(), null, null, null, false, null);
		DatabaseManager.addArtist(a);
		artists.put(a.getId(), a);
		search.put(u.getChatId(), a.getId());
	}

	public static ArrayList<Artist> getArtistList() {
		ArrayList<Artist> res = new ArrayList<>();
		for(Artist a: artists.values()) res.add(a);
		return res;
	}

	public static void updatePropic(long chat_id, String fileId) throws DatabaseException {
		Artist a  = getArtist(getArtistId(chat_id));
		a.setPropic(fileId);
		DatabaseManager.updateArtist(a);
	}

	public static void updateNick(long chat_id, String text) throws DatabaseException {
		Artist a = getArtist(getArtistId(chat_id));
		a.setNick(text);
		DatabaseManager.updateArtist(a);
	}

	public static boolean canUploadSample(long chat_id) {
		return (getArtist(getArtistId(chat_id)).getSamples().size()<=5);
	}

	public static void updateLink(long chat_id, String text) throws DatabaseException {
		Artist a = getArtist(getArtistId(chat_id));
		a.setUrl(text);
		DatabaseManager.updateArtist(a);
	}

	public static void addSample(long chat_id, String fileId) throws DatabaseException {
		Artist a = getArtist(getArtistId(chat_id));
		a.addSample(fileId);
		DatabaseManager.addSample(a,fileId);
	}
}
