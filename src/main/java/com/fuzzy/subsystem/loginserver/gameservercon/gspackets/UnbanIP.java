package com.fuzzy.subsystem.loginserver.gameservercon.gspackets;

import com.fuzzy.subsystem.loginserver.IpManager;
import com.fuzzy.subsystem.loginserver.gameservercon.AttGS;
import com.fuzzy.subsystem.loginserver.gameservercon.GSConnection;
import com.fuzzy.subsystem.loginserver.gameservercon.lspackets.BanIPList;
import com.fuzzy.subsystem.loginserver.gameservercon.lspackets.IpAction;

public class UnbanIP extends ClientBasePacket {
    public UnbanIP(byte[] decrypt, AttGS gameserver) {
        super(decrypt, gameserver);
    }

    @Override
    public void read() {
        String ip = readS();
        IpManager.getInstance().UnbanIp(ip);

        GSConnection.getInstance().broadcastPacket(new BanIPList());
        GSConnection.getInstance().broadcastPacket(new IpAction(ip, false, ""));
    }
}