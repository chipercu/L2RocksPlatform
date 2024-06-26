package com.fuzzy.subsystem.status;

import com.fuzzy.config.TelnetConfig;
import com.fuzzy.subsystem.Server;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Status extends Thread {
    protected static Logger _log = Logger.getLogger(Status.class.getName());
    private final ServerSocket statusServerSocket;

    private final int _mode;
    private final String _StatusPW;
    public static GameStatusThread telnetlist;
    private final GArray<LoginStatusThread> _loginStatus = new GArray<>();

    @Override
    public void run() {
        while (true) {
            try {
                Socket connection = statusServerSocket.accept();

                if (_mode == Server.MODE_GAMESERVER || _mode == Server.MODE_COMBOSERVER)
                    new GameStatusThread(connection, _StatusPW);
                else if (_mode == Server.MODE_LOGINSERVER) {
                    LoginStatusThread lst = new LoginStatusThread(connection, _StatusPW);
                    if (lst.isAlive())
                        _loginStatus.add(lst);
                }
                if (isInterrupted()) {
                    try {
                        statusServerSocket.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                }
            } catch (IOException e) {
                if (isInterrupted()) {
                    try {
                        statusServerSocket.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                }
            }
            try {
                Thread.sleep(3000); /* Защита от глюков */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Status(int mode) throws IOException {
        super("Status");
        _mode = mode;
        _StatusPW = TelnetConfig.StatusPW;

        if (_StatusPW == null)
            _log.warning("Warning: server's Telnet Function Has No Password Defined!");
            //_log.warning("A Password Has Been Automaticly Created!");
            //_StatusPW = RndPW(10);
            //System.out.println("Password Has Been Set To: " + _StatusPW);
        else
            _log.fine("Password Has Been Set");
        statusServerSocket = new ServerSocket(TelnetConfig.StatusPort);
        if (_mode == Server.MODE_LOGINSERVER)
            _log.fine("StatusServer for LoginServer Started! - Listening on Port: " + TelnetConfig.StatusPort);
        else
            _log.fine("StatusServer for GameServer Started! - Listening on Port: " + TelnetConfig.StatusPort);
    }

    @SuppressWarnings("unused")
    private String RndPW(int length) {
        StringBuilder password = new StringBuilder();
        String lowerChar = "qwertyuiopasdfghjklzxcvbnm";
        String upperChar = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String digits = "1234567890";
        for (int i = 0; i < length; i++) {
            int charSet = Rnd.get(3);
            switch (charSet) {
                case 0 -> password.append(lowerChar.charAt(Rnd.get(lowerChar.length() - 1)));
                case 1 -> password.append(upperChar.charAt(Rnd.get(upperChar.length() - 1)));
                case 2 -> password.append(digits.charAt(Rnd.get(digits.length() - 1)));
            }
        }
        return password.toString();
    }

    public void SendMessageToTelnets(String msg) {
        for (LoginStatusThread ls : _loginStatus)
            if (!ls.isInterrupted())
                ls.printToTelnet(msg);
    }
}