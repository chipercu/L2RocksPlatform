package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.common.loginservercon.AttLS;

public class KickPlayer extends LoginServerBasePacket {
    public KickPlayer(byte[] decrypt, AttLS loginserver) {
        super(decrypt, loginserver);
    }

    @Override
    public void read() {
        getLoginServer().getCon().kickAccountInGame(readS());
    }
}