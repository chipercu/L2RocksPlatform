package com.fuzzy.subsystem.common.loginservercon.gspackets;

import com.fuzzy.subsystem.extensions.network.L2GameClient;

public class PlayerAuthRequest extends GameServerBasePacket {
    public PlayerAuthRequest(L2GameClient client) {
        writeC(0x05);
        writeS(client.getLoginName());
        writeD(client.getSessionId().playOkID1);
        writeD(client.getSessionId().playOkID2);
        writeD(client.getSessionId().loginOkID1);
        writeD(client.getSessionId().loginOkID2);
    }
}