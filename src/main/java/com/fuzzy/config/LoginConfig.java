package com.fuzzy.config;


import com.fuzzy.config.configsystem.ConfigClass;
import com.fuzzy.config.configsystem.ConfigField;

@ConfigClass(name = "login_config")
public class LoginConfig {

    @ConfigField(desc = "Режим дебага",
            boolValue = false) public static boolean Debug;

    @ConfigField(desc = "Режим дебага LS_GS",
            boolValue = false) public static boolean DEBUG_LS_GS;

    @ConfigField(desc = "Режим дебага",
            boolValue = false) public static boolean DebugClientPackets;

    @ConfigField(desc = "Режим дебага",
            boolValue = false) public static boolean DebugServerPackets;

    @ConfigField(desc = "Директория временных файлов",
            strValue = "data/temp/") public static String TEMP_DIRECTORY;

    @ConfigField(desc = "Bind ip of the loginserver, use * to bind on all available IPs",
            strValue = "127.0.0.1") public static String LoginserverHostname;

    @ConfigField(desc = "порт логин сервера",
            intValue = 2106) public static int LoginserverPort;

    @ConfigField(desc = "Количество попыток залогиниться после которых дается бан",
            intValue = 20) public static int LoginTryBeforeBan;

    @ConfigField(desc = "Time in seconds, the server stores the unsuccessful attempts to login",
            intValue = 300) public static int LoginTryCheckDuration;

    @ConfigField(desc = "Time in seconds to ban for password hacks",
            intValue = 600) public static int LoginTryBanDuration;

    @ConfigField(desc = "Список внутренних адресов",
            strValue = "127.0.0.1,192.168.0.0-192.168.255.255,10.0.0.0-10.255.255.255,172.16.0.0-172.16.31.255")
    public static String InternalIpList;

    @ConfigField(desc = "Использовать альтернативный вариант продвинутой системы IP адрессов",
            boolValue = false) public static boolean AltAdvIPSystem;

    @ConfigField(desc = "",
            strValue = "*") public static String LoginHost;

    @ConfigField(desc = "The port on which login will listen for GameServers",
            intValue = 9014) public static int LoginPort;

    @ConfigField(desc = "Combo mode - run GameServer inside LoginServer",
            boolValue = false) public static boolean ComboMode;

    @ConfigField(desc = "Даная опция присылает клиенту 2 дополнительных ключа при авторизации. Если вы ее отключаете, и у вас ухитрились залезть с подменой ака, то потом не жалуйтесь.",
            boolValue = true) public static boolean ShowLicence;

    @ConfigField(desc = "use this option to choose whether accounts will be created automatically or not.",
            boolValue = true) public static boolean AutoCreateAccounts;

    @ConfigField(desc = "Account name",
            strValue = "[A-Za-z0-9]{3,14}") public static String AnameTemplate;

    @ConfigField(desc = "Account password",
            strValue = "[A-Za-z0-9]{8,24}") public static String ApasswdTemplate;

    @ConfigField(desc = "Актуальное шифрование, рекомендуется Whirlpool/DoubleWhirlpoolWithSalt",
            strValue = "Whirlpool") public static String DefaultPasswordEncoding;

    @ConfigField(desc = "Поддерживаемые устаревшие методы, есть DES (офф) и SHA1 (l2j)",
            strValue = "SHA1;DES") public static String LegacyPasswordEncoding;

    @ConfigField(desc = "salt",
            strValue = "blablabla") public static String DoubleWhirlpoolSalt;

    @ConfigField(desc = "Allow old authentication method (w/o RSA encryption)",
            boolValue = false) public static boolean AllowOldAuth;

    @ConfigField(desc = "LoginServer Protect",
            boolValue = false) public static boolean LoginServerProtectEnable;

    @ConfigField(desc = "Кешированые ключи BlowFish",
            intValue = 20) public static int BlowFishKeys;

    @ConfigField(desc = "Кешированые ключи RSA",
            intValue = 10) public static int RSAKeyPairs;

    @ConfigField(desc = "",
            intValue = 15) public static int IpUpdateTime;

    @ConfigField(desc = "AutoRestart period, hours",
            intValue = -1) public static int AutoRestart;

    @ConfigField(desc = "SelectorSleepTime",
            intValue = 5) public static int SelectorSleepTime;

    @ConfigField(desc = "LoginWatchdogTimeout",
            intValue = 15000) public static int LoginWatchdogTimeout;


    @ConfigField(desc = "Включить режим прокси?",
            boolValue = false) public static boolean LoginProxyEnable;

    @ConfigField(desc = "BanAtFlood",
            boolValue = false) public static boolean BanAtFlood;

    @ConfigField(desc = "ChangePasswordAtFlood",
            boolValue = false) public static boolean ChangePasswordAtFlood;

    @ConfigField(desc = "Проверка на GameGuard",
            boolValue = false) public static boolean GGCheck;

    @ConfigField(desc = "Combo mode - run GameServer inside LoginServer",
            boolValue = false) public static boolean COMBO_MODE;

    @ConfigField(desc = "Путь к датапаку",
            strValue = ".") public static String DatapackRoot;

    @ConfigField(desc = "Включить защиту от брута? (пропускает до выбора сервера даже с неправильно введенными данными от аккаунта и только потом говорит, что логин/пароль введены не верно)",
            boolValue = false) public static boolean FakeLogin;

    @ConfigField(desc = "Настройка АнтиФлуда",
            boolValue = false) public static boolean AntiFloodEnable;

    @ConfigField(desc = "CCPGuardEnable",
            boolValue = false) public static boolean CCPGuardEnable;

    @ConfigField(desc = "При включении АнтиФлуда с одного IP разрешается не более чем MaxUnhandledSocketsPerIP соединений, которые не послали ни одного пакета",
            intValue = 5) public static int MaxUnhandledSocketsPerIP;

    @ConfigField(desc = "если через MaxUnhandledSocketsPerIP количество соединение не приходит ни одного пакета в течении UnhandledSocketsMinTTL мс. то оно закрывается",
            intValue = 5000) public static int UnhandledSocketsMinTTL;

    @ConfigField(desc = "Размер пула потоков, для выполнения запланированных задач, рекомендуемое значение: CPU_CORE_COUNT x 4",
            intValue = 16) public static int ScheduledThreadPoolSize;

    @ConfigField(desc = "Размер пула потоков, для незамедлительного выполнения задач, рекомендуемое значение: CPU_CORE_COUNT x 2",
            intValue = 8) public static int ExecutorThreadPoolSize;

    @ConfigField(desc = "LoginserverId",
            intValue = 0) public static int LoginserverId;

    @ConfigField(desc = "LoginServerProtocol",
            intValue = 2) public static int LoginServerProtocol;

    @ConfigField(desc = "GCbreak",
            boolValue = false) public static boolean GCbreak;

     @ConfigField(desc = "SkipBannedIp",
            boolValue = false) public static boolean SkipBannedIp;

    @ConfigField(desc = "GuardType",
            strValue = "NONE") public static String GuardType;

    @ConfigField(desc = "GSLSConnectionSleep",
            intValue = 10) public static int GSLSConnectionSleep;

    @ConfigField(desc = "LoginServerConnectCount",
            intValue = 3) public static int LoginServerConnectCount;

    @ConfigField(desc = "LoginServerTryCheckDuration",
            intValue = 1000) public static int LoginServerTryCheckDuration;

    @ConfigField(desc = "LoginServerConnectBanTime",
            intValue = 600) public static int LoginServerConnectBanTime;


}