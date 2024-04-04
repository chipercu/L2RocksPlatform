package com.fuzzy.subsystem.loginserver.gameservercon;

import com.fuzzy.subsystem.loginserver.gameservercon.lspackets.RSAKey;

public class KeyTask {
    public KeyTask(AttGS gameserver) {
        try {
            gameserver.setRSA(new RSACrypt());
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameserver.sendPacket(new RSAKey(gameserver.getRSAPublicKey()));
    }
}
