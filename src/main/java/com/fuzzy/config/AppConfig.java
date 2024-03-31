package com.fuzzy.config;


import com.fuzzy.config.configsystem.Config;
import com.fuzzy.config.configsystem.ISubsystemConfig;

public class AppConfig implements ISubsystemConfig {

    @Override
    public String getConfigPathName() {
        return "app_config";
    }

    @Override
    public void init(){
        System.out.println(getConfigPathName() + " - leaded");
    }

    @Config(desc = "Режим дебага",
            boolValue = true) public static Boolean DEBUG_MODE;
    @Config(desc = "Директория временных файлов",
            strValue = "data/temp/") public static String TEMP_DIRECTORY;
    @Config(desc = "Директория конфиг файлов",
            strValue = "data/config/") public static String CONFIG_DIRECTORY = "data/config/";


}