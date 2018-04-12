package it.furryden.bot.telegramartistbot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class KeyboardMarkupFactory {
	private InlineKeyboardMarkup markupInline;
	private List<List<InlineKeyboardButton>> rowsInline;
	
	public KeyboardMarkupFactory() {
		markupInline = new InlineKeyboardMarkup();
		rowsInline = new ArrayList<>();
	}

	public void add(InlineKeyboardButton button) {
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		rowInline.add(button);
		rowsInline.add(rowInline);
	}
	
	public InlineKeyboardMarkup getKeyboardMarkup() {
		markupInline.setKeyboard(rowsInline);
		return markupInline;
	}
}
