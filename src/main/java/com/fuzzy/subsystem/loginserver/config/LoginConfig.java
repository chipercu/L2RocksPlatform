package com.fuzzy.subsystem.loginserver.config;


import com.fuzzy.config.configsystem.Config;
import com.fuzzy.config.configsystem.ISubsystemConfig;

public class LoginConfig implements ISubsystemConfig {

    @Override
    public String getConfigPathName() {
        return "login_config";
    }

    @Override
    public void init() {
        System.out.println(getConfigPathName() + " - leaded");
    }

    @Config(desc = "токен доступа github",
            strValue = "")
    public static String GITHUB_TOKEN;

    @Config(desc = "Аккаунт на github",
            strValue = "chipercu")
    public static String GITHUB_ACCOUNT;

    @Config(desc = "Репозиторий на github",
            strValue = "UpdateRepository")
    public static String GITHUB_REPOSITORY;

    @Config(desc = "Имя файла со списком защищаемых файлов от подмены на github",
            strValue = "hash_checked_list.json")
    public static String GITHUB_HASH_CHECKED_LIST_FILE_NAME;

    @Config(desc = "url файла со списком защищаемых файлов от подмены на github",
            strValue = "https://raw.githubusercontent.com/chipercu/UpdateRepository/master/hash_checked_list.json")
    public static String GITHUB_HASH_CHECKED_LIST_FILE_URL;

}