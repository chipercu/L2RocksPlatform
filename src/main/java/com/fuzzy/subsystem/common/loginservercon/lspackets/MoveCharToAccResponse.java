package com.fuzzy.subsystem.common.loginservercon.lspackets;

import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.extensions.scripts.Functions;
import com.fuzzy.subsystem.common.loginservercon.AttLS;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.extensions.network.L2GameClient;

/**
 * @Author: Abaddon
 */
public class MoveCharToAccResponse extends LoginServerBasePacket {
    public MoveCharToAccResponse(byte[] decrypt, AttLS loginServer) {
        super(decrypt, loginServer);
    }

    @Override
    public void read() {
        String account = readS();
        int changed = readD();

        L2GameClient client = getLoginServer().getCon().getAccountInGame(account);

        if (client == null)
            return;

        L2Player activeChar = client.getActiveChar();

        if (activeChar == null)
            return;

        if (changed == 2)
            Functions.show(new CustomMessage("scripts.commands.user.account.ResultTrue", activeChar), activeChar);
        else if (changed == 1)
            Functions.show(new CustomMessage("scripts.commands.user.account.ResultNoTarget", activeChar), activeChar);
        else if (changed == 0)
            Functions.show(new CustomMessage("scripts.commands.user.account.ResultWrongPass", activeChar), activeChar);
    }
}