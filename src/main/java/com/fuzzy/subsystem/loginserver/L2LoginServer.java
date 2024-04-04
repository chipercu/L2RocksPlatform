package com.fuzzy.subsystem.loginserver;

import com.fuzzy.config.LoginConfig;
import com.fuzzy.config.TelnetConfig;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.ui_manager.logger.LoginServerLogViewer;
import com.fuzzy.subsystem.Server;
import com.fuzzy.subsystem.database.L2DatabaseFactory;
import com.fuzzy.subsystem.extensions.network.SelectorStats;
import com.fuzzy.subsystem.extensions.network.SelectorThread;
import com.fuzzy.subsystem.gameserver.GameServer;
import com.fuzzy.subsystem.gameserver.taskmanager.MemoryWatchDog;
import com.fuzzy.subsystem.gameserver.xml.XmlUtils;
import com.fuzzy.subsystem.loginserver.gameservercon.GSConnection;
import com.fuzzy.subsystem.status.Status;
import com.fuzzy.subsystem.util.Log;
import com.fuzzy.subsystem.util.Util;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class L2LoginServer {
    protected static L2LoginServer _instance;
    private final Logger _log = Logger.getLogger(L2LoginServer.class.getName());
    private final GSConnection _gameServerListener;
    private SelectorThread<L2LoginClient> _selectorThread;
    public static Status statusServer;
    public LoginController loginController;

    //private GameServer gameServer;

    public static L2LoginServer getInstance() {
        return _instance;
    }

    public L2LoginServer() {

        Server.SERVER_MODE = Server.MODE_LOGINSERVER;
        //      Local Constants
        final String LOG_FOLDER = "log"; // Name of folder for log file
        final String LOG_NAME = "./data/config/log.properties"; // Name of log file

        /*** Main ***/
        // Create log folder
        File logFolder = new File("./", LOG_FOLDER);
        logFolder.mkdir();

        // Create input stream for log file -- or store file data into memory
        InputStream is = null;
        try {
            is = new FileInputStream(LOG_NAME);
            LogManager.getLogManager().readConfiguration(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        _log.addHandler(new LoginServerLogViewer());

        if (LoginConfig.ComboMode) {
            Server.SERVER_MODE = Server.MODE_COMBOSERVER;
            Log.InitGSLoggers();
        }

        // Prepare Database
        try {
            L2DatabaseFactory.getInstance();
        } catch (SQLException e) {
            _log.severe("FATAL: Failed initializing database. Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed initializing database. Reason: " + e.getMessage());
        }

        try {
            LoginController.load();
        } catch (GeneralSecurityException e) {
            _log.severe("FATAL: Failed initializing LoginController. Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed initializing LoginController. Reason: " + e.getMessage());
        }

        try {
            GameServerTable.load();
        } catch (GeneralSecurityException e) {
            _log.severe("FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
        } catch (SQLException e) {
            _log.severe("FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed to load GameServerTable. Reason: " + e.getMessage());
        }

        if (LoginConfig.LoginProxyEnable)
            loadProxy();

        //this.loadBanFile();

        /* Accepting connections from players */
        Util.waitForFreePorts(LoginConfig.LoginserverHostname, LoginConfig.LoginserverPort);
        InetAddress ad = null;
        try {
            ad = InetAddress.getByName(LoginConfig.LoginserverHostname);
        } catch (Exception ignored) {
        }

        L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
        try {
            _selectorThread = new SelectorThread<>(new SelectorStats(), loginPacketHandler, loginPacketHandler, loginPacketHandler, LoginConfig.LoginServerProtectEnable ? new SelectorHelper() : null);
        } catch (IOException e) {
            _log.severe("FATAL: Failed to open Selector. Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed to open Selector. Reason: " + e.getMessage());
        }

        _gameServerListener = GSConnection.getInstance();
        _gameServerListener.start();
        _log.info("Listening for GameServers on " + LoginConfig.LoginHost + ":" + LoginConfig.LoginPort);

        if (TelnetConfig.EnableTelnet)
            try {
                statusServer = new Status(Server.MODE_LOGINSERVER);
                statusServer.start();
            } catch (IOException e) {
                _log.severe("Failed to start the Telnet Server. Reason: " + e.getMessage());
                if (LoginConfig.Debug)
                    e.printStackTrace();
            }
        else
            _log.info("LoginServer Telnet server is currently disabled.");

        try {
            SelectorThread.setAntiFlood(LoginConfig.AntiFloodEnable);
            SelectorThread.setAntiFloodSocketsConf(LoginConfig.MaxUnhandledSocketsPerIP, LoginConfig.UnhandledSocketsMinTTL);
            _selectorThread.openServerSocket(ad, LoginConfig.LoginserverPort);
        } catch (IOException e) {
            _log.severe("FATAL: Failed to open server socket on " + ad + ":" + LoginConfig.LoginserverPort + ". Reason: " + e.getMessage());
            if (LoginConfig.Debug)
                e.printStackTrace();
            Server.exit(1, "FATAL: Failed to open server socket on " + ad + ":" + LoginConfig.LoginserverPort + ". Reason: " + e.getMessage());
        }
        _selectorThread.start();
        _log.info("Login Server ready on port " + LoginConfig.LoginserverPort);
        _log.info(IpManager.getInstance().getBannedCount() + " banned IPs defined");

        if (LoginConfig.ComboMode)
            try {
                Util.waitForFreePorts(ConfigValue.GameserverHostname, ConfigValue.GameserverPort);

                if (TelnetConfig.EnableTelnet) {
                    Status _statusServer = new Status(Server.MODE_GAMESERVER);
                    _statusServer.start();
                } else
                    _log.info("GameServer Telnet server is currently disabled.");
                new GameServer();
            } catch (Exception e) {
                e.printStackTrace();
            }


            Shutdown.getInstance().startShutdownH(LoginConfig.AutoRestart, true);



        Util.gc(3, 333);
        _log.info("Free memory " + MemoryWatchDog.getMemFreeMb() + " of " + MemoryWatchDog.getMemMaxMb());
    }

    private void loadProxy() {
        try {
            File file = new File(LoginConfig.DatapackRoot + "/gameservers.xml");
            Document document = XmlUtils.readFile(file);

            Element root = document.getRootElement();
            for (Iterator i = root.elementIterator("game"); i.hasNext(); ) {
                Element game = (Element) i.next();
                int id = Integer.parseInt(game.attributeValue("id"));
                String name = game.attributeValue("name");

                GameInfo gi = new GameInfo(id, name);

                for (Element proxy : game.elements("proxy")) {
                    int id_p = Integer.parseInt(proxy.attributeValue("id"));
                    int max_player = proxy.attributeValue("max_player") == null ? 65535 : Integer.parseInt(proxy.attributeValue("max_player"));
                    int region = proxy.attributeValue("region") == null ? -1 : Integer.parseInt(proxy.attributeValue("region"));
                    int bit_mask = proxy.attributeValue("bit_mask") == null ? 0 : Integer.parseInt(proxy.attributeValue("bit_mask"));

                    String ip = proxy.attributeValue("ip");
                    String ip_in = proxy.attributeValue("ip_in");

                    gi._proxy.put(id_p, new Proxy(id_p, max_player, region, ip, ip_in, bit_mask));
                    _log.info("game[" + id + "][" + name + "] proxy[" + id_p + "][" + ip + "]");
                }
                GameServerTable._game_proxy.put(id, gi);
            }
        } catch (Exception e) {
            _log.severe("L2LoginServer: Error parsing gameservers.xml file. ");
            e.printStackTrace();
        }
    }

    public GSConnection getGameServerListener() {
        return _gameServerListener;
    }

    public boolean unblockIp(String ipAddress) {
        return loginController.ipBlocked(ipAddress);
    }

    public boolean setPassword(String account, String password) {
        return loginController.setPassword(account, password);
    }
}