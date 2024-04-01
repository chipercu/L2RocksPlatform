package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.config.*;
import com.fuzzy.subsystem.gameserver.instancemanager.SiegeManager;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Clan;
import com.fuzzy.subsystem.gameserver.model.L2DropData;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.base.Experience;
import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.model.entity.residence.ResidenceType;
import com.fuzzy.subsystem.gameserver.model.entity.siege.Siege;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Util;

public class L2SiegeGuardInstance extends L2MonsterInstance
{
	public L2SiegeGuardInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public int getAggroRange()
	{
		return 1200;
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		L2Player player = attacker.getPlayer();
		if(player == null)
			return false;
		L2Clan clan = player.getClan();
		if(clan != null && SiegeManager.getSiege(this, true) == clan.getSiege() && clan.isDefender())
			return false;
		Castle castle = getCastle();
		if(player.getTerritorySiege() > -1 && castle != null && player.getTerritorySiege() == castle.getId())
			return false;
		return true;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	private static final L2DropData EPAULETTE = new L2DropData(9912, 3, 6, 1000000, 1, 85);

	@Override
	public void doDie(L2Character killer)
	{
		if(ConfigValue.DropEpauleteOnlyReg)
		{
			Siege siege = SiegeManager.getSiege(this, true);
			if(killer != null)
			{
				L2Player player = killer.getPlayer();
				if(siege != null && player != null && siege.getSiegeUnit().getType() == ResidenceType.Fortress)
				{
					L2Clan clan = player.getClan();
					if(clan != null && siege == clan.getSiege() && !clan.isDefender())
					{
						L2Character topdam = getTopDamager(getAggroList());
						if(topdam == null)
							topdam = killer;
						double chancemod = Experience.penaltyModifier(calculateLevelDiffForDrop(topdam.getLevel(), false), 9);
						dropItem(player, EPAULETTE.getItemId(), Util.rollDrop(EPAULETTE.getMinDrop(), EPAULETTE.getMaxDrop(), EPAULETTE.getChance() * chancemod * ConfigValue.RateDropEpaulette * player.getRateItems(), false, player));
					}
				}
			}
		}
		super.doDie(killer);
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isSiegeGuard()
	{
		return true;
	}

	@Override
	public boolean can_drop_epaulette(L2Player player)
	{
		Siege siege = SiegeManager.getSiege(this, true);
		if(siege != null && player != null && siege.getSiegeUnit().getType() == ResidenceType.Fortress)
		{
			L2Clan clan = player.getClan();
			if(clan != null && siege == clan.getSiege() && !clan.isDefender())
				return true;
		}
		return ConfigValue.AlwaysDropEpaulette;
	}
}