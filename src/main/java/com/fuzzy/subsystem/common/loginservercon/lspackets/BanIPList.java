package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.common.loginservercon.AttLS;
import com.fuzzy.subsystem.util.BannedIp;
import com.fuzzy.subsystem.util.GArray;

public class BanIPList extends LoginServerBasePacket {
    GArray<BannedIp> baniplist = new GArray<>();

    public BanIPList(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        int size = readD();
        for (int i = 0; i < size; i++) {
            BannedIp ip = new BannedIp();
            ip.ip = readS();
            ip.admin = readS();
            baniplist.add(ip);
        }
    }
}