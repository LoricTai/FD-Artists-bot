package it.furryden.bot.telegramartistbot;

import java.sql.SQLException;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {	
	private static FurryDenArtistBot fd;
	
	public static void main(String[] args) {
		if(args.length<5) {
			System.out.println("Numero di parametri non sufficiente. Parametri richiesti: idbot, dbmsip, dbmsuser, dbmspass, dbname");
		}
		ApiContextInitializer.init();
	    TelegramBotsApi botsApi = new TelegramBotsApi();
	    fd = new FurryDenArtistBot();
	    fd.setBotId(args[0]);
	    PostgreSQLConfig.getInstance().setConnectionParameters(args[1], args[2], args[3], args[4]);
	    Utility.setBot(fd);
	    try {
	    	PostgreSQLConfig.checkDB();
	        UserManager.loadUsers();
	        ArtistManager.loadArtists();
	        UserManager.loadFollows();
	        botsApi.registerBot(fd);
	        System.out.println("Bot attivo");
	    } catch (TelegramApiException e) {
	        e.printStackTrace();
	    } catch (DatabaseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static TelegramLongPollingBot getBot() {
		return fd;
	}
}
