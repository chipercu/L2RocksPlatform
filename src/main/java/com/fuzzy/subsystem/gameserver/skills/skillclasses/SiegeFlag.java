package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.idfactory.IdFactory;
import com.fuzzy.subsystem.gameserver.instancemanager.SiegeManager;
import com.fuzzy.subsystem.gameserver.instancemanager.ZoneManager;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.L2Zone.ZoneType;
import com.fuzzy.subsystem.gameserver.model.entity.siege.Siege;
import com.fuzzy.subsystem.gameserver.model.entity.siege.SiegeClan;
import com.fuzzy.subsystem.gameserver.model.instances.L2SiegeHeadquarterInstance;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncMul;
import com.fuzzy.subsystem.gameserver.tables.NpcTable;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Location;

import java.util.logging.Logger;

// 247, 326
public class SiegeFlag extends L2Skill
{
	protected static Logger _log = Logger.getLogger(SiegeFlag.class.getName());
	private final boolean _advanced;
	private final double _advancedMult;

	public SiegeFlag(StatsSet set)
	{
		super(set);
		_advanced = set.getBool("advancedFlag", false);
		_advancedMult = set.getDouble("advancedMultiplier", 1.);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(activeChar == null || !activeChar.isPlayer() || activeChar.isOutOfControl() || activeChar.getDuel() != null)
			return false;
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		L2Player player = (L2Player) activeChar;
		if(player.getClan() == null || !player.isClanLeader())
		{
			//_log.warning(player.toFullString() + " has " + toString() + ", but he isn't in a clan leader.");
			return false;
		}

		if(player.isInZone(ZoneType.siege_residense) || player.isInZone(ZoneType.RESIDENCE) || player.getReflectionId() != 0 && (player.getEventMaster() == null || !player.getEventMaster().siege_event))
		{
			activeChar.sendMessage("Flag can't be placed at castle.");
			return false;
		}

		if(player.getEventMaster() == null || player.getEventMaster()._ref == null || player.getEventMaster()._ref.getId() != player.getReflectionId())
		{
			Siege siege = SiegeManager.getSiege(activeChar, true);
			if(siege == null || siege.getAttackerClan(player.getClan()) == null)
			{
				activeChar.sendMessage("You must be an attacker to place a flag.");
				return false;
			}
		}
		else if(player.getEventMaster().siege_event && player.getEventMaster()._defender_clan != null && player.getEventMaster()._defender_clan.getClanId() == player.getClanId())
		{
			activeChar.sendMessage("You must be an attacker to place a flag.");
			return false;
		}

		int x = (int) (activeChar.getX() + 250 * Math.cos(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int y = (int) (activeChar.getY() + 250 * Math.sin(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int x1 = (int) (activeChar.getX() + 100 * Math.cos(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		int y1 = (int) (activeChar.getY() + 100 * Math.sin(activeChar.headingToRadians(activeChar.getHeading() - 32768)));
		
		boolean isWater = activeChar.isInWater() || L2World.isWater(x, y, GeoEngine.getNextZ(x1, y1));

		if(isWater || !GeoEngine.canSeeCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ() + 32, x, y, GeoEngine.getNextZ(x, y)+32, false, 0) || !(activeChar.getZ() - GeoEngine.getNextZ(x1, y1) < 42 && activeChar.getZ() - GeoEngine.getNextZ(x1, y1) > -42))
		{
			activeChar.sendMessage("Простите но здесь нельзя установить флаг.");
			return false;
		}
		else if(ConfigValue.EnableCustomZoneToOutpost && !ZoneManager.getInstance().checkIfInZone(ZoneType.allow_outpost, x1, y1, -1))
		{
			activeChar.sendMessage("Простите но здесь нельзя установить флаг.");
			return false;
		}
		return true;
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		L2Player player = (L2Player) activeChar;

		L2Clan clan = player.getClan();
		if(clan == null || !player.isClanLeader())
		{
			activeChar.sendMessage("You must be a clan leader to place a flag.");
			return;
		}

		SiegeClan siegeClan = null;
		if(player.getEventMaster() == null || player.getEventMaster()._ref == null || player.getEventMaster()._ref.getId() != player.getReflectionId())
		{
			Siege siege = SiegeManager.getSiege(activeChar, true);
			if(siege == null)
			{
				activeChar.sendMessage("You must be an attacker to place a flag.");
				return;
			}

			siegeClan = siege.getAttackerClan(clan);
		}
		else if(player.getEventMaster().siege_event && (player.getEventMaster()._defender_clan == null || player.getEventMaster()._defender_clan.getClanId() != player.getClanId()))
			siegeClan = player.getEventMaster().getSiegeClan(player);

		if(siegeClan == null)
		{
			activeChar.sendMessage("You must be an attacker to place a flag.");
			return;
		}
		else if(siegeClan.getHeadquarter() != null)
		{
			activeChar.sendMessage("You already has a flag.");
			return;
		}

		L2SiegeHeadquarterInstance flag = new L2SiegeHeadquarterInstance(player, IdFactory.getInstance().getNextId(), NpcTable.getTemplate(35062));

		if(_advanced)
			flag.addStatFunc(new FuncMul(Stats.p_max_hp, 0x50, flag, _advancedMult));

		flag.setReflection(activeChar.getReflection());
		flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp(), true);
		flag.setHeading(player.getHeading());

		// Ставим флаг перед чаром
		int x = (int) (player.getX() + 100 * Math.cos(player.headingToRadians(player.getHeading() - 32768)));
		int y = (int) (player.getY() + 100 * Math.sin(player.headingToRadians(player.getHeading() - 32768)));
		flag.spawnMe(new Location(x, y, GeoEngine.getNextZ(x, y)));

		siegeClan.setHeadquarter(flag);
	}
}