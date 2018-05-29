package it.furryden.bot.telegramartistbot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiParser;

public class FurryDenArtistBot extends TelegramLongPollingBot {
	private String botId;

	@Override
	public String getBotUsername() {
		return "FurryDenArtistsBot";
	}

	@Override
	public void onUpdateReceived(Update u) {
		manageUpdate(u);
	}

	@Override
	public String getBotToken() {
		return botId;
	}

	private void manageUpdate(Update u) {
		long chat_id = (u.hasMessage()) ? u.getMessage().getChat().getId() : u.getCallbackQuery().getMessage().getChatId();
		String nickname =  (u.hasMessage()) ? u.getMessage().getChat().getUserName() : u.getCallbackQuery().getFrom().getUserName();
		try {
			UserManager.updateUser(chat_id, nickname);
		} catch (DatabaseException e) {
			System.out.println("Error on user update");
			e.printPreviousStackTrace();
		}
		if(ActionManager.getAction(chat_id)!=null) {
			ActionWrapper a = ActionManager.getAction(chat_id);
			try {
				switch(a.getAction()) {
					case UPDATEPROPIC: {
						if(u.hasMessage() && u.getMessage().hasPhoto()) ArtistManager.updatePropic(chat_id, u.getMessage().getPhoto().get(0).getFileId());					
						break;
					}
					case UPDATENICK: {
						if(u.hasMessage() && u.getMessage().hasText()) ArtistManager.updateNick(chat_id, u.getMessage().getText());
						break;
					}
					case UPDATELINKFA: {
						if(u.hasMessage() && u.getMessage().hasText()) ArtistManager.updateLink(chat_id, u.getMessage().getText());
						break;
					}
					case UPLOADSAMPLE: {
						if(u.hasMessage() && u.getMessage().hasPhoto()) {
							List<PhotoSize> samples = u.getMessage().getPhoto();
							int i=0;
							while(ArtistManager.canUploadSample(chat_id)) {
								ArtistManager.addSample(chat_id, samples.get(i++).getFileId());
							}
						}
						break;
					}
					default: {
						break;
					}
				}
				Utility.deleteMessage(u.getMessage().getMessageId(), String.valueOf(chat_id));
				Utility.deleteMessage(a.getMessageId(), String.valueOf(chat_id));
				Utility.deleteMessage(a.getPanelId(), String.valueOf(chat_id));
				sendPanel(chat_id);
			} catch (DatabaseException e) {
				Utility.sendDatabaseErrorMessage(chat_id, e.getMessage());
			}
		}
		else if(u.hasMessage() && u.getMessage().hasText()) {
			System.out.println("Messaggio da "+u.getMessage().getChat().getUserName()+": "+u.getMessage().getText());
			parseText(u);
		}
		else if (u.hasCallbackQuery()) {
			String callback = u.getCallbackQuery().getData();
			System.out.println("Callback da "+UserManager.getUser(Long.parseLong(callback.split("_")[0])).getNickname()+": "+callback);
			parseCallback(u);
		}
		else if (u.hasMessage() && u.getMessage().hasPhoto() && UserManager.isAdmin(u.getMessage().getChatId())) {
			Utility.sendMessage(chat_id, u.getMessage().getPhoto().get(0).getFileId());
		}
		else {
			Utility.sendCommandNotFoundErrorMessage(chat_id);
		}
	}

	private void parseText(Update u) {
		if(!(tryParseCommand(u.getMessage().getText(), u.getMessage().getChatId()))) {
			Utility.sendCommandNotFoundErrorMessage(u.getMessage().getChatId());
		}
	}

	private boolean tryParseCommand(String text, long chatId) {
		switch(text.substring(0,1)) {
			case "/": parseCommand(text.substring(1), chatId);return true;
			default: return false;
		}
	}

