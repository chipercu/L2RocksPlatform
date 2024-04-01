package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.Reflection;
import com.fuzzy.subsystem.gameserver.model.entity.DimensionalRift;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;

public class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(activeChar.isInParty())
		{
			if(Olympiad.isRegistered(activeChar) || activeChar.getOlympiadGame() != null || activeChar.isInOlympiadMode())
			{
				activeChar.sendPacket(Msg.FAILED_TO_WITHDRAW_FROM_THE_PARTY);
				return;
			}
			Reflection r = activeChar.getParty().getReflection();
			if(r != null && r instanceof DimensionalRift && activeChar.getReflection().equals(r))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestWithDrawalParty.Rift", activeChar));
			else if(r != null && (activeChar.isInCombat() || !activeChar.can_create_party))
				activeChar.sendPacket(Msg.FAILED_TO_WITHDRAW_FROM_THE_PARTY);
			else
				activeChar.getParty().oustPartyMember(activeChar);
		}
	}
}