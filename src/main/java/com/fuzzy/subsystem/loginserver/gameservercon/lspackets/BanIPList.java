package com.fuzzy.subsystem.loginserver.gameservercon.lspackets;

import javolution.util.FastList;
import com.fuzzy.subsystem.loginserver.IpManager;
import com.fuzzy.subsystem.util.BannedIp;

public class BanIPList extends ServerBasePacket {
    public BanIPList() {
        FastList<BannedIp> baniplist = IpManager.getInstance().getBanList();
        writeC(0x05);
        writeD(baniplist.size());
        for (BannedIp ip : baniplist) {
            writeS(ip.ip);
            writeS(ip.admin);
        }
        FastList.recycle(baniplist);
    }
}