	private void parseCommand(String command, long chat_id) {
		String[] cmdP = command.split(" ");
		switch(cmdP[0]) {
			case "help": sendHelp(chat_id);break;
			case "lista": sendArtistList(chat_id);break;
			case "start": sendWelcome(chat_id);break;
			case "pannello": sendPanel(chat_id);break;
			case "admins": sendAdminsList(chat_id);break;
			case "ruolo": {
				if(cmdP.length!=2 && cmdP.length!=3) {
					Utility.sendParametersError(chat_id);
				}
				else parseRole(cmdP, chat_id);
				break;
			}
		}
	}

	private void sendPanel(long chat_id) {
		if(ArtistManager.isArtist(chat_id)) {
			Artist a = ArtistManager.getArtist(ArtistManager.getArtistId(chat_id));
			SendPhoto msg = new SendPhoto()
					.setChatId(chat_id)
					.setCaption("Artista: "+a.getNickname()+"\n\nPagina FA: "+a.getUrl()+"\n\nCommissions: "+((a.getCommStatus())?"APERTE":"CHIUSE"))
					.setPhoto((a.getProfilePic()==null)?"https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png":a.getProfilePic());
			InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
			List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
			List<InlineKeyboardButton> rowInline = new ArrayList<>();
			rowInline.add(new InlineKeyboardButton().setText("Aggiorna immagine profilo").setCallbackData("" + chat_id + "_updatepropic_"+a.getId()));
			rowInline.add(new InlineKeyboardButton().setText("Aggiorna nickname").setCallbackData("" + chat_id + "_updatenick_"+a.getId()));
			rowInline.add(new InlineKeyboardButton().setText("Link FurAffinity").setCallbackData("" + chat_id + "_updatelink_"+a.getId()));
			rowInline.add(new InlineKeyboardButton().setText("Aggiungi immagine d'esempio").setCallbackData("" + chat_id + "_uploadsample_"+a.getId()));
			rowsInline.add(rowInline);
			markupInline.setKeyboard(rowsInline);
			msg.setReplyMarkup(markupInline);
			try {
				sendPhoto(msg);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		else Utility.sendUnauthorizedErrorMessage(chat_id);
	}

	private void sendHelp(long chat_id) {
		TelegramUser u = UserManager.getUser(chat_id);
		String msg = "Lista dei comandi:\n\nComandi utente:\n/lista - visualizza lista degli artisti\n/help - visualizza questo help\n\n";
		if(u.getRole().equals(Role.ADMIN)) msg+="Comandi amministratore:\n/ruolo <nickname> <utente|admin|artista> - Assegna all'utente il "
												+"ruolo definito\nAdmins - visualizza lista amministratori\n\n";
		if(u.getRole().equals(Role.ARTIST)) msg+="Comandi artista: \n/panello - visualizza pannello di controllo artista\n\n";
		Utility.sendMessage(chat_id, msg);
	}

	private void parseRole(String[] cmdP, long chat_id) {
		if(UserManager.isAdmin(chat_id)) {
			TelegramUser u=UserManager.getUser(cmdP[1]);
			if(u==null) Utility.sendUserNotFoundErrorMessage(chat_id);
			else if(cmdP.length==2) {
				Utility.sendMessage(chat_id, "L'utente @"+u.getNickname()+" ha come ruolo: "+u.getRole());
			}
			else {
				try {
					System.out.println(cmdP[2].toUpperCase());
					Role r = Role.valueOf(cmdP[2].toUpperCase());
					UserManager.setRole(u, r);
					Utility.sendMessage(chat_id, "Nuovo ruolo di @"+cmdP[1]+" : "+r.toString().toLowerCase());
				} catch (DatabaseException e) {
					Utility.sendDatabaseErrorMessage(chat_id, e.getMessage());
				} catch (IllegalArgumentException ex) {
					Utility.sendParametersError(chat_id);
				}
			}
		}
		else Utility.sendUnauthorizedErrorMessage(chat_id);
	}

	private void sendAdminsList(long chat_id) {
		if(UserManager.isAdmin(chat_id)) {
			String[] admins = UserManager.getAdminList();
			String msg = "Lista amministratori: \n";
			for(int i=0; i<admins.length; i++) {
				msg+="\n@"+admins[i];
			}
			Utility.sendMessage(chat_id, msg);
		}
		else {
			Utility.sendUnauthorizedErrorMessage(chat_id);
		}
	}

	private void sendWelcome(long chatId) {
		SendPhoto logo = new SendPhoto()
				.setChatId(chatId)
				.setCaption("Benvenuto su FurryDen Artists!")
				.setPhoto("https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png");
		SendMessage msg = new SendMessage()
						.setText("Con questo bot potrai vedere una lista degli artisti che collaborano con noi, vederne delle immagini "
						+ "di esempio, e commissionarli!\n\n"
						+ "Per cominciare, visualizza un elenco degli artisti con il comando /lista\n\n"
						+ "Per problemi o informazioni, contattare @furrydencontact_bot")
						.setChatId(chatId);

		try {
			sendPhoto(logo);
			execute(msg);
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
	}

	private void sendArtistList(long chatId) {
		SendMessage msg = new SendMessage()
				.setChatId(chatId)
				.setText("Lista artisti:");
        KeyboardMarkupFactory kmf = new KeyboardMarkupFactory();
        ArrayList<Artist> artists = ArtistManager.getArtistList();
        for(Artist a: artists) {
        	kmf.add(new InlineKeyboardButton()
        			.setText(((a.getNickname()!=null)?a.getNickname():a.getId()))
        			.setCallbackData("" + chatId + "_list_"+a.getId()));            
        }
		try {
			execute(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void parseCallback(Update u) {
		String[] query = u.getCallbackQuery().getData().split("_");
		long chat_id = Long.parseLong(query[0]);
		
		try {
			switch(query[1]) {
				case "list": {
					if(!sendArtistInfoResponse(query[2], chat_id)) {
						sendCallbackError(query, chat_id);
					}
					else {
						Message m = u.getCallbackQuery().getMessage();
					    Utility.deleteMessage(m.getMessageId(), String.valueOf(m.getChatId()));
					}
					break;
				}
				case "example": {
					if(!sendExampleResponse(query[2], chat_id)) {
						sendCallbackError(query, chat_id);
					}
					break;
				}
				case "commission": {
					if(ArtistManager.getArtist(query[2]).getCommStatus()) {

					}
					else Utility.sendMessage(chat_id, "Questo artista non accetta commissioni al momento");
					break;
				}
				case "follow": {
				    UserManager.follow(chat_id, ArtistManager.getArtist(query[2]));
				    Message m = u.getCallbackQuery().getMessage();
				    Utility.deleteMessage(m.getMessageId(), String.valueOf(m.getChatId()));
				    sendArtistInfoResponse(query[2], m.getChatId());
					break;
				}
				case "unfollow": {
					UserManager.unfollow(chat_id, ArtistManager.getArtist(query[2]));
					Message m = u.getCallbackQuery().getMessage();
				    Utility.deleteMessage(m.getMessageId(), String.valueOf(m.getChatId()));
				    sendArtistInfoResponse(query[2], m.getChatId());
					break;
				}
				case "pmsg": {
					Utility.sendMessage(chat_id, "Contatto dell'artista: @"+UserManager.getUser(ArtistManager.getArtist(query[2]).getUserId()).getNickname());
					break;
				}
				case "updatepropic": {
					Message m = Utility.sendMessage(chat_id, "Manda la foto da impostare come imamgine di profilo");
					ActionManager.addAction(chat_id, Action.UPDATEPROPIC, u.getCallbackQuery().getMessage().getMessageId(), m.getMessageId());
					break;
				}
				case "updatenick": {
					Message m = Utility.sendMessage(chat_id, "Manda il nuovo nickname");
					ActionManager.addAction(chat_id, Action.UPDATENICK, u.getCallbackQuery().getMessage().getMessageId(), m.getMessageId());
					break;
				}
				case "updatelink": {
					Message m = Utility.sendMessage(chat_id, "Manda il nuovo link FA");
					ActionManager.addAction(chat_id, Action.UPDATELINKFA, u.getCallbackQuery().getMessage().getMessageId(), m.getMessageId());
					break;
				}
				case "uploadsample": {
					ArrayList<String> samples = ArtistManager.getArtist(ArtistManager.getArtistId(chat_id)).getSamples();
					if(samples != null && samples.size()==5) {
						Utility.sendMessage(chat_id, "Hai già aggiunto il massimo numero di foto consentito");
						break;
					}
					Message m = Utility.sendMessage(chat_id, "Manda un'immagine da aggiungere ai tuoi esempi.\n"+(5-((samples == null)?0:samples.size()))+" immagini rimanenti");
					ActionManager.addAction(chat_id, Action.UPLOADSAMPLE, u.getCallbackQuery().getMessage().getMessageId(), m.getMessageId());
					break;
				}
				default: sendCallbackError(query, chat_id);
			}
		} catch (DatabaseException e) {
			Utility.sendDatabaseErrorMessage(chat_id, e.getMessage());
			System.out.println("Query: "+e.getQuery().getString());
			e.printPreviousStackTrace();
		}
	}

	private boolean sendArtistInfoResponse(String name, long chat_id) {
		Artist a = ArtistManager.getArtist(name);
		if(a==null) return false;
		SendPhoto msg = new SendPhoto()
				.setChatId(chat_id)
				.setCaption(EmojiParser.parseToUnicode(((UserManager.follows(chat_id, a))?" :star2:\n":"\n")+"Artista: "+a.getNickname()+"\n\nPagina FA: "+a.getUrl()+"\n\nCommissioni: "+((a.getCommStatus())?":white_check_mark:":":x:")))
				.setPhoto((a.getProfilePic()==null)?"https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png":a.getProfilePic());
		KeyboardMarkupFactory kmf = new KeyboardMarkupFactory();
        kmf.add(new InlineKeyboardButton().setText("Immagini esempio").setCallbackData("" + chat_id + "_example_" + a.getId()));
        kmf.add(new InlineKeyboardButton().setText("Commissiona").setCallbackData("" + chat_id + "_commission_" + a.getId()));
        if(!UserManager.follows(chat_id, a)) {
        	kmf.add(new InlineKeyboardButton().setText("Segui").setCallbackData("" + chat_id + "_follow_" + a.getId()));
        }
        else {
        	kmf.add(new InlineKeyboardButton().setText("Smetti di seguire").setCallbackData("" + chat_id + "_unfollow_" + a.getId()));
        }
        kmf.add(new InlineKeyboardButton().setText("Contatta in privato").setCallbackData("" + chat_id + "_pmsg_" + a.getId()));
        msg.setReplyMarkup(kmf.getKeyboardMarkup());
		try {
			sendPhoto(msg);
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
		return true;
	}

	private boolean sendExampleResponse(String name, long chat_id) {
		Artist a = ArtistManager.getArtist(name);
		if(a==null) return false;
		ArrayList<String> samples = a.getSamples();
		if(samples.isEmpty()) Utility.sendMessage(chat_id, "Questo artista non ha immagini d'esempio");
		for(String sample:samples) {
			try {
				sendPhoto(new SendPhoto().setPhoto(sample).setChatId(chat_id));
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void sendCallbackError(String[] callback, long chatId) {
		String call = "";
		for(int i=0; i<callback.length; i++) {
			call+=callback[i]+"_";
		}
		call=call.substring(0, call.length()-1);
		String text = "Errore su callback: " + call + ". \n\nContattare un amministratore via @furrydencontact_bot";
		System.out.println(text+"lolasd");
		SendMessage msg = new SendMessage()
			.setChatId(chatId)
			.setText(text);
		try {
			execute(msg);
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}
}
