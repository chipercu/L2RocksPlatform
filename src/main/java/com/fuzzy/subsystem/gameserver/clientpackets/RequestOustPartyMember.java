package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Party;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.Reflection;
import com.fuzzy.subsystem.gameserver.model.entity.DimensionalRift;
import com.fuzzy.subsystem.gameserver.model.entity.olympiad.Olympiad;

public class RequestOustPartyMember extends L2GameClientPacket
{
	//Format: cS
	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS(ConfigValue.cNameMaxLen);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2Party party = activeChar.getParty();
		if(party != null && party.isLeader(activeChar))
		{
			Reflection r = party.getReflection();
			L2Player oustPlayer = party.getPlayerByName(_name);
			
			if(Olympiad.isRegistered(activeChar) || Olympiad.isRegistered(oustPlayer) || oustPlayer != null && (oustPlayer.getOlympiadGame() != null || oustPlayer.isInOlympiadMode()))
			{
				activeChar.sendPacket(Msg.FAILED_TO_EXPEL_A_PARTY_MEMBER);
				return;
			}
			
			if(r != null && r instanceof DimensionalRift && oustPlayer != null && oustPlayer.getReflection().equals(r))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", activeChar));
			else if(r != null && !(r instanceof DimensionalRift))
				activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", activeChar));
			else
				party.oustPartyMember(_name);
		}
	}
}