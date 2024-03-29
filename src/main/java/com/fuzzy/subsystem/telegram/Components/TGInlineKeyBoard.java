package com.fuzzy.subsystem.telegram.Components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kiperku
 * Date: 25.07.2023
 */

public class TGInlineKeyBoard extends InlineKeyboardMarkup{

    private List<List<InlineKeyboardButton>> buttonRows;

    public TGInlineKeyBoard(int rows){
        buttonRows = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttonRows.add(buttons);
        }
        this.setKeyboard(buttonRows);
    }


    public void addButtons(TGInlineButton... inlineKeyboardButtons){
        if (inlineKeyboardButtons != null && inlineKeyboardButtons.length > 0){
                for (TGInlineButton button : inlineKeyboardButtons){
                    buttonRows.get(button.getRow()).add(button);
                }
        }
    }

    public void addButton(TGInlineButton button){
        buttonRows.get(button.getRow()).add(button);
    }

    public static InlineKeyboardMarkup createInlineSingleButton(String text, String data){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(data);
        button.setCallbackData(data);
        return inlineKeyboardMarkup;
    }








}
