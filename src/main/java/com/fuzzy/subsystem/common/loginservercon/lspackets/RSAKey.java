package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.common.loginservercon.AttLS;

import java.util.logging.Logger;

public class RSAKey extends LoginServerBasePacket {
    private static final Logger log = Logger.getLogger(RSAKey.class.getName());

    public RSAKey(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        byte[] rsaKey = readB(128);
        getLoginServer().initRSA(rsaKey);
        getLoginServer().initCrypt();

        if (ConfigValue.enableDebugGsLs)
            log.info("GS Debug: RSAKey packet readed.");
    }
}