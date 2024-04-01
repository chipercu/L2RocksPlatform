package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.ExWaitWaitingSubStituteInfo;

public final class RequestRegistWaitingSubstitute extends L2GameClientPacket {
    private int _key;

    @Override
    protected void readImpl() {
        _key = readD();
    }

    @Override
    protected void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        switch (_key) {
            case 0:
                activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(ExWaitWaitingSubStituteInfo.WAITING_CANCEL));
                break;
            case 1:
                activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(ExWaitWaitingSubStituteInfo.WAITING_CANCEL));
                break;
            default:
                _log.info("RequestRegistWaitingSubstitute: key is " + _key);
                break;
        }
    }
}
