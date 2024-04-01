package com.fuzzy.subsystem.common.loginservercon.gspackets;

import com.fuzzy.subsystem.common.loginservercon.AttLS;

public class BlowFishKey extends GameServerBasePacket {
    public BlowFishKey(byte[] data, AttLS loginServer) {
        writeC(0x00);
        if (data == null || data.length == 0) {
            writeD(0);
            return;
        }

        try {
            data = loginServer.getRsa().encryptRSA(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeD(data.length);
        writeB(data);
    }
}