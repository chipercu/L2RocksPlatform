package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestCrystallizeItemCancel extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //TODO
    }

    @Override
    protected void runImpl() {
        L2Player activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        activeChar.sendActionFailed();
    }
}
