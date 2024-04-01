package com.fuzzy.subsystem.loginserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.loginserver.LoginController;
import com.fuzzy.subsystem.loginserver.SessionKey;
import com.fuzzy.subsystem.loginserver.serverpackets.LoginFail.LoginFailReason;
import com.fuzzy.subsystem.loginserver.serverpackets.PlayOk;

/**
 * Fromat is ddc
 * d: first part of session id
 * d: second part of session id
 * c: server ID
 */
public class RequestServerLogin extends L2LoginClientPacket {
    private int _skey1;
    private int _skey2;
    private int _serverId;

    @Override
    public boolean readImpl() {
        if (getAvaliableBytes() >= 9) {
            _skey1 = readD();
            _skey2 = readD();
            _serverId = readC();
            return true;
        }
        return false;
    }

    @Override
    public void runImpl() {
        SessionKey sk = getClient().getSessionKey();

        // if we didnt showed the license we cant check these values
        if (!ConfigValue.ShowLicence || sk.checkLoginPair(_skey1, _skey2)) {
            if (LoginController.getInstance().isLoginPossible(getClient(), _serverId))
                getClient().sendPacket(new PlayOk(sk));
            else
                getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
        } else
            getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
    }
}