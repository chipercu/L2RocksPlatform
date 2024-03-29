package com.fuzzy.subsystem.telegram.Components;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class TGMenu implements CallBackConst{

    public static void showMenu(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("MENU");
        TGInlineKeyBoard keyBoard = new TGInlineKeyBoard(2);
        keyBoard.addButton(new TGInlineButton("Посмотреть все", SHOW_ALL_EVENT, 1));
        keyBoard.addButton(new TGInlineButton("По категориям", CATEGORY_EVENT, 1));
        keyBoard.addButton(new TGInlineButton("Создать событье", CREATE_EVENT, 2));
        keyBoard.addButton(new TGInlineButton("Настройки", USER_SETTINGS, 2));
        message.setReplyMarkup(keyBoard);




//        ImEventApplication.getInstance().getBean(TelegramBotService.class).executeMessage(message);

    }
    public static void showMenu(CallbackQuery callbackQuery){
        EditMessageText message = new EditMessageText();
        message.setChatId(callbackQuery.getMessage().getChatId());
        message.setMessageId(callbackQuery.getMessage().getMessageId());
        message.setText("MENU");
        TGInlineKeyBoard keyBoard = new TGInlineKeyBoard(2);
        keyBoard.addButton(new TGInlineButton("Посмотреть все", SHOW_ALL_EVENT, 1));
        keyBoard.addButton(new TGInlineButton("По категориям", CATEGORY_EVENT, 1));
        keyBoard.addButton(new TGInlineButton("Создать событье", CREATE_EVENT, 2));
        keyBoard.addButton(new TGInlineButton("Настройки", USER_SETTINGS, 2));
        message.setReplyMarkup(keyBoard);
//        ImEventApplication.getInstance().getBean(TelegramBotService.class).executeMessage(callbackQuery, message);
    }

}
