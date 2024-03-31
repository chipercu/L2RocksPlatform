package com.fuzzy.subsystem.loginserver.gameservercon;

import com.fuzzy.subsystem.loginserver.gameservercon.lspackets.RSAKey;

/**
 * @Author: Death
 * @Date: 12/11/2007
 * @Time: 20:13:42
 */
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
