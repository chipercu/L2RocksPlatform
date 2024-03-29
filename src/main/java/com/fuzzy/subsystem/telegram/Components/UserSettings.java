package com.fuzzy.subsystem.telegram.Components;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;


public class UserSettings implements CallBackConst{

    public static void showUserSettings(CallbackQuery callbackQuery){
        final TGEditMessage message = new TGEditMessage(callbackQuery, "Настройки");
        message.setReplyMarkup(userSettingsButtons());
        //ImEventApplication.getInstance().getBean(TelegramBotService.class).execute(message);
    }

    public static TGInlineKeyBoard userSettingsButtons(){
        TGInlineKeyBoard keyBoard = new TGInlineKeyBoard(2);
        keyBoard.addButton(new TGInlineButton("Редактировать", REDACT_USER, 1));
        keyBoard.addButton(new TGInlineButton("Удалить свой профиль", DELETE_USER, 1));
        keyBoard.addButton(new TGInlineButton("Пока не знаю", MAIL, 2));
        keyBoard.addButton(new TGInlineButton("MENU", MENU, 2));
        return keyBoard;
    }
    public static TGInlineKeyBoard conformDeleteUser(){
        TGInlineKeyBoard keyBoard = new TGInlineKeyBoard(1);
        keyBoard.addButton(new TGInlineButton("Подтвердить", CONFIRM_DELETE_USER, 1));
        keyBoard.addButton(new TGInlineButton("Отмена", USER_SETTINGS, 1));
        return keyBoard;
    }






}
