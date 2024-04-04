package com.fuzzy.subsystem.loginserver.gameservercon.lspackets;

import com.fuzzy.config.LoginConfig;
import com.fuzzy.subsystem.loginserver.GameServerTable;

public class AuthResponse extends ServerBasePacket {
    public AuthResponse(int serverId, int LastProtocolVersion) {
        writeC(0x02);
        writeC(serverId);
        writeS(GameServerTable.getInstance().getServerNameById(serverId));
        writeC(LoginConfig.ShowLicence ? 0 : 1);
        writeH(LastProtocolVersion);
    }
}