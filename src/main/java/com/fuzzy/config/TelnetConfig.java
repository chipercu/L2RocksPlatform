package com.fuzzy.config;


import com.fuzzy.config.configsystem.ConfigClass;
import com.fuzzy.config.configsystem.ConfigField;

@ConfigClass(name = "telnet_config")
public class TelnetConfig {

    @ConfigField(desc = "Telnet по умолчанию выключен. Установите на True для включения",
            boolValue = false) public static boolean EnableTelnet;

    @ConfigField(desc = "Укажите порт telnet",
            intValue = 12346) public static int StatusPort;

    @ConfigField(desc = "Пароль можно не указывать",
            strValue = "somePass") public static String StatusPW;

    @ConfigField(desc = "Данный список содержит адреса и хосты, с которых разрешен вход на telnet-сервер.",
            strValue = "127.0.0.1,localhost") public static String ListOfHosts;


}