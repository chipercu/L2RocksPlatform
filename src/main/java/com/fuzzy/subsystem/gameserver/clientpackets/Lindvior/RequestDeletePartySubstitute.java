package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * Created with IntelliJ IDEA.
 * User: Darvin
 * Date: 01.07.12
 * Time: 14:39
 */
public class RequestDeletePartySubstitute extends L2GameClientPacket {

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        L2Player player = getClient().getActiveChar();

        if (player == null)
            return;
    }
}
