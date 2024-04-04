package com.fuzzy.config;


import com.fuzzy.config.configsystem.ConfigClass;
import com.fuzzy.config.configsystem.ConfigField;

@ConfigClass(name = "game_server_config")
public class GameServerConfig {

    @ConfigField(desc = "Ключь лицензии.",
            strValue = "") public static String LicenseKey;

    @ConfigField(desc = "Bind ip of the gameserver, use * to bind on all available IPs",
            strValue = "*") public static String GameserverHostname;

    @ConfigField(desc = "Game server port",
            intValue = 7777) public static int GameserverPort;

    @ConfigField(desc = "This is transmitted to the clients connecting from an external network, so it has to be a public IP or resolvable hostname",
            strValue = "127.0.0.1") public static String ExternalHostname;

    @ConfigField(desc = "This is transmitted to the client from the same network, so it has to be a local IP or resolvable hostname",
            strValue = "127.0.0.1") public static String InternalHostname;


    @ConfigField(desc = """
            Если вы используете защиту CCPGuard то нужно поставить CCPGuard.
            Если вы используете защиту LameGuard то нужно поставить LameGuard.
            Если вы используете защиту SmartGuard(Akumu) то нужно поставить SmartGuard.
            Если вы используете защиту StrixGuard то нужно поставить StrixGuard.
            Если вы используете защиту L2Script то нужно поставить ScriptsGuard.
            Если у вас не установлена защита то ставьте NONE.
            """, strValue = "NONE") public static String GuardType;

    @ConfigField(desc = """
            Размер ключа для защиты CCP.
            # 74 для старых версий,80 для новых, 84 - для новой ветки.
            """, intValue = 80) public static int CCPGuardSize;

    @ConfigField(desc = "AdvIPSystem", boolValue = false) public static boolean AdvIPSystem;

    @ConfigField(desc = "Loginserver host", strValue = "127.0.0.1") public static String LoginHosts;

    @ConfigField(desc = "Loginserver ports", intArrValue = {9014}) public static int[] LoginPorts;

    @ConfigField(desc = "LoginUseCrypt", boolValue = false) public static boolean LoginUseCrypt;

    @ConfigField(desc = "This is the server id that the gameserver will request (i.e. 1 is Bartz)",
            intValue = 1) public static int RequestServerID;

    @ConfigField(desc = "If set to true, the login will give an other id to the server  if the requested id is already reserved",
            boolValue = true) public static boolean AcceptAlternateID;

    @ConfigField(desc = "Database Driver", strValue = "com.mysql.jdbc.Driver") public static String Driver;

    @ConfigField(desc = "Database url",
            strValue = "jdbc:mysql://localhost/l2tehno?useUnicode=true&characterEncoding=UTF-8") public static String URL;

    @ConfigField(desc = "Database login",
            strValue = "root") public static String Login;

    @ConfigField(desc = "Database Password",
            strValue = "68464846l2") public static String Password;

    @ConfigField(desc = "Database MaximumDbConnections",
            intValue = 1000) public static int MaximumDbConnections;

    @ConfigField(desc = """
            Через сколько секунд после последней активности будут закрыватся соединения с базой, по умолчанию 600 (10 минут)
            данный параметр важно согласовывать с настройками в самом mysql сервере, параметр interactive_timeout
            """,
            intValue = 600) public static int MaxIdleConnectionTimeout;

    @ConfigField(desc = """
            Интервал проверки неактивных соединений, по умолчанию 60 (1 минута)
            При условии стабильного соединения с базой и корректной настроки MaxIdleConnectionTimeout, можно выставлять 0 (не проверять)
            """,
            intValue = 60) public static int IdleConnectionTestPeriod;

    @ConfigField(desc = """
            Интервал проверки неактивных соединений, по умолчанию 60 (1 минута)
            При условии стабильного соединения с базой и корректной настроки MaxIdleConnectionTimeout, можно выставлять 0 (не проверять)
            """,
            boolValue = true) public static boolean UseDatabaseLayer;

}