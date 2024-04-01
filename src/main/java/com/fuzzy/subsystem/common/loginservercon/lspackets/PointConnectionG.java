package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.common.loginservercon.AttLS;

public class PointConnectionG extends LoginServerBasePacket {
    public PointConnectionG(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        String acc = readS();
        int point = readD();
    }
}
