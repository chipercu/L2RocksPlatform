package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.loginserver.GameServerTable;
import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;

import java.util.logging.Logger;

public class PlayerLogout extends ClientBasePacket {
    public static final Logger log = Logger.getLogger(PlayerLogout.class.getName());

    public PlayerLogout(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    @Override
    public void read() {
        String account = readS();

        getGameServer().removeAccountFromGameServer(account);
        LoginController.getInstance().removeAuthedLoginClient(account);

        if (ConfigValue.Debug)
            log.info("Player " + account + " logged out from gameserver [" + getGameServer().getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getGameServer().getServerId()));
    }
}