package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;

public class RequestCallToChangeClass extends L2GameClientPacket {

    @Override
    protected void readImpl() throws Exception {
    }

    @Override
    protected void runImpl() throws Exception {
		/*L2Player activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;
        if (activeChar.getVarB("GermunkusUSM"))
            return;
        int _cId = 0;
        for (ClassId Cl : ClassId.VALUES)
            if((Cl.isOfLevel(ClassLevel.AWAKED)) && (activeChar.getClassId().childOf(Cl)))
			{
                _cId = Cl.getId();
                break;
			}

        if (activeChar.isDead())
		{
            sendPacket(new ExShowScreenMessage(NpcString.YOU_CANNOT_TELEPORT_WHILE_YOU_ARE_DEAD, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, new String[0]));
            sendPacket(new ExCallToChangeClass(_cId, false));
            return;
        }

        activeChar.processQuestEvent("_10338_SeizeYourDestiny", "MemoryOfDisaster", null);*/
    }
}
