package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.common.loginservercon.AttLS;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.extensions.network.L2GameClient;

/**
 * @Author: Death
 * @Date: 8/2/2007
 * @Time: 14:39:46
 */
public class ChangePasswordResponse extends LoginServerBasePacket {
    public ChangePasswordResponse(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        String account = readS();
        boolean changed = readD() == 1;

        L2GameClient client = getLoginServer().getCon().getAccountInGame(account);

        if (client == null)
            return;

        L2Player activeChar = client.getActiveChar();

        if (activeChar == null)
            return;

        if (changed) {
            String result = new CustomMessage("communityboard.cabinet.password.result.true", activeChar).toString();
            activeChar.setPasswordResult("<font color=\"33CC33\">" + result + "</font>");
            activeChar.sendMessage(result);
            Functions.show(new CustomMessage("scripts.commands.user.password.ResultTrue", activeChar), activeChar);
        } else {
            String result = new CustomMessage("communityboard.cabinet.password.result.false", activeChar).toString();
            activeChar.setPasswordResult("<font color=\"FF0000\">" + result + "</font>");
            activeChar.sendMessage(result);
            Functions.show(new CustomMessage("scripts.commands.user.password.ResultFalse", activeChar), activeChar);
        }
    }
}