package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.common.loginservercon.AttLS;
import com.fuzzy.subsystem.common.loginservercon.gspackets.TestConnectionResponse;

public class TestConnection extends LoginServerBasePacket {
    public TestConnection(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        //System.out.println("GS: request obtained");
        getLoginServer().sendPacket(new TestConnectionResponse());
    }
}