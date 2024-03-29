package com.fuzzy.subsystem.telegram.Components;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class TGEditMessage extends EditMessageText {

    public TGEditMessage(CallbackQuery callbackQuery){
        this.setChatId(callbackQuery.getMessage().getChatId());
        this.setMessageId(callbackQuery.getMessage().getMessageId());
    }

    public TGEditMessage(CallbackQuery callbackQuery, String text){
        this.setChatId(callbackQuery.getMessage().getChatId());
        this.setMessageId(callbackQuery.getMessage().getMessageId());
        this.setText(text);
    }

}
