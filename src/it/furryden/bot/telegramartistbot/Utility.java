package it.furryden.bot.telegramartistbot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

public class Utility {
	private static TelegramLongPollingBot fdbot = null;
	
	public static void sendDatabaseErrorMessage(long chat_id, String err) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText("Errore nella lettura del database, Errore: "+err+"\n\nContattare un amministratore tramite @furrydencontact_bot");
		try {
			fdbot.execute(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public static void sendUnauthorizedErrorMessage(long chat_id) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText("Non autorizzato ad usare questo comando");
		try {
			fdbot.execute(msg);
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
	}

	public static void sendParametersError(long chat_id) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText("Parametri non corretti");
		try {
			fdbot.execute(msg);
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
	}

	public static void sendUserNotFoundErrorMessage(long chat_id) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText("Utente non trovato");
		try {
			fdbot.execute(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendCommandNotFoundErrorMessage(long chat_id) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText("Comando non riconosciuto");
		try {
			fdbot.execute(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	public static Message sendMessage(long chat_id, String text) {
		SendMessage msg = new SendMessage()
			.setChatId(chat_id)
			.setText(EmojiParser.parseToUnicode(text));
		try {
			return fdbot.execute(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void deleteMessage(Integer messageId, String chat_id) {
		DeleteMessage dl = new DeleteMessage()
				.setChatId(chat_id)
				.setMessageId(messageId);
		try {
			fdbot.deleteMessage(dl);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public static void setBot(TelegramLongPollingBot bot) {
		fdbot = bot;
	}
}
