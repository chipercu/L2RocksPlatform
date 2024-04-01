package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.util.Strings;

/**
 * format ch
 * c: (id) 0xD0
 * h: (subid) 0x2F
 */
public class RequestExOlympiadObserverEnd extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(!activeChar.inObserverMode())
			return;
		NpcHtmlMessage reply = new NpcHtmlMessage(0);
		StringBuffer msg = new StringBuffer("");
		msg.append("!Grand Olympiad Game View:<br>");

		reply.setHtml(Strings.bbParse(msg.toString()));
		activeChar.sendPacket(reply);
	}
}