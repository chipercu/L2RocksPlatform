package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;

import java.util.logging.Logger;

public class ChangeAccessLevel extends ClientBasePacket {
    public static final Logger log = Logger.getLogger(ChangeAccessLevel.class.getName());

    public ChangeAccessLevel(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    @Override
    public void read() {
        int level = readD();
        String account = readS();
        String comments = readS();
        int banTime = readD();

        LoginController.getInstance().setAccountAccessLevel(account, level, comments, banTime);
        log.info("Changed " + account + " access level to " + level);
    }
}