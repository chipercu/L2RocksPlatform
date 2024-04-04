package com.fuzzy.subsystem.loginserver.clientpackets;

import com.fuzzy.config.LoginConfig;
import com.fuzzy.subsystem.extensions.network.ReceivablePacket;
import com.fuzzy.subsystem.loginserver.L2LoginClient;

import java.util.logging.Logger;

/**
 *
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient> {
    public static Logger _log = Logger.getLogger(L2LoginClientPacket.class.getName());


    @Override
    protected final boolean read() {
        try {
            if (LoginConfig.DebugClientPackets)
                _log.info("Client send to LoginServer(" + getClient().toString() + ") packets: " + getType());

            return readImpl();
        } catch (Exception e) {
            _log.severe("ERROR READING: " + this.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            _log.severe("runImpl error: Client: " + getClient().toString());
            e.printStackTrace();
        }
        getClient().can_runImpl = true;
    }

    protected abstract boolean readImpl();

    protected abstract void runImpl() throws Exception;

    public String getType() {
        return "[C] " + getClass().getSimpleName();
    }
}
