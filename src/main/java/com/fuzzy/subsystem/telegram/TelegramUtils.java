package com.fuzzy.subsystem.telegram;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by a.kiperku
 * Date: 17.01.2024
 */

public class TelegramUtils {

    public static String currentTime(){
        return " [" + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date(System.currentTimeMillis())) + "]";
    }


}
