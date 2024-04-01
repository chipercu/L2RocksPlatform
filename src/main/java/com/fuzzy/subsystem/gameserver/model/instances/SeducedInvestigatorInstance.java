package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.NpcHtmlMessage;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class SeducedInvestigatorInstance extends L2MonsterInstance
{
	public SeducedInvestigatorInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		hasChatWindow = true;
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		player.sendPacket(new NpcHtmlMessage(player, this, "data/html/default/seducedinvestigator.htm", val));
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		if(player.isPlayable())
			return false;
		return true;
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isHealBlocked(boolean check_invul, boolean check_ref)
	{
		return true;
	}
}