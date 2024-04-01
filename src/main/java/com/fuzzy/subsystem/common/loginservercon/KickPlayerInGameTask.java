package com.fuzzy.subsystem.common.loginservercon;

import com.fuzzy.subsystem.extensions.network.L2GameClient;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * @Author: Death
 * @Date: 13/11/2007
 * @Time: 20:46:51
 */
public class KickPlayerInGameTask extends com.fuzzy.subsystem.common.RunnableImpl {
    private final L2GameClient client;

    public KickPlayerInGameTask(L2GameClient client) {
        this.client = client;
    }

    public void runImpl() {
        L2Player activeChar = client.getActiveChar();

        if (activeChar != null)
            activeChar.logout(false, false, true, true);
        else {
            client.sendPacket(Msg.ServerClose(null));
            client.closeNow(false);
        }
    }
}
